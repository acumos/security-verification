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
* http://www.apache.org/licenses/LICENSE-2.0
*
* This file is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
* ===============LICENSE_END=========================================================
*/ 

package org.acumos.licensemanager.service;

public class VerifyLicenseRequest {

  private String[] workflow; // we will need to convert this to rtuID --mapping rtuid to workflow id?
  private String solutionId;
  private String userId;

  public VerifyLicenseRequest(String workflow, String solutionId, String userId) {
    this.workflow = new String[] {workflow};
    this.solutionId = solutionId;
    this.userId = userId;
  }

  public VerifyLicenseRequest(String[] workflow, String solutionId, String userId) {
    this.workflow = workflow;
    this.solutionId = solutionId;
    this.userId = userId;
  }

  /**
   * @return the userId
   */
  public String getUserId() {
    return userId;
  }

  /**
   * @return the solutionId
   */
  public String getSolutionId() {
    return solutionId;
  }


  /**
   * @return the workflow
   */
  public String[] getWorkflow() {
    return workflow;
  }


}