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
import org.acumos.licensemanager.client.model.ICommonLicenseRequest;
import org.acumos.licensemanager.client.model.ICreateRtu;
import org.acumos.licensemanager.exceptions.RightToUseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestClientResponseException;

/** Internal class for working with right to use controller in the common data service. */
class LicenseDataUtils {
  /** Logger for any exceptions that happen while creating a RTU with CDS. */
  private static final Logger LOGGER =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  /** No not allow for utility class from being instantiated. */
  protected LicenseDataUtils() {
    // prevents calls from subclass
    throw new UnsupportedOperationException();
  }

  /**
   * Converts rtu refs as string to MLPRtuReference then adds them to a RTU.
   *
   * @param dataClient a {@link org.acumos.cds.client.ICommonDataServiceRestClient} object.
   * @param request a {@link org.acumos.licensemanager.client.model.ICreateRtu} object.
   * @param rtu a {@link org.acumos.cds.domain.MLPRightToUse} object.
   */
  protected static void addRtuRefs(final ICreateRtu request, final MLPRightToUse rtu) {
    Set<MLPRtuReference> rtuReferences = new HashSet<MLPRtuReference>();
    List<String> rtuRefStr = request.getRtuRefs();
    for (String rtuRef : rtuRefStr) {
      rtuReferences.add(new MLPRtuReference(rtuRef));
    }
    rtu.setRtuReferences(rtuReferences);
  }

  /**
   * Wrapper method for updating the right to use. Used primarily to log and capture any {@link
   * org.springframework.web.client.RestClientResponseException} that is returned from the CDS
   * client.
   *
   * @param dataClient a {@link org.acumos.cds.client.ICommonDataServiceRestClient} object.
   * @param rtu a {@link org.acumos.cds.domain.MLPRightToUse} object.
   * @return a boolean.
   * @throws org.acumos.licensemanager.exceptions.RightToUseException if any.
   */
  protected static boolean updateRightToUse(
      final ICommonDataServiceRestClient dataClient, final MLPRightToUse rtu)
      throws RightToUseException {
    try {
      dataClient.updateRightToUse(rtu);
    } catch (RestClientResponseException ex) {
      LOGGER.error("updateRightToUse failed, server reports: {}", ex.getResponseBodyAsString());
      throw new RightToUseException("updateRightToUse failed", ex);
    }
    return true;
  }

  /**
   * Wrapper method for getting the right to use for an existing solution. Used primarily to log and
   * capture any {@link org.springframework.web.client.RestClientResponseException} that is returned
   * from the CDS client.
   *
   * @param dataClient a {@link org.acumos.cds.client.ICommonDataServiceRestClient} object.
   * @param request a {@link org.acumos.licensemanager.client.model.ICommonLicenseRequest} object.
   * @return a {@link java.util.List} object.
   * @throws org.acumos.licensemanager.exceptions.RightToUseException if any.
   */
  protected static List<MLPRightToUse> getRightToUses(
      final ICommonDataServiceRestClient dataClient, final ICommonLicenseRequest request)
      throws RightToUseException {
    List<MLPRightToUse> rtus = new ArrayList<MLPRightToUse>(0);
    try {
      // only supporting one userid at a time
      rtus = dataClient.getRightToUses(request.getSolutionId(), request.getUserIds().get(0));
    } catch (RestClientResponseException ex) {
      LOGGER.error("getRightToUses failed, server reports: {}", ex.getResponseBodyAsString());
      throw new RightToUseException("getRightToUses Failed", ex);
    }
    return rtus;
  }

  /**
   * Query the CDS client for any site wide solution RTU.
   *
   * @param dataClient a {@link org.acumos.cds.client.ICommonDataServiceRestClient} object.
   * @param request a {@link org.acumos.licensemanager.client.model.ICommonLicenseRequest} object.
   * @return a {@link java.util.List} object.
   * @throws org.acumos.licensemanager.exceptions.RightToUseException if any.
   */
  protected static List<MLPRightToUse> getSitewideSolutionRtu(
      final ICommonDataServiceRestClient dataClient, final ICommonLicenseRequest request)
      throws RightToUseException {
    List<MLPRightToUse> rtus = new ArrayList<MLPRightToUse>();
    try {
      Map<String, Object> queryParams = new HashMap<String, Object>();
      queryParams.put("site", true);
      queryParams.put("solutionId", request.getSolutionId());

      RestPageResponse<MLPRightToUse> rtuPageResponse =
          dataClient.searchRightToUses(queryParams, false, null);
      rtus = rtuPageResponse.getContent();
    } catch (RestClientResponseException ex) {
      LOGGER.error("getRightToUses failed, server reports: {}", ex.getResponseBodyAsString());
      throw new RightToUseException("getRightToUsesFailed", ex);
    }
    return rtus;
  }
}
