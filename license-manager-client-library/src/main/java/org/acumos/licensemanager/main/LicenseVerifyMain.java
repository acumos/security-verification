/*-
 * ===============LICENSE_START=======================================================
 * Acumos Apache-2.0
 * ===================================================================================
 * Copyright (C) 2019 Nordix Foundation.
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

package org.acumos.licensemanager.main;

import java.lang.invoke.MethodHandles;
import java.net.URL;

import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.licensemanager.client.LicenseVerifier;
import org.acumos.licensemanager.client.model.ILicenseVerification;
import org.acumos.licensemanager.client.model.LicenseAction;
import org.acumos.licensemanager.client.model.VerifyLicenseRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpStatusCodeException;

/**
 * License Verify Main program
 * Input to main program:
 * 	String solutionId String userId
 *
 * Envirionment variables required to point to CCDS api
 *    ACUMOS_CDS_HOST
 * 		ACUMOS_CDS_PORT
 * 		ACUMOS_CDS_USER
 * 		ACUMOS_CDS_PASSWORD
 *
 * @author est.tech
 * @version 0.0.2
 */
public class LicenseVerifyMain {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private static String hostname =  System.getenv("ACUMOS_CDS_HOST");
	private static final String contextPath = "/ccds";
	private static final int port = Integer.valueOf(System.getenv("ACUMOS_CDS_PORT"));
	private static final String userName = System.getenv("ACUMOS_CDS_USER");
	private static final String password = System.getenv("ACUMOS_CDS_PASSWORD");

	/**
	 * <p>main.</p>
	 *
	 * @param args an array of {@link java.lang.String} objects.
	 * @throws java.lang.Exception if any.
	 */
	public static void main(String[] args) throws Exception {

		URL url = new URL("http", hostname, port, contextPath);
		logger.info("createClient: URL is {}", url);
		ICommonDataServiceRestClient client = CommonDataServiceRestClientImpl.getInstance(url.toString(), userName,
				password);

		try {
	
			LicenseVerifier licenseVerifier = new LicenseVerifier(client);
			VerifyLicenseRequest verifyRequest = new VerifyLicenseRequest(new LicenseAction[]{LicenseAction.download, LicenseAction.deploy}, args[0], args[1]);
			ILicenseVerification licenseRes = licenseVerifier.verifyRTU(verifyRequest);
      System.out.println("Verified rtu");
      System.out.println("deploy allowed? " + licenseRes.isAllowed(LicenseAction.deploy));
			System.out.println("download allowed? " + licenseRes.isAllowed(LicenseAction.download));

		} catch (HttpStatusCodeException ex) {
			logger.error("basicSequenceDemo failed, server reports: {}", ex.getResponseBodyAsString());
			throw ex;
		}

	}
}
