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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.acumos.cds.client.CommonDataServiceRestClientMockImpl;
import org.acumos.cds.domain.MLPRightToUse;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.cds.domain.MLPUser;
import org.acumos.licensemanager.client.model.CreateRTURequest;
import org.acumos.licensemanager.client.model.ICreatedRtu;
import org.acumos.licensemanager.client.model.ILicenseCreator;
import org.acumos.licensemanager.exceptions.RightToUseException;
import org.junit.Test;

/**
 * License Manager Client Unit tests - Test creation of the RTU and RTU
 * references - Test reading and verifying license/right to use exists.
 */
public class LicenseCreatorTest {

	// TODO add invalid test which checks for log message


	@Test
	public void licenseCreator() throws RightToUseException {
		// URL url = new URL("http", hostname, port, contextPath);
		// logger.info("createClient: URL is {}", url);
		CommonDataServiceRestClientMockImpl client = new CommonDataServiceRestClientMockImpl("url", "user", "pass");
		String userId = "dummyuserid";

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

		// create
		ILicenseCreator licenseSrvc = new LicenseCreator(client);
		CreateRTURequest licenseDownloadRequest = new CreateRTURequest(solutionId, userId);
		ICreatedRtu verifyUserRTU;

		verifyUserRTU = licenseSrvc.createRTU(licenseDownloadRequest);
		assertEquals(true, verifyUserRTU != null);
		assertEquals(true, verifyUserRTU.isCreated());
		assertEquals(false, verifyUserRTU.isUpdated());
		assertEquals(solutionId, verifyUserRTU.getRequest().getSolutionId());
		List<MLPRightToUse> rtus = verifyUserRTU.getRtus();
		assertEquals(true, rtus != null && rtus.size() == 1);
		client.setRightToUseList(rtus);


		// update
		CreateRTURequest licenseDownloadRequest2 = new CreateRTURequest();
		// solutionId, userId
		licenseDownloadRequest2.setSiteWide(true);
		licenseDownloadRequest2.setSolutionId(solutionId);
		licenseDownloadRequest2.addUserId(userId);
		licenseDownloadRequest2.setRtuId(new Random().nextLong());
		licenseDownloadRequest2.setRtuRefs(new String[] { UUID.randomUUID().toString() });
		ICreatedRtu verifyUserRTU2 = licenseSrvc.createRTU(licenseDownloadRequest2);
		assertEquals(true, verifyUserRTU2 != null);
		assertEquals("expect rtu is updated not created", false, verifyUserRTU2.isCreated());
		assertEquals(true, verifyUserRTU2.isUpdated());
		assertEquals(solutionId, verifyUserRTU2.getRequest().getSolutionId());
		assertEquals(true, client.getRightToUses(solutionId, userId).size() > 0);
		// assertEquals(0, verifyUserRTU2.getRtuException().size());

	}

	@Test()
	public void invalidNoRequestArgumentTest() throws RightToUseException {
		CommonDataServiceRestClientMockImpl client = new CommonDataServiceRestClientMockImpl("url", "user", "pass");
		ILicenseCreator licenseSrvc = new LicenseCreator(client);

		try {
			licenseSrvc.createRTU(null);
			fail("expected illegal argument exception");
		} catch (IllegalArgumentException illegalArgument) {
			assertEquals(illegalArgument.getMessage(),"request is not defined");
		}
	}

	@Test()
	public void invalidNoSolutionIdArgumentTest() throws RightToUseException {
		CommonDataServiceRestClientMockImpl client = new CommonDataServiceRestClientMockImpl("url", "user", "pass");
		ILicenseCreator licenseSrvc = new LicenseCreator(client);
		CreateRTURequest creationrequest = new CreateRTURequest();

		try {
			licenseSrvc.createRTU(creationrequest);
			fail("expected illegal argument exception");
		} catch (IllegalArgumentException illegalArgument) {
			assertEquals(illegalArgument.getMessage(),"request solution id is not defined");
		}
	}

	@Test()
	public void invalidNoUserIdArgumentTest() throws RightToUseException {
		CommonDataServiceRestClientMockImpl client = new CommonDataServiceRestClientMockImpl("url", "user", "pass");
		ILicenseCreator licenseSrvc = new LicenseCreator(client);
		CreateRTURequest creationrequest = new CreateRTURequest();
		creationrequest.setSolutionId("dummysolutionid");
		try {
			licenseSrvc.createRTU(creationrequest);
			fail("expected illegal argument exception");
		} catch (IllegalArgumentException illegalArgument) {
			assertEquals(illegalArgument.getMessage(),"request userId or siteWide is not defined");
		}
	}


	
}
