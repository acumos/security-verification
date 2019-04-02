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
import java.net.URL;

import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.domain.MLPRtuReference;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPUser;
import org.acumos.licensemanager.client.LicenseCreator;
import org.acumos.licensemanager.client.LicenseVerifier;
import org.acumos.licensemanager.client.model.CreateRTURequest;
import org.acumos.licensemanager.client.model.ICreatedRtu;
import org.acumos.licensemanager.client.model.ILicenseCreator;
import org.acumos.licensemanager.client.model.ILicenseVerification;
import org.acumos.licensemanager.client.model.LicenseAction;
import org.acumos.licensemanager.client.model.VerifyLicenseRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpStatusCodeException;

/**
 * License Manager Client DB Test
 * - Demonstrate that License Manager client will work with running CDS 2.2
 */
public class LicenseClientDBTest {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private static String hostname =  System.getenv("ACUMOS_CDS_HOST");
	private static final String contextPath = "/ccds";
	private static final int port = Integer.valueOf(System.getenv("ACUMOS_CDS_PORT"));
	private static final String userName = System.getenv("ACUMOS_CDS_USER");
	private static final String password = System.getenv("ACUMOS_CDS_PASSWORD");

	public static void main(String[] args) throws Exception {

		URL url = new URL("http", hostname, port, contextPath);
		logger.info("createClient: URL is {}", url);
		ICommonDataServiceRestClient client = CommonDataServiceRestClientImpl.getInstance(url.toString(), userName,
				password);

		try {
			String userName = "user_login1";
			MLPUser cu = new MLPUser(userName, "login1user@abc.com", true);
			cu.setLoginHash("user_pass1");
			cu.setFirstName("First Name");
			cu.setLastName("Last Name");
			cu.setEmail(cu.getLoginName() + "@nowhere.com");

			cu = client.createUser(cu);
			logger.info("Created user {}", cu);

			MLPSolution cs = new MLPSolution("solution name", cu.getUserId(), true);
			cs.setModelTypeCode("CL");
			cs.setToolkitTypeCode("CP");
			cs = client.createSolution(cs);
			logger.info("Created solution {}", cs);

			// use license manager to create license for solution + user id
			ILicenseCreator licenseCreator = new LicenseCreator(client);
			CreateRTURequest rtuRequest = new CreateRTURequest();
			rtuRequest.setSiteWide(true);
			rtuRequest.setSolutionId(cs.getSolutionId());
			ICreatedRtu rtuRes= licenseCreator.createRTU(rtuRequest);
			logger.info("Created rtu {}", rtuRes);

			LicenseVerifier licenseVerifier = new LicenseVerifier(client);

			VerifyLicenseRequest verifyRequest = new VerifyLicenseRequest(LicenseAction.download, cs.getSolutionId(), cu.getUserId());
			ILicenseVerification licenseRes = licenseVerifier.verifyRTU(verifyRequest);
			logger.info("Verified rtu {}", licenseRes);

			// TODO - this was not working due to constraint violation
			client.deleteRtuReference(rtuRes.getRtus().get(0).getRtuReferences().toArray(new MLPRtuReference[1])[0]);
			client.deleteRightToUse(rtuRes.getRtus().get(0).getRtuId());
			client.deleteSolution(cs.getSolutionId());
			client.deleteUser(cu.getUserId());

		} catch (HttpStatusCodeException ex) {
			logger.error("basicSequenceDemo failed, server reports: {}", ex.getResponseBodyAsString());
			throw ex;
		}

	}
}
