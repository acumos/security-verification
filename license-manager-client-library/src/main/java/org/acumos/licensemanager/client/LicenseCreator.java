/*-
* ===============LICENSE_START=======================================================
 * Acumos Apache-2.0
 * ===================================================================================
 * Copyright (C) 2019 Nordix Foundation.
 * ===================================================================================
 * This Acumos software file is distributed by AT&T and Tech Mahindra
 *  under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  This file is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * ===============LICENSE_END=========================================================
*/
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
 * LicenseCreator Library will create a right to use for
 * either a solution and can be added for the entire site
 * or for a specific user.
 *
 * @author est.tech
 * @version 0.0.2
 */
public class LicenseCreator implements ILicenseCreator {
  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final ICommonDataServiceRestClient dataClient;

  /**
   * The implementation of the CDS is required to enable this library
   *
   * @param dataClient a {@link org.acumos.cds.client.ICommonDataServiceRestClient} object.
   */
  public LicenseCreator(ICommonDataServiceRestClient dataClient) {
    this.dataClient = dataClient;
  }

  /** {@inheritDoc} */
  @Override
  public ICreatedRtu createRTU(ICreateRTURequest request) {

    if (request == null) {
      throw new IllegalArgumentException("request is not defined");
    }
    if (request.getSolutionId() == null) {
      throw new IllegalArgumentException("request solution id is not defined");
    }
    if (request.getUserId() == null && request.isSiteWide() == false) {
      throw new IllegalArgumentException("request userId or siteWide is not defined");
    }

    CreatedRtu response = new CreatedRtu();
    response.setRequest(request);
    // check if rtu reference already exists for solution id + userid

    List<MLPRightToUse> rtus = LicenseCDSUtil.getRightToUses(dataClient, request, response);
    // in Boreas only expect 1 rtu
    if (rtus != null && rtus.isEmpty() == false) {
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
      MLPRightToUse rightToUse = new MLPRightToUse(request.getSolutionId(), request.isSiteWide());
      rightToUse.setRtuId(request.getRTUId());
      LicenseCDSUtil.addRtuRefs(dataClient, request, rightToUse);
      rightToUse.setCreated(Instant.now());

      try {
        MLPRightToUse completeRTU =dataClient.createRightToUse(rightToUse);
        MLPRightToUse rtu = completeRTU != null ? completeRTU : rightToUse;
        response.addRtu(rtu);
        response.setCreated(true);
      } catch (RestClientResponseException ex) {
        logger.error("createRightToUse failed, server reports: {}", ex.getResponseBodyAsString());
        response.addError(ex);
        return response;
      }


    }
    return response;
  }

}
