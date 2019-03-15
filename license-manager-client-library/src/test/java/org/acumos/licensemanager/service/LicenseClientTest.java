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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.acumos.cds.client.CommonDataServiceRestClientMockImpl;
import org.acumos.cds.domain.MLPRightToUse;
import org.acumos.cds.domain.MLPRtuReference;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.cds.domain.MLPUser;
import org.junit.Test;

/**
 * Demonstrates use of the CDS client.
 */
public class LicenseClientTest {

    // private static final Logger logger =
    // LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Test
    public void rightToUseTest() throws InterruptedException, ExecutionException {
		// URL url = new URL("http", hostname, port, contextPath);
		// logger.info("createClient: URL is {}", url);
		CommonDataServiceRestClientMockImpl client = new CommonDataServiceRestClientMockImpl("url", "user", "pass");

		// mock user for rtu test

		MLPUser user = new MLPUser();
		client.setLoginUser(user);
		client.setUserById(user);
		client.setUser(user);

		// mock solution to use for rtu test
		MLPSolution solution = new MLPSolution();
		String solutionId = UUID.randomUUID().toString();
		solution.setSolutionId(solutionId);
		client.setSolution(solution);
		client.setSolutionById(solution);

		// mock revision for solution
		MLPSolutionRevision solRev = new MLPSolutionRevision();
		client.setSolutionRevisionById(solRev);
		client.setSolutionRevision(solRev);

		// generic right to use 
		Long rtuId = new Random().nextLong();
		MLPRightToUse rightToUse = new MLPRightToUse(solutionId, true);
		rightToUse.setRtuId(rtuId);

		Set<MLPRtuReference> rtuReferences = new HashSet<MLPRtuReference>();
		rtuReferences.add(new MLPRtuReference(UUID.randomUUID().toString()));
		rightToUse.setRtuReferences(rtuReferences);
		client.createRightToUse(rightToUse);

		List<MLPRightToUse> rightToUseList = new ArrayList<MLPRightToUse>();
		rightToUseList.add(rightToUse);
		client.setRightToUseList(rightToUseList);

		LicenseService licenseSrvc = new LicenseService(client);
		VerifyLicenseRequest licenseDownloadRequest = new VerifyLicenseRequest(new String[]{"deploy","download"}, "dummysolutionid", "dummyuserid");
		CompletableFuture<LicenseResponse> verifyUserRTU = licenseSrvc.verifyRTU(licenseDownloadRequest);
		CompletableFuture.allOf(verifyUserRTU).join();
		assertEquals(true, verifyUserRTU != null);
		assertEquals(true, verifyUserRTU.get().getAllowedToUse().get("download").booleanValue());
		assertEquals(true, verifyUserRTU.get().getAllowedToUse().get("deploy").booleanValue());

        client.setRightToUseList(new ArrayList<MLPRightToUse>());

        licenseDownloadRequest = new VerifyLicenseRequest(new String[]{"deploy","download"}, "dummysolutionid", "dummyuserid");
		verifyUserRTU = licenseSrvc.verifyRTU(licenseDownloadRequest);
		CompletableFuture.allOf(verifyUserRTU).join();
		assertEquals(true, verifyUserRTU != null);
		assertEquals(false, verifyUserRTU.get().getAllowedToUse().get("download").booleanValue());
		assertEquals(false, verifyUserRTU.get().getAllowedToUse().get("deploy").booleanValue());


	
	}

}