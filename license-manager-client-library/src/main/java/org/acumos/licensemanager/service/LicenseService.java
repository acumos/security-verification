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


import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class LicenseService {

    private final LicenseClientProperties serviceProperties;

    public LicenseService(LicenseClientProperties serviceProperties) {
        this.serviceProperties = serviceProperties;
    }

    // TODO use RightToUseController.getRightToUsesForSolAndUser()
    public CompletableFuture<LicenseResponse> verifyRTU(VerifyLicenseRequest request) {
        // currently only using workflow for dummy response
        if (!this.serviceProperties.getCDS()) {

            // for each workflow requested // need to get rtuid -- TODO cache this later
            //
            LicenseResponse response = new LicenseResponse();
            // Temporary code will be replaced with CDS async all to database in S4
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
               throw new IllegalStateException(e);
            }
            // Placeholder work in progress code in S3
            for (String workflow : request.getWorkflow()) {
                // processEachWorkflow(workflow);
                // we will get all rtu for user and solution id from CDS fake that here

                switch (workflow) {
                    case "download":
                        response.addWorkflow(workflow, this.serviceProperties.isDownloadAllowed());
                        break;
                    case "deploy":
                        response.addWorkflow(workflow, this.serviceProperties.isDeployAllowed());
                        break;
                    default:
                        break;
                }
            }
            return CompletableFuture.completedFuture(response);
        }
        return null;

    }

}