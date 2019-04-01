/*-
* ===============LICENSE_START================================================
 * Acumos Apache-2.0
 * ============================================================================
 * Copyright (C) 2019 Nordix Foundation.
 * ============================================================================
 * This Acumos software file is distributed by AT&T and Tech Mahindra
 * under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
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
import java.util.List;

import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.domain.MLPRightToUse;
import org.acumos.licensemanager.client.model.ICreateRTURequest;
import org.acumos.licensemanager.client.model.ICreatedRtu;
import org.acumos.licensemanager.client.model.ILicenseCreator;
import org.acumos.licensemanager.client.model.CreatedRtu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestClientResponseException;

/**
 * LicenseCreator Library will create a right to use for either a solution and
 * can be added for the entire site or for a specific user.
 *
 * @version 0.0.2
 */
public class LicenseCreator implements ILicenseCreator {
  /**
   * Logger for any exceptions that happen while
   * creating a RTU with CDS.
   */
  private static final Logger LOGGER = LoggerFactory
    .getLogger(MethodHandles.lookup().lookupClass());
  /**
   * dataClient must be provided by consumer of this library.
   */
  private final ICommonDataServiceRestClient dataClient;

  /**
   * The implementation of the CDS is required to enable this library.
   *
   * @param dataServiceClient a
   *             {@link org.acumos.cds.client.ICommonDataServiceRestClient}
   *             object.
   */
  public LicenseCreator(final ICommonDataServiceRestClient dataServiceClient) {
    this.dataClient = dataServiceClient;
  }

  @Override
  public final ICreatedRtu createRTU(final ICreateRTURequest request) {

    if (request == null) {
      throw new IllegalArgumentException("request is not defined");
    }
    if (request.getSolutionId() == null) {
      throw new IllegalArgumentException("request solution id is not defined");
    }
    if (request.getUserIds().isEmpty() && !request.isSiteWide()) {
      throw new IllegalArgumentException("request userId or "
        + "siteWide is not defined");
    }

    if (request.getUserIds().size() > 1) {
      throw new IllegalArgumentException("only allow one user id was passed"
        + request.getUserIds().size());
    }


    CreatedRtu response = new CreatedRtu();
    response.setRequest(request);
    // check if rtu reference already exists for solution id + userid

    List<MLPRightToUse> rtus = LicenseCDSUtil
      .getRightToUses(dataClient, request, response);
    // in Boreas only expect 1 rtu
    if (rtus != null && !rtus.isEmpty()) {
      // in Boreas we will not update RTU if added for solution and user
      for (MLPRightToUse rtu : rtus) {
        LicenseCDSUtil.addRtuRefs(dataClient, request, rtu);
        rtu.setSite(request.isSiteWide());
        rtu.setRtuId(request.getRTUId());
        rtu.setModified(Instant.now());

        if (LicenseCDSUtil.updateRightToUse(dataClient, rtu, response)) {
          response.addRtu(rtu);
          response.setUpdated(true);
          response.setCreated(false);
        }

      }
      return response;
    } else {
      MLPRightToUse rightToUse = new MLPRightToUse(request.getSolutionId(),
          request.isSiteWide());
      rightToUse.setRtuId(request.getRTUId());
      LicenseCDSUtil.addRtuRefs(dataClient, request, rightToUse);
      rightToUse.setCreated(Instant.now());

      try {
        MLPRightToUse completeRtu = dataClient.createRightToUse(rightToUse);
        MLPRightToUse rtu;
        if (completeRtu != null) {
          rtu = completeRtu;
        } else {
          rtu = rightToUse;
        }
        response.addRtu(rtu);
        response.setCreated(true);
      } catch (RestClientResponseException ex) {
        LOGGER.error("createRightToUse failed, server reports: {}",
           ex.getResponseBodyAsString());
        response.addError(ex);
        return response;
      }

    }
    return response;
  }

}
