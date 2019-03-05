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

import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.assertEquals;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {
    "licenseclient.deployAllowed=false",
    "licenseclient.downloadAllowed=true"})
public class LicenseClientTest {

    @Autowired
    private LicenseService licenseSrvc;

    @Test
    public void contextLoads() throws InterruptedException, ExecutionException {
        VerifyLicenseRequest licenseDownloadRequest = new VerifyLicenseRequest(new String[]{"deploy","download"}, "dummysolutionid", "dummyuserid");
        CompletableFuture<LicenseResponse> verifyUserRTU = licenseSrvc.verifyRTU(licenseDownloadRequest);
        CompletableFuture.allOf(verifyUserRTU).join();
        assertThat(verifyUserRTU).isNotNull();
        assertEquals(true, verifyUserRTU.get().getAllowedToUse().get("download").booleanValue());
        assertEquals(false, verifyUserRTU.get().getAllowedToUse().get("deploy").booleanValue());

    }

    @SpringBootApplication
    static class TestConfiguration {
    }

}