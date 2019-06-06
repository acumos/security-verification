/*-
 * ===============LICENSE_START================================================
 * Acumos Apache-2.0
 * ============================================================================
 * Copyright (C) 2019 Nordix Foundation.
 * ============================================================================
 * This Acumos software file is distributed by Nordix Foundation
 * under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * This file is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ===============LICENSE_END==================================================
 **/

package org.acumos.licensemanager.client;

import java.lang.invoke.MethodHandles;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.domain.MLPRightToUse;
import org.acumos.cds.domain.MLPRtuReference;
import org.acumos.cds.domain.MLPUser;
import org.acumos.licensemanager.client.model.CreatedRtu;
import org.acumos.licensemanager.client.model.ICreateRtu;
import org.acumos.licensemanager.client.model.ICreatedRtuResponse;
import org.acumos.licensemanager.client.model.ILicenseCreator;
import org.acumos.licensemanager.client.model.RtuSearchRequest;
import org.acumos.licensemanager.exceptions.RightToUseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestClientResponseException;

/**
 * LicenseCreator Library will create a right to use for either a solution and can be added for the
 * entire site or for a specific user.
 */
public class LicenseCreator implements ILicenseCreator {
  /** Logger for any exceptions that happen while creating a RTU with CDS. */
  private static final Logger LOGGER =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  /** dataClient must be provided by consumer of this library. */
  private final ICommonDataServiceRestClient dataClient;

  /**
   * The implementation of the CDS is required to enable this library.
   *
   * @param dataServiceClient a {@link org.acumos.cds.client.ICommonDataServiceRestClient} object.
   */
  public LicenseCreator(final ICommonDataServiceRestClient dataServiceClient) {
    this.dataClient = dataServiceClient;
  }

  @Override
  public final ICreatedRtuResponse createRtu(final ICreateRtu request) throws RightToUseException {

    if (request == null) {
      throw new IllegalArgumentException("request is not defined");
    }
    if (request.getSolutionId() == null) {
      throw new IllegalArgumentException("request solution id is not defined");
    }
    if (request.getUserIds() == null && !request.isSiteWide()) {
      throw new IllegalArgumentException("request userId or " + "siteWide is not defined");
    }

    // if (request.getUserIds().size() > 1) {
    //   throw new IllegalArgumentException(
    //       "only allow one user id was passed" + request.getUserIds().size());
    // }

    CreatedRtu response = new CreatedRtu();
    response.setRequest(request);
    // check if rtu reference already exists for solution id + userid
    RtuSearchRequest searchRequest = new RtuSearchRequest();
    searchRequest.setSolutionId(request.getSolutionId());
    List<MLPRightToUse> rtus = LicenseDataUtils.getRightToUsesNoFilter(dataClient, searchRequest);
    // in Boreas only expect 1 rtu
    if (rtus != null && !rtus.isEmpty()) {

      // in Boreas we will not update RTU if added for solution and user
      // update if rtu ref id matches
      Set<MLPRtuReference> rtuReferences = rtus.get(0).getRtuReferences();
      String ref = rtuReferences.iterator().next().getRef();
      if (rtus.size() == 1
          && rtuReferences.size() == 1
          && !ref.equals(request.getRtuRefs().get(0))) {
        throw new RightToUseException(
            "Rtu already exists for solution id" + request.getSolutionId() + "with rtuId " + ref,
            request.getSolutionId(),
            rtus);
      } else {
        updateRtu(request, response, rtus);
        removeRtuFromUsers(request, response);
        grantRtuToUsers(request, response);
      }

    } else {
      createRightToUse(request, response);
      grantRtuToUsers(request, response);
    }
    return response;
  }

  private void removeRtuFromUsers(ICreateRtu request, CreatedRtu response)
      throws RightToUseException {
    // remove rtu from user that are not in request list
    // get the list of users for the rtu being updated
    // if user is not in creation list remove user
    List<String> userIds = request.getUserIds();
    List<MLPUser> rtuUsers = new ArrayList<MLPUser>();
    Long rtuId = response.getRtus().get(0).getRtuId();
    List<MLPUser> rtuUsers2 = getRtuUsers(rtuId);
    if (rtuUsers2 == null) {
      return;
    }
    rtuUsers.addAll(rtuUsers2);

    for (MLPUser rtuUser : rtuUsers) {
      String userId = rtuUser.getUserId();
      boolean match = userIds.stream().anyMatch(str -> str.trim().equals(userId));
      if (!match) {
        removeUserFromRtu(rtuId, userId);
      }
    }
  }

