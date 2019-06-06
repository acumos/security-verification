/*-
 * ===============LICENSE_START================================================
 * Acumos Apache-2.0
 * ============================================================================
 * Copyright (C) 2019 Nordix Foundation.
 * ============================================================================
 * This Acumos software file is distributed by Nordix Foundation
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
 * ===============LICENSE_END==================================================
 */

package org.acumos.licensemanager.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import org.acumos.cds.client.CommonDataServiceRestClientMockImpl;
import org.acumos.cds.domain.MLPRightToUse;
import org.acumos.cds.domain.MLPRtuReference;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.cds.domain.MLPUser;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.licensemanager.client.model.CreateRtuRequest;
import org.acumos.licensemanager.client.model.ICreatedRtuResponse;
import org.acumos.licensemanager.client.model.ILicenseVerification;
import org.acumos.licensemanager.client.model.ILicenseVerifier;
import org.acumos.licensemanager.client.model.LicenseAction;
import org.acumos.licensemanager.client.model.VerifyLicenseRequest;
import org.acumos.licensemanager.exceptions.RightToUseException;
import org.junit.Test;

/**
 * License Manager Client Unit tests - Test creation of the RTU and RTU references - Test reading
 * and verifying license/right to use exists.
 */
public class LicenseVerifierTest {

  private class MockDatabaseClient extends CommonDataServiceRestClientMockImpl {

    Long counter = new Long(0);
    Map<Long, List<MLPUser>> userRtu = new HashMap<Long, List<MLPUser>>();
    List<MLPRightToUse> nextRtuResponse = new ArrayList<MLPRightToUse>();

    public MockDatabaseClient(String webapiString, String user, String pass) {
      super(webapiString, user, pass);
    }

    @Override
    public MLPRightToUse createRightToUse(MLPRightToUse rightToUse) {
      // set as right to use response
      rightToUse.setRtuId(counter++);
      nextRtuResponse.add(rightToUse);
      setRightToUseList(nextRtuResponse);
      super.createRightToUse(rightToUse);

      return rightToUse;
    }

    @Override
    public void updateRightToUse(MLPRightToUse rightToUse) {
      // rightToUse.setRtuId(counter++);
      super.updateRightToUse(rightToUse);
    }

    @Override
    public void addUserToRtu(String userId, Long rtuId) {
      List<MLPUser> userIdsForRtu = userRtu.get(rtuId);
      if (userIdsForRtu != null) {
        userIdsForRtu.add(createUser(userId));
      } else {
        userIdsForRtu = new ArrayList<MLPUser>();
        userIdsForRtu.add(createUser(userId));
        userRtu.put(rtuId, userIdsForRtu);
      }
    }

    private MLPUser createUser(String userId) {
      MLPUser mlpUser = new MLPUser();
      mlpUser.setUserId(userId);
      return mlpUser;
    }

    @Override
    public void dropUserFromRtu(String userId, Long rtuId) {
      List<MLPUser> userIdsForRtu = userRtu.get(rtuId);
      List<MLPUser> resultUserIdsForRtu =
          userIdsForRtu.stream()
              .filter(item -> !userId.equals(item.getUserId()))
              .collect(Collectors.toList());
      userRtu.put(rtuId, resultUserIdsForRtu);
    }

    @Override
    public List<MLPUser> getRtuUsers(long rtuId) {
      return userRtu.get(rtuId);
    }
  }

  // TODO add invalid test which checks for log message

  @Test
  public void licenseVerifierUserOnly()
      throws InterruptedException, ExecutionException, RightToUseException {

    CommonDataServiceRestClientMockImpl client = new MockDatabaseClient("url", "user", "pass");

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
    // client.setRtusByReference(rightToUseList);
    CreateRtuRequest createRtuRequest = new CreateRtuRequest(solutionId, allowedUser.getUserId());
    LicenseCreator licenseCreator = new LicenseCreator(client);
    ICreatedRtuResponse createdRtu = licenseCreator.createRtu(createRtuRequest);

    ILicenseVerifier licenseSrvc = new LicenseVerifier(client);
    VerifyLicenseRequest licenseDownloadRequest =
        new VerifyLicenseRequest(
            LicenseAction.DEPLOY, solution.getSolutionId(), allowedUser.getUserId());
    licenseDownloadRequest.addAction(LicenseAction.DOWNLOAD);

    client.setRightToUses(new RestPageResponse<MLPRightToUse>(createdRtu.getRtus()));

    ILicenseVerification verifyUserRtu = licenseSrvc.verifyRtu(licenseDownloadRequest);
    // CompletableFuture.allOf(verifyUserRTU).join();
    assertEquals(true, verifyUserRtu != null);
    // assertEquals(rightToUseList.get(0),
    // client.getRtusByReference(UUID.randomUUID().toString()).get(0));
    assertEquals(true, verifyUserRtu.isAllowed(LicenseAction.DOWNLOAD));
    assertEquals(true, verifyUserRtu.isAllowed(LicenseAction.DEPLOY));

    client.setRightToUseList(new ArrayList<MLPRightToUse>());
    client.setRightToUses(new RestPageResponse<MLPRightToUse>(new ArrayList<MLPRightToUse>()));

    CreateRtuRequest createRtuRequest2 = new CreateRtuRequest();
    createRtuRequest2.setUserIds(new ArrayList<String>());
    createRtuRequest2.setSolutionId(solutionId);
    ICreatedRtuResponse createdRtu2CreatedRtuResponse = licenseCreator.createRtu(createRtuRequest2);
    licenseDownloadRequest =
        new VerifyLicenseRequest(
            new LicenseAction[] {LicenseAction.DEPLOY, LicenseAction.DOWNLOAD},
            solution.getSolutionId(),
            allowedUser.getUserId());
    verifyUserRtu = licenseSrvc.verifyRtu(licenseDownloadRequest);
    assertEquals(true, verifyUserRtu != null);
    assertEquals(false, verifyUserRtu.getAllowedToUse().get(LicenseAction.DOWNLOAD));
    assertEquals(false, verifyUserRtu.getAllowedToUse().get(LicenseAction.DEPLOY));
  }

