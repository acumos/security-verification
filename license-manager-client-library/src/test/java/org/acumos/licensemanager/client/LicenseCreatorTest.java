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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
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
import org.acumos.licensemanager.client.model.ILicenseCreator;
import org.acumos.licensemanager.exceptions.RightToUseException;
import org.junit.Before;
import org.junit.Test;

/**
 * License Manager Client Unit tests - Test creation of the RTU and RTU references - Test reading
 * and verifying license/right to use exists.
 */
public class LicenseCreatorTest {

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

  MockDatabaseClient client;

  @Before
  public void setupDbClient() {
    client = new MockDatabaseClient("url", "user", "pass");
  }

  @Test
  public void licenseCreator() throws RightToUseException {
    // URL url = new URL("http", hostname, port, contextPath);
    // logger.info("createClient: URL is {}", url);
    final String userId = "dummyuserid";
    final String userId2 = "dummeruserid2";
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
    CreateRtuRequest licenseDownloadRequest = new CreateRtuRequest(solutionId, userId);
    ICreatedRtuResponse verifyUserRTU = licenseSrvc.createRtu(licenseDownloadRequest);
    assertEquals(true, verifyUserRTU != null);
    assertEquals(true, verifyUserRTU.isCreated());
    assertEquals(false, verifyUserRTU.isUpdated());
    assertEquals(solutionId, verifyUserRTU.getRequest().getSolutionId());
    List<MLPRightToUse> rtus = verifyUserRTU.getRtus();
    assertEquals(true, rtus != null && rtus.size() == 1);
    MLPRtuReference firstRtuRefId = rtus.get(0).getRtuReferences().iterator().next();

    client.setRightToUseList(rtus);
    client.setRightToUses(new RestPageResponse<MLPRightToUse>(rtus));

    // update rtu on solution same one with different nme -- get rtu failure
    CreateRtuRequest licenseDownloadRequest2 = new CreateRtuRequest();
    // solutionId, userId
    String secondRtuRefId = UUID.randomUUID().toString();
    licenseDownloadRequest2.setSiteWide(true);
    licenseDownloadRequest2.setSolutionId(solutionId);
    licenseDownloadRequest2.addUserId(userId);
    licenseDownloadRequest2.setRtuRefs(new String[] {secondRtuRefId});
    try {
      licenseSrvc.createRtu(licenseDownloadRequest2);
      fail("expect exception this line should not fire");
    } catch (RightToUseException e) {
      assertEquals("except solution to match", solutionId, e.getSolutionId());
    }

    // update case use same name to allow it to update
    CreateRtuRequest licenseDownloadRequest3 = new CreateRtuRequest();
    // solutionId, userId

    licenseDownloadRequest3.setSiteWide(false);
    licenseDownloadRequest3.setSolutionId(solutionId);
    licenseDownloadRequest3.addUserId(userId2);
    licenseDownloadRequest3.setRtuRefs(new String[] {firstRtuRefId.getRef()});
    ICreatedRtuResponse verifyUserRtu3 = licenseSrvc.createRtu(licenseDownloadRequest3);

    assertEquals(true, verifyUserRtu3 != null);
    assertEquals("expect rtu is updated", false, verifyUserRtu3.isCreated());
    assertEquals("expect 1 rtu", 1, verifyUserRtu3.getRtus().size());
    assertEquals(
        "expect 2 rtu refs ", 1, verifyUserRtu3.getRtus().get(0).getRtuReferences().size());
    Set<MLPRtuReference> refs = verifyUserRtu3.getRtus().get(0).getRtuReferences();
    assertEquals("expect firstRtuRef" + firstRtuRefId, true, refs.contains(firstRtuRefId));

    assertEquals(solutionId, verifyUserRtu3.getRequest().getSolutionId());
    List<MLPUser> rtuUsers = client.getRtuUsers(verifyUserRtu3.getRtus().get(0).getRtuId());
    assertEquals(1, rtuUsers.size());
    assertEquals(
        "Expect new user id",
        true,
        rtuUsers.stream().anyMatch(rtuUser -> rtuUser.getUserId().equals(userId2)));
    assertEquals(
        "Expect all user no longer has right to use",
        false,
        rtuUsers.stream().anyMatch(rtuUser -> rtuUser.getUserId().equals(userId)));

    // assertEquals(0, verifyUserRTU2.getRtuException().size());

  }

  @Test()
  public void invalidNoRequestArgumentTest() throws RightToUseException {
    ILicenseCreator licenseSrvc = new LicenseCreator(client);

    try {
      licenseSrvc.createRtu(null);
      fail("expected illegal argument exception");
    } catch (IllegalArgumentException illegalArgument) {
      assertEquals("request is not defined", illegalArgument.getMessage());
    }
  }

  @Test()
  public void invalidNoSolutionIdArgumentTest() throws RightToUseException {
    ILicenseCreator licenseSrvc = new LicenseCreator(client);
    CreateRtuRequest creationrequest = new CreateRtuRequest();

    try {
      licenseSrvc.createRtu(creationrequest);
      fail("expected illegal argument exception");
    } catch (IllegalArgumentException illegalArgument) {
      assertEquals("request solution id is not defined", illegalArgument.getMessage());
    }
  }

  @Test()
  public void invalidNoUserIdArgumentTest() throws RightToUseException {
    ILicenseCreator licenseSrvc = new LicenseCreator(client);
    CreateRtuRequest creationrequest = new CreateRtuRequest();
    creationrequest.setUserIds(null);
    creationrequest.setSolutionId("dummysolutionid");
    try {
      licenseSrvc.createRtu(creationrequest);
      fail("expected illegal argument exception");
    } catch (IllegalArgumentException illegalArgument) {
      assertEquals("request userId or siteWide is not defined", illegalArgument.getMessage());
    }
  }
}
