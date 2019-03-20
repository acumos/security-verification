/*-
* ===============LICENSE_START=======================================================
 * Acumos Apache-2.0
 * ===================================================================================
 * Copyright (C) 2019 Ericsson Software Technology AB. All rights reserved.
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
package org.acumos.licensemanager.service;

import java.lang.invoke.MethodHandles;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.domain.MLPRightToUse;
import org.acumos.cds.domain.MLPRtuReference;
import org.acumos.licensemanager.service.model.CreateRTURequest;
import org.acumos.licensemanager.service.model.IErrorResponse;
import org.acumos.licensemanager.service.model.ILicenseRequest;
import org.acumos.licensemanager.service.model.LicenseResponse;
import org.acumos.licensemanager.service.model.LicenseWorkflow;
import org.acumos.licensemanager.service.model.RTUResponse;
import org.acumos.licensemanager.service.model.VerifyLicenseRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestClientResponseException;

public class LicenseService {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ICommonDataServiceRestClient dataClient;

    public LicenseService(ICommonDataServiceRestClient dataClient) {
        this.dataClient = dataClient;
    }

    private void addRtuRefs(CreateRTURequest request, MLPRightToUse rtu) {
        Set<MLPRtuReference> rtuReferences = new HashSet<MLPRtuReference>();
        String[] rtuRefStr = request.getRTURefs();
        for (String rtuRef : rtuRefStr) {
            rtuReferences.add(new MLPRtuReference(rtuRef));
        }
        rtu.setRtuReferences(rtuReferences);
    }

    private boolean updateRightToUse(MLPRightToUse rtu, RTUResponse response) {
        try {
            dataClient.updateRightToUse(rtu);
        } catch (RestClientResponseException ex) {
            logger.error("updateRightToUse failed, server reports: {}", ex.getResponseBodyAsString());
            response.addError(ex);
            return false;
        }
        return true;
    }

    private List<MLPRightToUse> getRightToUses(ILicenseRequest request, IErrorResponse response) {
        List<MLPRightToUse> rtus = new ArrayList<MLPRightToUse>();
        try {
            rtus = dataClient.getRightToUses(request.getSolutionId(), request.getUserId());
        } catch (RestClientResponseException ex) {
            logger.error("getRightToUses failed, server reports: {}", ex.getResponseBodyAsString());
            response.addError(ex);
        }
        return rtus;
    }



    /**
     * CreateRTU
     * 
     * @param request
     * @return the response of the create rtu request
     * 
     */
    public RTUResponse createRTU(CreateRTURequest request) {
        RTUResponse response = new RTUResponse();
        response.setRequest(request);
        // check if rtu reference already exists for solution id + userid
        List<MLPRightToUse> rtus = getRightToUses(request, response);
        // in Boreas only expect 1 rtu
        if (rtus != null && rtus.size() > 0) {
            // in Boreas we will not update RTU if added for solution and user
            for (MLPRightToUse rtu : rtus) {
                addRtuRefs(request, rtu);
                rtu.setSite(request.isSiteWide());
                rtu.setRtuId(request.getRTUId());
                rtu.setModified(Instant.now());

                if (updateRightToUse(rtu, response)) {
                    response.addRtu(rtu);
                    response.setUpdated(true);
                    response.setCreated(false);
                }

            }

            return response;
        } else {
            MLPRightToUse rightToUse = new MLPRightToUse(request.getSolutionId(), request.isSiteWide());
            rightToUse.setRtuId(request.getRTUId());
            addRtuRefs(request, rightToUse);
            rightToUse.setCreated(Instant.now());

            try {
                dataClient.createRightToUse(rightToUse);
            } catch (RestClientResponseException ex) {
                logger.error("createRightToUse failed, server reports: {}", ex.getResponseBodyAsString());
                response.addError(ex);
                return response;
            }

            if (updateRightToUse(rightToUse, response)) {
                response.setCreated(true);
                response.addRtu(rightToUse);
            }

        }
        return response;
    }



    public LicenseResponse verifyRTU(VerifyLicenseRequest request) {
        // currently only using workflow for dummy response
        LicenseResponse response = new LicenseResponse();

        boolean rightToUseFlag = false;
        List<MLPRightToUse> rightToUse = getRightToUses(request, response);

        // If there is a right to use (any for solution/user both download and deploy
        // are allowed in Boreas)
        if (rightToUse != null && !rightToUse.isEmpty()) {
            rightToUseFlag = true;
        }
        for (String workflow : request.getWorkflow()) {
            // we will get all rtu for user and solution id from CDS fake that here
            LicenseWorkflow workflowVal = LicenseWorkflow.valueOf(workflow.toLowerCase());
            switch (workflowVal) {
            case download:
                response.addWorkflow(workflow, rightToUseFlag);
                break;
            case deploy:
                response.addWorkflow(workflow, rightToUseFlag);
                break;
            default:
                logger.error("unimplemented license workflow {}", workflow);
                break;
            }
        }
        return response;

    }

}
