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
* http://www.apache.org/licenses/LICENSE-2.0
*
* This file is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
* ===============LICENSE_END=========================================================
*/
package org.acumos.licensemanager.service;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.domain.MLPRightToUse;
import org.acumos.cds.domain.MLPRtuReference;
import org.acumos.licensemanager.service.create.CreateRTURequest;
import org.acumos.licensemanager.service.create.RTUResponse;

public class LicenseService {

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
    
    /**
     * CreateRTU
     */
    public RTUResponse createRTU(CreateRTURequest request) {
        RTUResponse response = new RTUResponse(request);
        // check if rtu reference already exists for solution id + userid
        List<MLPRightToUse> rtus = dataClient.getRightToUses(request.getSolutionId(), request.getUserId());
        // in Boreas only expect 1 rtu
        if (rtus != null && rtus.size() > 0) {
            // in Boreas we will not update RTU if added for solution and user
            for (MLPRightToUse rtu : rtus) {
                addRtuRefs(request, rtu);
                rtu.setSite(request.isSiteWide());
                rtu.setRtuId(request.getRTUId());
                rtu.setModified(Instant.now());
                dataClient.updateRightToUse(rtu);
                response.addRtu(rtu);

            }
            response.setUpdated(true);
            response.setCreated(false);
            return response;
        } else {
            MLPRightToUse rightToUse = new MLPRightToUse(request.getSolutionId(), request.isSiteWide());
            rightToUse.setRtuId(request.getRTUId());
            addRtuRefs(request, rightToUse);
            rightToUse.setCreated(Instant.now());
            dataClient.createRightToUse(rightToUse);
            response.setCreated(true);
            response.addRtu(rightToUse);
        }
        return response;
    }

    public LicenseResponse verifyRTU(VerifyLicenseRequest request) {
        // currently only using workflow for dummy response
        boolean rightToUseFlag = false;
        List<MLPRightToUse> rightToUse = dataClient.getRightToUses(request.getSolutionId(), request.getUserId());
        // If there is a right to use (any for solution/user both download and deploy
        // are allowed in Boreas)
        if (rightToUse != null && !rightToUse.isEmpty()) {
            rightToUseFlag = true;
        }
        LicenseResponse response = new LicenseResponse();
        for (String workflow : request.getWorkflow()) {
            // we will get all rtu for user and solution id from CDS fake that here

            switch (workflow) {
            case "download":
                response.addWorkflow(workflow, rightToUseFlag);
                break;
            case "deploy":
                response.addWorkflow(workflow, rightToUseFlag);
                break;
            default:
                break;
            }
        }
        return response;

    }

}