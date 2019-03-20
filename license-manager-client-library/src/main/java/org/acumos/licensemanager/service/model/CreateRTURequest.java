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

package org.acumos.licensemanager.service.model;

import java.util.Random;
import java.util.UUID;

public class CreateRTURequest implements ILicenseRequest {
  String solutionId;
  String userId;
  private String[] rtuRefs;
  private boolean siteWide = false;
  private Long rtuId;

  public CreateRTURequest(){

  }

  public CreateRTURequest(String solId, String uId) {
    solutionId = solId;
    userId = uId;

  }

  public String getSolutionId() {
    return solutionId;
  }

  /**
   * @param solutionId the solutionId to set
   */
  public void setSolutionId(String solutionId) {
    this.solutionId = solutionId;
  }

  /**
   * @param userId the userId to set
   */
  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getUserId() {
    return userId;
  }

  public String[] getRTURefs() {
    if(rtuRefs == null){
      return new String[]{UUID.randomUUID().toString()};
    }
    return rtuRefs;
  }

  public Long getRTUId() {
    if(rtuId == null){
      return new Random().nextLong();
    }
    return rtuId;
  }

  public boolean isSiteWide() {
    return siteWide;
  }

  /**
   * @param siteWide the siteWide to set
   */
  public void setSiteWide(boolean siteWide) {
    this.siteWide = siteWide;
  }

}
