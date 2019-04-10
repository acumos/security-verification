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
import java.util.List;

import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.domain.MLPRightToUse;
import org.acumos.licensemanager.client.model.ILicenseVerification;
import org.acumos.licensemanager.client.model.ILicenseVerifier;
import org.acumos.licensemanager.client.model.IVerifyLicenseRequest;
import org.acumos.licensemanager.client.model.LicenseVerification;
import org.acumos.licensemanager.exceptions.RightToUseException;
import org.acumos.licensemanager.client.model.LicenseAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * LicenseVerifier will verify that user or site has the RTU for a solution id
 * for a specific action.
 *
 * In Boreas release the action we only have one RTU for all actions.
 *
 * @version 0.0.2
 */
public class LicenseVerifier implements ILicenseVerifier {

    /**
     * Logger for any exceptions that happen while creating a RTU with CDS.
     */
    private static final Logger LOGGER = LoggerFactory
        .getLogger(MethodHandles.lookup().lookupClass());
    /**
     * dataClient must be provided by consumer of this library.
     */
    private final ICommonDataServiceRestClient dataClient;

    /**
     * <p>
     * Constructor for LicenseVerifier.
     * </p>
     *
     * @param dataServiceClient a
     *              {@link org.acumos.cds.client.ICommonDataServiceRestClient}
     *              object.
     */
    public LicenseVerifier(
            final ICommonDataServiceRestClient dataServiceClient) {
        this.dataClient = dataServiceClient;
    }

      @Override
    public final ILicenseVerification verifyRTU(
            final IVerifyLicenseRequest request) throws RightToUseException {
        // currently only using action for dummy response
        LicenseVerification response = new LicenseVerification();

        if (request == null) {
            throw new IllegalArgumentException("request is not defined");
        }
        if (request.getSolutionId() == null) {
            throw new IllegalArgumentException("request solution id "
            + "is not defined");
        }
        if (request.getUserIds().isEmpty()) {
            throw new IllegalArgumentException("request user "
            + "id is not defined");
        }
        if (request.getActions().isEmpty()) {
            throw new IllegalArgumentException("request action is not "
            + "defined");
        }

        boolean rightToUseFlag = false;
        // Check for user RTU if there is no sitewide RTU
        List<MLPRightToUse> rightToUse = LicenseCDSUtil
            .getRightToUses(dataClient, request);
        List<MLPRightToUse> siteWideRTUForSolution = LicenseCDSUtil
            .getSitewideSolutionRTU(dataClient, request);
        // If there is a right to use (any for solution/user
        // for download or deploy actions
        // are allowed in Boreas)
        if ((siteWideRTUForSolution != null
            && !siteWideRTUForSolution.isEmpty())
                || (rightToUse != null && !rightToUse.isEmpty())) {
            rightToUseFlag = true;
        }
        for (LicenseAction action : request.getActions()) {
            switch (action) {
            case DOWNLOAD:
                response.addAction(action, rightToUseFlag);
                break;
            case DEPLOY:
                response.addAction(action, rightToUseFlag);
                break;
            default:
                LOGGER.error("unimplemented license action {}", action);
                break;
            }
        }
        return response;

    }

}