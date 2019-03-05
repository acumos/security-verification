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
    "licenseclient.message=Hello", 
    "licenseclient.deployAllowed=false",
    "licenseclient.downloadAllowed=true"})
public class LicenseClientTest {

    @Autowired
    private LicenseService licenseSrvc;

    @Test
    public void contextLoads() throws InterruptedException, ExecutionException {
        LicenseRequest licenseDownloadRequest = new LicenseRequest(new String[]{"deploy","download"}, "dummysolutionid", "dummyuserid");
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