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
import java.util.List;
import java.util.UUID;
import org.acumos.cds.client.CommonDataServiceRestClientMockImpl;
import org.acumos.cds.domain.MLPRightToUse;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.cds.domain.MLPUser;
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

    List<MLPRightToUse> nextRtuResponse = new ArrayList<MLPRightToUse>();

    public MockDatabaseClient(String webapiString, String user, String pass) {
      super(webapiString, user, pass);
    }

    @Override
    public MLPRightToUse createRightToUse(MLPRightToUse rightToUse) {
      // set as right to use response
      nextRtuResponse.add(rightToUse);
      setRightToUseList(nextRtuResponse);
      super.createRightToUse(rightToUse);
      return rightToUse;
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
    client.setRightToUseList(rtus);

    // update
    CreateRtuRequest licenseDownloadRequest2 = new CreateRtuRequest();
    // solutionId, userId
    licenseDownloadRequest2.setSiteWide(true);
    licenseDownloadRequest2.setSolutionId(solutionId);
    licenseDownloadRequest2.addUserId(userId);
    licenseDownloadRequest2.setRtuRefs(new String[] {UUID.randomUUID().toString()});
    ICreatedRtuResponse verifyUserRtu2 = licenseSrvc.createRtu(licenseDownloadRequest2);
    assertEquals(true, verifyUserRtu2 != null);
    assertEquals("expect rtu is updated not created", false, verifyUserRtu2.isCreated());
    assertEquals(true, verifyUserRtu2.isUpdated());
    assertEquals(solutionId, verifyUserRtu2.getRequest().getSolutionId());
    assertEquals(true, client.getRightToUses(solutionId, userId).size() > 0);
    // assertEquals(0, verifyUserRTU2.getRtuException().size());
    client.setRightToUseList(null);

    CreateRtuRequest licenseDownloadRequest3 = new CreateRtuRequest();
    // solutionId, userId
    licenseDownloadRequest3.setSiteWide(false);
    licenseDownloadRequest3.setSolutionId(solutionId);
    licenseDownloadRequest3.addUserId(userId2);
    licenseDownloadRequest3.setRtuRefs(new String[] {UUID.randomUUID().toString()});
    ICreatedRtuResponse verifyUserRtu3 = licenseSrvc.createRtu(licenseDownloadRequest3);
    assertEquals(true, verifyUserRtu3 != null);
    assertEquals("expect rtu is created not updated", true, verifyUserRtu3.isCreated());
    assertEquals(solutionId, verifyUserRtu3.getRequest().getSolutionId());
    assertEquals(true, client.getRightToUses(solutionId, userId2).size() > 0);
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
    creationrequest.setSolutionId("dummysolutionid");
    try {
      licenseSrvc.createRtu(creationrequest);
      fail("expected illegal argument exception");
    } catch (IllegalArgumentException illegalArgument) {
      assertEquals("request userId or siteWide is not defined", illegalArgument.getMessage());
    }
  }
}
