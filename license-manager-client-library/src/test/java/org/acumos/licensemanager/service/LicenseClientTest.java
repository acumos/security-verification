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

import static org.junit.Assert.assertEquals;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.Test;


public class LicenseClientTest {

    @Test
    public void contextLoads() throws InterruptedException, ExecutionException {
        // Temporary values will be replaced with CDS in Boreas S4
        LicenseClientProperties properties = new LicenseClientProperties();
        properties.setDeployAllowed(false);
        properties.setDownloadAllowed(true);
        // Start usage of license service
        LicenseService licenseSrvc = new LicenseService(properties);
        VerifyLicenseRequest licenseDownloadRequest = new VerifyLicenseRequest(new String[]{"deploy","download"}, "dummysolutionid", "dummyuserid");
        CompletableFuture<LicenseResponse> verifyUserRTU = licenseSrvc.verifyRTU(licenseDownloadRequest);
        CompletableFuture.allOf(verifyUserRTU).join();
        assertEquals(true, verifyUserRTU != null);
        assertEquals(true, verifyUserRTU.get().getAllowedToUse().get("download").booleanValue());
        assertEquals(false, verifyUserRTU.get().getAllowedToUse().get("deploy").booleanValue());

    }

}