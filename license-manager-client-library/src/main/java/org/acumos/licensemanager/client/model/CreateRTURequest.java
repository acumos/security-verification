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

package org.acumos.licensemanager.client.model;

import java.util.Random;
import java.util.UUID;

/**
 * <p>CreateRTURequest class.</p>
 *
 *CreatedRtu
 *
 * @author est.tech
 * @version 0.0.2
 */
public class CreateRTURequest implements ICreateRTURequest {
  String solutionId;
  String userId;
  private String[] rtuRefs;
  private boolean siteWide = false;
  private Long rtuId;

  /**
   * <p>Constructor for CreateRTURequest.</p>
   */
  public CreateRTURequest(){

  }

  /**
   * <p>Constructor for CreateRTURequest.</p>
   *
   * @param solId a {@link java.lang.String} object.
   * @param uId a {@link java.lang.String} object.
   */
  public CreateRTURequest(String solId, String uId) {
    solutionId = solId;
    userId = uId;

  }

  /** {@inheritDoc} */
  @Override
  public String getSolutionId() {
    return solutionId;
  }

  /** {@inheritDoc} */
  @Override
  public void setSolutionId(String solutionId) {
    this.solutionId = solutionId;
  }


  /** {@inheritDoc} */
  @Override
  public void setUserId(String userId) {
    this.userId = userId;
  }

  /** {@inheritDoc} */
  @Override
  public String getUserId() {
    return userId;
  }

  /** {@inheritDoc} */
  @Override
  public String[] getRTURefs() {
    if(rtuRefs == null){
      return new String[]{UUID.randomUUID().toString()};
    }
    return rtuRefs;
  }

  /** {@inheritDoc} */
  @Override
  public void setRtuRefs(String[] rtuRefs) {
    this.rtuRefs = rtuRefs;
  }

  /** {@inheritDoc} */
  @Override
  public Long getRTUId() {
    if(rtuId == null){
      return new Random().nextLong();
    }
    return rtuId;
  }

  /** {@inheritDoc} */
  @Override
  public void setRtuId(long rtuId) {
    this.rtuId = rtuId;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isSiteWide() {
    return siteWide;
  }


  /** {@inheritDoc} */
  @Override
  public void setSiteWide(boolean siteWide) {
    this.siteWide = siteWide;
  }



 

}
