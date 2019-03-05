package org.acumos.licensemanager.service;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;

@Service
@EnableConfigurationProperties(LicenseClientProperties.class)
public class LicenseService {

    private final LicenseClientProperties serviceProperties;

    public LicenseService(LicenseClientProperties serviceProperties) {
        this.serviceProperties = serviceProperties;
    }

    // TODO use RightToUseController.getRightToUsesForSolAndUser()
    public CompletableFuture<LicenseResponse> verifyRTU(LicenseRequest request) {
        // currently only using workflow for dummy response
        if (!this.serviceProperties.getCDS()) {

            // for each workflow requested // need to get rtuid -- TODO cache this later
            //
            LicenseResponse response = new LicenseResponse();

            for (String workflow : request.getWorkflow()) {
                // processEachWorkflow(workflow);
                // we will get all rtu for user and solution id from CDS fake that here
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

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
        // }else{
        // // TODO

        // }
    }

}