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
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * This file is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ===============LICENSE_END==================================================
 */

package org.acumos.licensemanager.client;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.domain.MLPRightToUse;
import org.acumos.cds.domain.MLPRtuReference;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.licensemanager.client.model.ICreateRTURequest;
import org.acumos.licensemanager.client.model.ICommonLicenseRequest;
import org.acumos.licensemanager.exceptions.RightToUseException;
import org.acumos.licensemanager.client.model.CreatedRtu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestClientResponseException;

/**
 * Internal class for working with right to use controller in the
 * common data service.
 */
class LicenseCDSUtil {
  /**
   * Logger for any exceptions that happen while creating a RTU with CDS.
   */
  private static final Logger LOGGER =
    LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  /**
   * No not allow for utility class from being instantiated.
   */
  protected LicenseCDSUtil() {
    // prevents calls from subclass
    throw new UnsupportedOperationException();
  }
  /**
   * <p>
   * Converts rtu refs as string to MLPRtuReference
   * then adds them to a RTU.
   * </p>
   *
   * @param dataClient a
   *  {@link org.acumos.cds.client.ICommonDataServiceRestClient} object.
   * @param request a
   *  {@link org.acumos.licensemanager.client.model.ICreateRTURequest} object.
   * @param rtu a {@link org.acumos.cds.domain.MLPRightToUse} object.
   */
  protected static void addRtuRefs(
    final ICommonDataServiceRestClient dataClient,
    final ICreateRTURequest request,
    final MLPRightToUse rtu) {
    if (dataClient == null) {
      throw new IllegalArgumentException("dataClient was null, "
        + "expected value");
    }
    if (request == null) {
      throw new IllegalArgumentException("request was null, expected value");
    }
    if (rtu == null) {
      throw new IllegalArgumentException("rtu was null, expected value");
    }
    Set<MLPRtuReference> rtuReferences = new HashSet<MLPRtuReference>();
    List<String> rtuRefStr = request.getRTURefs();
    for (String rtuRef : rtuRefStr) {
      rtuReferences.add(new MLPRtuReference(rtuRef));
    }
    rtu.setRtuReferences(rtuReferences);
  }

  /**
   * <p>
   * Wrapper method for updating the right to use.
   * Used primarily to log and capture any
   *  {@link org.springframework.web.client.RestClientResponseException}
   * that is returned from the CDS client.
   * </p>
   *
   * @param dataClient a
   *  {@link org.acumos.cds.client.ICommonDataServiceRestClient} object.
   * @param rtu a
   *  {@link org.acumos.cds.domain.MLPRightToUse} object.
   * @param response a
   *  {@link org.acumos.licensemanager.client.model.CreatedRtu} object.
   * @return a boolean.
   * @throws RightToUseException when not able to update right to use
   */
  protected static boolean updateRightToUse(
    final ICommonDataServiceRestClient dataClient,
    final MLPRightToUse rtu,
    final CreatedRtu response) throws RightToUseException {
    try {
      dataClient.updateRightToUse(rtu);
    } catch (RestClientResponseException ex) {
      LOGGER.error("updateRightToUse failed, server reports: {}",
        ex.getResponseBodyAsString());
      throw new RightToUseException("updateRightToUse failed", ex);
    }
    return true;
  }


  /**
   * <p>
   * Wrapper method for getting  the right to use for an existing solution.
   * Used primarily to log and capture any
   * {@link org.springframework.web.client.RestClientResponseException}
   * that is returned from the CDS client.
   * </p>
   *
   * @param dataClient
   *  a {@link org.acumos.cds.client.ICommonDataServiceRestClient} object.
   * @param request
   *  a {@link org.acumos.licensemanager.client.model.ICommonLicenseRequest}
   *  object.
   * @return a {@link java.util.List} object.
   * @throws RightToUseException when cannot get right to uses
   */
  protected static List<MLPRightToUse> getRightToUses(
    final ICommonDataServiceRestClient dataClient,
    final ICommonLicenseRequest request) throws RightToUseException {
    List<MLPRightToUse> rtus = new ArrayList<MLPRightToUse>(0);
    try {
      // TODO support multiple users
        rtus = dataClient.getRightToUses(request.getSolutionId(),
          request.getUserIds().get(0));
    } catch (RestClientResponseException ex) {
        LOGGER.error("getRightToUses failed, server reports: {}",
          ex.getResponseBodyAsString());
        throw new RightToUseException("getRightToUses Failed", ex);
    }
    return rtus;
  }

  /**
   * <p>
   * Query the CDS client for any site wide solution RTU.
   * </p>
   *
   * @param dataClient a
   *  {@link org.acumos.cds.client.ICommonDataServiceRestClient} object.
   * @param request a
   *  {@link org.acumos.licensemanager.client.model.ICommonLicenseRequest}
   *  object.
   * @return a {@link java.util.List} object.
   * @throws RightToUseException when not able to get site wide right to use
   */
  protected static List<MLPRightToUse> getSitewideSolutionRTU(
      final ICommonDataServiceRestClient dataClient,
      final ICommonLicenseRequest request) throws RightToUseException {
    List<MLPRightToUse> rtus = new ArrayList<MLPRightToUse>();
    try {
      Map<String, Object> queryParams = new HashMap<String, Object>();
      queryParams.put("site", true);
      queryParams.put("solutionId", request.getSolutionId());

      RestPageResponse<MLPRightToUse> rtuPageResponse =
        dataClient.searchRightToUses(queryParams, false, null);
      rtus = rtuPageResponse.getContent();
    } catch (RestClientResponseException ex) {
        LOGGER.error("getRightToUses failed, server reports: {}",
          ex.getResponseBodyAsString());
        throw new RightToUseException("getRightToUsesFailed", ex);
    }
    return rtus;
  }
}
