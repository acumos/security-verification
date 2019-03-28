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
import org.acumos.licensemanager.client.LicenseCreator;
import org.acumos.licensemanager.client.model.CreateRTURequest;
import org.acumos.licensemanager.client.model.ICreateRTURequest;
import org.acumos.licensemanager.client.model.ICreatedRtu;
import org.acumos.licensemanager.client.model.ILicenseCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpStatusCodeException;

/**
 * License Verify Main program
 * Input to main program:
 * 	String solutionId String userId boolean siteWide
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
public class LicenseCreatorMain {

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
	
			// use license manager to create license for solution + user id
			ILicenseCreator licenseCreator = new LicenseCreator(client);
			ICreateRTURequest rtuRequest = new CreateRTURequest();
      
      rtuRequest.setSolutionId(args[0]);
      if(args.length>1){
        rtuRequest.setUserId(args[1]);
      }
      if(args.length>2){
        rtuRequest.setSiteWide(true);
      }
      
			ICreatedRtu rtuRes= licenseCreator.createRTU(rtuRequest);

      System.out.println("Created rtu" + rtuRes.isCreated());
      System.out.println("rtus created? " + rtuRes.getRtus());

		} catch (HttpStatusCodeException ex) {
			logger.error("basicSequenceDemo failed, server reports: {}", ex.getResponseBodyAsString());
			throw ex;
		}

	}
}