  @Test
  public void licenseVerifierSiteWide()
      throws InterruptedException, ExecutionException, RightToUseException {

    CommonDataServiceRestClientMockImpl client = new MockDatabaseClient("url", "user", "pass");

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
    MLPRightToUse rightToUse = new MLPRightToUse(solutionId, true);

    Set<MLPRtuReference> rtuReferences = new HashSet<MLPRtuReference>();
    rtuReferences.add(new MLPRtuReference(UUID.randomUUID().toString()));
    rightToUse.setRtuReferences(rtuReferences);
    client.createRightToUse(rightToUse);

    List<MLPRightToUse> rightToUseList = new ArrayList<MLPRightToUse>();
    rightToUseList.add(rightToUse);
    client.setRightToUseList(rightToUseList);

    ILicenseVerifier licenseSrvc = new LicenseVerifier(client);
    VerifyLicenseRequest licenseDownloadRequest =
        new VerifyLicenseRequest(LicenseAction.DEPLOY, "dummysolutionid", "dummyuserid");
    licenseDownloadRequest.addAction(LicenseAction.DOWNLOAD);
    client.setRightToUses(new RestPageResponse<MLPRightToUse>(rightToUseList));

    ILicenseVerification verifyUserRTU = licenseSrvc.verifyRtu(licenseDownloadRequest);
    // CompletableFuture.allOf(verifyUserRTU).join();
    assertEquals(true, verifyUserRTU != null);
    assertEquals(true, verifyUserRTU.getAllowedToUse().get(LicenseAction.DOWNLOAD).booleanValue());
    assertEquals(true, verifyUserRTU.getAllowedToUse().get(LicenseAction.DEPLOY).booleanValue());

    client.setRightToUseList(new ArrayList<MLPRightToUse>());

    licenseDownloadRequest =
        new VerifyLicenseRequest(
            new LicenseAction[] {LicenseAction.DEPLOY, LicenseAction.DOWNLOAD},
            "dummysolutionid",
            "dummyuserid");
    verifyUserRTU = licenseSrvc.verifyRtu(licenseDownloadRequest);
    assertEquals(true, verifyUserRTU != null);
    assertEquals(true, verifyUserRTU.getAllowedToUse().get(LicenseAction.DOWNLOAD).booleanValue());
    assertEquals(true, verifyUserRTU.getAllowedToUse().get(LicenseAction.DEPLOY).booleanValue());
  }

  @Test
  public void licenseVerifierInvalidTests() throws RightToUseException {

    CommonDataServiceRestClientMockImpl dataClient = new MockDatabaseClient("url", "user", "pass");
    LicenseVerifier licenseVerifier = new LicenseVerifier(dataClient);
    VerifyLicenseRequest licenseDownloadRequest = new VerifyLicenseRequest();
    List<LicenseAction> actions = new ArrayList<LicenseAction>();
    actions.add(LicenseAction.DOWNLOAD);
    actions.add(LicenseAction.DEPLOY);
    licenseDownloadRequest.setActions(actions);
    licenseDownloadRequest.setSolutionId("dummysolutionid");

    try {
      licenseVerifier.verifyRtu(licenseDownloadRequest);
      fail("expected illegal argument exception");
    } catch (IllegalArgumentException illegalArgument) {
      assertEquals("request user id is not defined", illegalArgument.getMessage());
    }
  }

  @Test
  public void licenseVerifierNoRequest() throws RightToUseException {

    CommonDataServiceRestClientMockImpl dataClient = new MockDatabaseClient("url", "user", "pass");
    LicenseVerifier licenseVerifier = new LicenseVerifier(dataClient);

    try {
      licenseVerifier.verifyRtu(null);
      fail("expected illegal argument exception");
    } catch (IllegalArgumentException illegalArgument) {
      assertEquals("request is not defined", illegalArgument.getMessage());
    }
  }

  @Test
  public void licenseVerifierNoSolution() throws RightToUseException {

    CommonDataServiceRestClientMockImpl dataClient = new MockDatabaseClient("url", "user", "pass");
    LicenseVerifier licenseVerifier = new LicenseVerifier(dataClient);
    VerifyLicenseRequest licenseDownloadRequest = new VerifyLicenseRequest();
    licenseDownloadRequest.setSolutionId("dummysolution");
    licenseDownloadRequest.addUserId("dummyUser");

    try {
      licenseVerifier.verifyRtu(licenseDownloadRequest);
      fail("expected illegal argument exception");
    } catch (IllegalArgumentException illegalArgument) {
      assertEquals("request action is not defined", illegalArgument.getMessage());
    }
  }
}