  private void removeUserFromRtu(Long rtuId, String userId) throws RightToUseException {
    try {
      // allow user to have access
      dataClient.dropUserFromRtu(userId, rtuId);
    } catch (RestClientResponseException ex) {
      LOGGER.error(
          "allowSolutionUserAccess failed, server reports: {}", ex.getResponseBodyAsString());
      throw new RightToUseException("allowSolutionUserAccess failed", ex);
    }
  }

  private void grantRtuToUsers(final ICreateRtu request, CreatedRtu response)
      throws RightToUseException {
    if (!request.getUserIds().isEmpty() && !response.getRtus().isEmpty()) {
      for (MLPRightToUse rtu : response.getRtus()) {
        for (String userId : request.getUserIds()) {
          grantRtuToUser(userId, rtu.getRtuId());
        }
      }
    }
  }

  /**
   * Update Right to use if an existing RTU exists will create a new RTU.
   *
   * @param request creation request
   * @param response responds with information about the rtu creation
   * @throws RightToUseException when then creation of RTU was unsuccessful
   */
  private void createRightToUse(final ICreateRtu request, final CreatedRtu response)
      throws RightToUseException {
    MLPRightToUse rightToUse = new MLPRightToUse(request.getSolutionId(), request.isSiteWide());
    LicenseDataUtils.addRtuRefs(request, rightToUse);
    rightToUse.setCreated(Instant.now());

    try {
      // create rtu for the solution
      MLPRightToUse completeRtu = dataClient.createRightToUse(rightToUse);
      response.addRtu(completeRtu);
      response.setCreated(true);
    } catch (RestClientResponseException ex) {
      LOGGER.error("createRightToUse failed, server reports: {}", ex.getResponseBodyAsString());
      throw new RightToUseException("createRightToUse failed", ex)
          .setSolutionId(request.getSolutionId());
    }
  }

  /**
   * Update Right to use if an existing RTU exists will create a new RTU.
   *
   * @param request creation request
   * @param response responds with information about the rtu creation
   * @throws RightToUseException when then creation of RTU was unsuccessful
   */
  private void grantRtuToUser(final String userId, final Long rtuId) throws RightToUseException {

    try {
      // allow user to have access
      dataClient.addUserToRtu(userId, rtuId);
    } catch (RestClientResponseException ex) {
      LOGGER.error(
          "allowSolutionUserAccess failed, server reports: {}", ex.getResponseBodyAsString());
      throw new RightToUseException("allowSolutionUserAccess failed", ex);
    }
  }

  private List<MLPUser> getRtuUsers(final Long rtuId) throws RightToUseException {

    try {
      // allow user to have access
      return dataClient.getRtuUsers(rtuId);
    } catch (RestClientResponseException ex) {
      LOGGER.error(
          "allowSolutionUserAccess failed, server reports: {}", ex.getResponseBodyAsString());
      throw new RightToUseException("allowSolutionUserAccess failed", ex);
    }
  }

  /**
   * Internal method to update the rtu.
   *
   * @param request creation request
   * @param response responds with information about the rtu creation
   * @param rtus list of rtus to process
   * @throws RightToUseException when then creation of RTU was unsuccessful
   */
  private void updateRtu(
      final ICreateRtu request, final CreatedRtu response, final List<MLPRightToUse> rtus)
      throws RightToUseException {
    for (MLPRightToUse rtu : rtus) {
      LicenseDataUtils.addRtuRefs(request, rtu);
      rtu.setSite(request.isSiteWide());
      rtu.setModified(Instant.now());
      if (LicenseDataUtils.updateRightToUse(dataClient, rtu)) {
        response.addRtu(rtu);
        response.setUpdated(true);
        response.setCreated(false);
      }
    }
  }
}
