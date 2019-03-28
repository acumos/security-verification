/*-
 * ===============LICENSE_START=======================================================
 * Acumos Apache-2.0
 * ===================================================================================
 * Copyright (C) 2019 Ericsson Software Technology AB. All rights reserved.
 * ===================================================================================
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
 * ===============LICENSE_END=========================================================
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
import org.acumos.licensemanager.client.model.IErrorResponse;
import org.acumos.licensemanager.client.model.ILicenseRequest;
import org.acumos.licensemanager.client.model.CreatedRtu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestClientResponseException;

/**
 * Internal class for interfacing with CDS
 */
class LicenseCDSUtil {

  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  /**
   * <p>addRtuRefs.</p>
   *
   * @param dataClient a {@link org.acumos.cds.client.ICommonDataServiceRestClient} object.
   * @param request a {@link org.acumos.licensemanager.client.model.ICreateRTURequest} object.
   * @param rtu a {@link org.acumos.cds.domain.MLPRightToUse} object.
   */
  protected static void addRtuRefs(ICommonDataServiceRestClient dataClient, ICreateRTURequest request, MLPRightToUse rtu) {
    if(dataClient == null){
      throw new IllegalArgumentException("dataClient was null, expected value");
    }
    if(request == null){
      throw new IllegalArgumentException("request was null, expected value");
    }
    if(rtu == null){
      throw new IllegalArgumentException("rtu was null, expected value");
    }
    Set<MLPRtuReference> rtuReferences = new HashSet<MLPRtuReference>();
    String[] rtuRefStr = request.getRTURefs();
    for (String rtuRef : rtuRefStr) {
      rtuReferences.add(new MLPRtuReference(rtuRef));
    }
    rtu.setRtuReferences(rtuReferences);
  }

  /**
   * <p>updateRightToUse.</p>
   *
   * @param dataClient a {@link org.acumos.cds.client.ICommonDataServiceRestClient} object.
   * @param rtu a {@link org.acumos.cds.domain.MLPRightToUse} object.
   * @param response a {@link org.acumos.licensemanager.client.model.CreatedRtu} object.
   * @return a boolean.
   */
  protected static boolean updateRightToUse(ICommonDataServiceRestClient dataClient, MLPRightToUse rtu, CreatedRtu response) {
    try {
      dataClient.updateRightToUse(rtu);
    } catch (RestClientResponseException ex) {
      logger.error("updateRightToUse failed, server reports: {}", ex.getResponseBodyAsString());
      response.addError(ex);
      return false;
    }
    return true;
  }


  /**
   * <p>getRightToUses.</p>
   *
   * @param dataClient a {@link org.acumos.cds.client.ICommonDataServiceRestClient} object.
   * @param request a {@link org.acumos.licensemanager.client.model.ILicenseRequest} object.
   * @param response a {@link org.acumos.licensemanager.client.model.IErrorResponse} object.
   * @return a {@link java.util.List} object.
   */
  protected static List<MLPRightToUse> getRightToUses(ICommonDataServiceRestClient dataClient, ILicenseRequest request, IErrorResponse response) {
    List<MLPRightToUse> rtus = new ArrayList<MLPRightToUse>(0);
    try {
        rtus = dataClient.getRightToUses(request.getSolutionId(), request.getUserId());
    } catch (RestClientResponseException ex) {
        logger.error("getRightToUses failed, server reports: {}", ex.getResponseBodyAsString());
        response.addError(ex);
    }
    return rtus;
  }

  /**
   * <p>getSitewideSolutionRTU.</p>
   *
   * @param dataClient a {@link org.acumos.cds.client.ICommonDataServiceRestClient} object.
   * @param request a {@link org.acumos.licensemanager.client.model.ILicenseRequest} object.
   * @param response a {@link org.acumos.licensemanager.client.model.IErrorResponse} object.
   * @return a {@link java.util.List} object.
   */
  protected static List<MLPRightToUse> getSitewideSolutionRTU(ICommonDataServiceRestClient dataClient, ILicenseRequest request, IErrorResponse response) {
    List<MLPRightToUse> rtus = new ArrayList<MLPRightToUse>();
    try {
      Map<String,Object> queryParams = new HashMap<String,Object>();
      queryParams.put("site", true);
      queryParams.put("solutionId", request.getSolutionId());

      RestPageResponse<MLPRightToUse> rtuPageResponse = dataClient.searchRightToUses(queryParams, false, null );
      rtus = rtuPageResponse.getContent();
    } catch (RestClientResponseException ex) {
        logger.error("getRightToUses failed, server reports: {}", ex.getResponseBodyAsString());
        response.addError(ex);
    }
    return rtus;
  }
}
