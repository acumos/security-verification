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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.acumos.cds.client.CommonDataServiceRestClientMockImpl;
import org.acumos.cds.domain.MLPRightToUse;
import org.acumos.cds.domain.MLPRtuReference;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.cds.domain.MLPUser;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.licensemanager.client.model.ILicenseVerification;
import org.acumos.licensemanager.client.model.ILicenseVerifier;
import org.acumos.licensemanager.client.model.LicenseAction;
import org.acumos.licensemanager.client.model.VerifyLicenseRequest;
import org.acumos.licensemanager.exceptions.RightToUseException;
import org.junit.Test;

/**
 * License Manager Client Unit tests - Test creation of the RTU and RTU
 * references - Test reading and verifying license/right to use exists.
 */
public class LicenseVerifierTest {

	// TODO add invalid test which checks for log message

	@Test
	public void licenseVerifierUserOnly() throws InterruptedException, ExecutionException, RightToUseException {

		CommonDataServiceRestClientMockImpl client = new CommonDataServiceRestClientMockImpl("url", "user", "pass");

		// mock user for rtu test

		MLPUser allowedUser = new MLPUser();
		client.setLoginUser(allowedUser);
		client.setUserById(allowedUser);
		client.setUser(allowedUser);
		allowedUser.setUserId("allowedUser");

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
		MLPRightToUse rightToUse = new MLPRightToUse(solutionId, false);
		rightToUse.setRtuId(rtuId);

		Set<MLPRtuReference> rtuReferences = new HashSet<MLPRtuReference>();
		rtuReferences.add(new MLPRtuReference(UUID.randomUUID().toString()));
		rightToUse.setRtuReferences(rtuReferences);
		client.createRightToUse(rightToUse);

		List<MLPRightToUse> rightToUseList = new ArrayList<MLPRightToUse>();
		rightToUseList.add(rightToUse);
		client.setRightToUseList(rightToUseList);

		ILicenseVerifier licenseSrvc = new LicenseVerifier(client);
		VerifyLicenseRequest licenseDownloadRequest = new VerifyLicenseRequest(LicenseAction.deploy, solution.getSolutionId(),
				allowedUser.getUserId());
		licenseDownloadRequest.addAction(LicenseAction.download);

		client.setRightToUses(new RestPageResponse<MLPRightToUse>(new ArrayList<MLPRightToUse>()));

		ILicenseVerification verifyUserRTU = licenseSrvc.verifyRTU(licenseDownloadRequest);
		// CompletableFuture.allOf(verifyUserRTU).join();
		assertEquals(true, verifyUserRTU != null);
		assertEquals(true, verifyUserRTU.isAllowed(LicenseAction.download));
		assertEquals(true, verifyUserRTU.isAllowed(LicenseAction.deploy));

		client.setRightToUseList(new ArrayList<MLPRightToUse>());

		// licenseDownloadRequest = new VerifyLicenseRequest(
		// 		new LicenseAction[] { LicenseAction.deploy, LicenseAction.download }, solution.getSolutionId(), allowedUser.getUserId());
		// verifyUserRTU = licenseSrvc.verifyRTU(licenseDownloadRequest);
		// assertEquals(true, verifyUserRTU != null);
		// assertEquals(0, verifyUserRTU.getRtuException().size());
		// assertEquals(true, verifyUserRTU.isAllowed(LicenseAction.download));
		// assertEquals(true, verifyUserRTU.isAllowed(LicenseAction.deploy));

		MLPUser disAllowedUser = new MLPUser();
		client.setLoginUser(disAllowedUser);
		client.setUserById(disAllowedUser);
		client.setUser(disAllowedUser);
		disAllowedUser.setUserId("disAllowedUser");
		licenseDownloadRequest = new VerifyLicenseRequest(
			new LicenseAction[] { LicenseAction.deploy, LicenseAction.download }, solution.getSolutionId(), disAllowedUser.getUserId());
		verifyUserRTU = licenseSrvc.verifyRTU(licenseDownloadRequest);
		assertEquals(true, verifyUserRTU != null);
		assertEquals(false, verifyUserRTU.getAllowedToUse().get(LicenseAction.download));
		assertEquals(false, verifyUserRTU.getAllowedToUse().get(LicenseAction.deploy));


	}


