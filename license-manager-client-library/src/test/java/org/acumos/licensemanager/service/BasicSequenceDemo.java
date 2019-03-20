/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2019 Ericsson Software Technology AB. All rights reserved.
 * ===================================================================================
 * This Acumos software file is distributed by AT&T and Tech Mahindra
 * under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * This file is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ===============LICENSE_END=========================================================
 */

package org.acumos.licensemanager.service;

import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.domain.MLPArtifact;
import org.acumos.cds.domain.MLPRevisionDescription;
import org.acumos.cds.domain.MLPRtuReference;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.cds.domain.MLPUser;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.licensemanager.service.create.CreateRTURequest;
import org.acumos.licensemanager.service.create.RTUResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpStatusCodeException;

/**
 * Demonstrates use of the CDS client.
 */
public class BasicSequenceDemo {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private static String hostname = "localhost";
	private static final String contextPath = "/ccds";
	private static final int port = 30614;
	private static final String userName = "ccds_client";
	private static final String password = "ccds_password";

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
			// Map<String, Object> map = new HashMap<String, Object>();
			// map.put("loginName", "user_login1");
			
			// RestPageResponse<MLPUser>  existingCus = client.searchUsers(map, false, null);
			// if(existingCus != null && existingCus.getSize() > 0){
			// 	for (MLPUser user : existingCus.getContent()) {
			// 		client.deleteUser(user.getUserId());
			// 	}
				
			// }
			cu = client.createUser(cu);
			logger.info("Created user {}", cu);

			MLPSolution cs = new MLPSolution("solution name", cu.getUserId(), true);
			cs.setModelTypeCode("CL");
			cs.setToolkitTypeCode("CP");
			cs = client.createSolution(cs);
			logger.info("Created solution {}", cs);

			// use license manager to create license for solution + user id
			LicenseService licenseService = new LicenseService(client);
			CreateRTURequest rtuRequest = new CreateRTURequest(cs.getSolutionId(), cu.getUserId());
			RTUResponse rtuRes= licenseService.createRTU(rtuRequest);
			logger.info("Created rtu {}", rtuRes);

			VerifyLicenseRequest verifyRequest = new VerifyLicenseRequest("download", cs.getSolutionId(), cu.getUserId());
			LicenseResponse licenseRes = licenseService.verifyRTU(verifyRequest);
			logger.info("Verified rtu {}", licenseRes);

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