	@Test
	public void licenseVerifierSiteWide() throws InterruptedException, ExecutionException, RightToUseException {

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

		ILicenseVerifier licenseSrvc = new LicenseVerifier(client);
		VerifyLicenseRequest licenseDownloadRequest = new VerifyLicenseRequest(LicenseAction.deploy, "dummysolutionid",
				"dummyuserid");
		licenseDownloadRequest.addAction(LicenseAction.download);
		client.setRightToUses(new RestPageResponse<MLPRightToUse>(rightToUseList));

		ILicenseVerification verifyUserRTU = licenseSrvc.verifyRTU(licenseDownloadRequest);
		// CompletableFuture.allOf(verifyUserRTU).join();
		assertEquals(true, verifyUserRTU != null);
		assertEquals(true, verifyUserRTU.getAllowedToUse().get(LicenseAction.download).booleanValue());
		assertEquals(true, verifyUserRTU.getAllowedToUse().get(LicenseAction.deploy).booleanValue());

		client.setRightToUseList(new ArrayList<MLPRightToUse>());

		licenseDownloadRequest = new VerifyLicenseRequest(
				new LicenseAction[] { LicenseAction.deploy, LicenseAction.download }, "dummysolutionid", "dummyuserid");
		verifyUserRTU = licenseSrvc.verifyRTU(licenseDownloadRequest);
		assertEquals(true, verifyUserRTU != null);
		assertEquals(true, verifyUserRTU.getAllowedToUse().get(LicenseAction.download).booleanValue());
		assertEquals(true, verifyUserRTU.getAllowedToUse().get(LicenseAction.deploy).booleanValue());

	}

	@Test
	public void licenseVerifierInvalidTests() throws RightToUseException {

		CommonDataServiceRestClientMockImpl dataClient = new CommonDataServiceRestClientMockImpl("url", "user", "pass");
		LicenseVerifier licenseVerifier = new LicenseVerifier(dataClient);
		VerifyLicenseRequest licenseDownloadRequest = new VerifyLicenseRequest();
		List<LicenseAction> actions = new ArrayList<LicenseAction>();
		actions.add(LicenseAction.download);
		actions.add(LicenseAction.deploy);
		licenseDownloadRequest.setActions(actions);
		licenseDownloadRequest.setSolutionId("dummysolutionid");
		
		try {
			licenseVerifier.verifyRTU(licenseDownloadRequest);
			fail("expected illegal argument exception");
		} catch (IllegalArgumentException illegalArgument) {
			assertEquals(illegalArgument.getMessage(),"request user id is not defined");
		}

	}

	@Test
	public void licenseVerifierNoRequest() throws RightToUseException {

		CommonDataServiceRestClientMockImpl dataClient = new CommonDataServiceRestClientMockImpl("url", "user", "pass");
		LicenseVerifier licenseVerifier = new LicenseVerifier(dataClient);

		try {
			licenseVerifier.verifyRTU(null);
			fail("expected illegal argument exception");
		} catch (IllegalArgumentException illegalArgument) {
			assertEquals(illegalArgument.getMessage(),"request is not defined");
		}

	}

	@Test
	public void licenseVerifierNoSolution() throws RightToUseException {

		CommonDataServiceRestClientMockImpl dataClient = new CommonDataServiceRestClientMockImpl("url", "user", "pass");
		LicenseVerifier licenseVerifier = new LicenseVerifier(dataClient);
		VerifyLicenseRequest licenseDownloadRequest = new VerifyLicenseRequest();
		licenseDownloadRequest.setSolutionId("dummysolution");
		licenseDownloadRequest.addUserId("dummyUser");

		try {
			licenseVerifier.verifyRTU(licenseDownloadRequest);
			fail("expected illegal argument exception");
		} catch (IllegalArgumentException illegalArgument) {
			assertEquals(illegalArgument.getMessage(),"request action is not defined");
		}

	}



}
