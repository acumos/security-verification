/*-
* ===============LICENSE_START=======================================================
 * Acumos Apache-2.0
 * ===================================================================================
 * Copyright (C) 2019 Nordix Foundation.
 * ===================================================================================
 * This Acumos software file is distributed by AT&T and Tech Mahindra
 *  under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  This file is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * ===============LICENSE_END=========================================================
*/

package org.acumos.licensemanager.client.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>VerifyLicenseRequest class.</p>
 *
 *CreatedRtu
 *
 * @author est.tech
 * @version 0.0.2
 */
public class VerifyLicenseRequest implements IVerifyLicenseRequest {

  private List<LicenseAction> action = new ArrayList<LicenseAction>();
  private String solutionId;
  private String userId;

  /**
   * <p>Constructor for VerifyLicenseRequest.</p>
   */
  public VerifyLicenseRequest() {
  }

  /**
   * <p>Constructor for VerifyLicenseRequest.</p>
   *
   * @param action a {@link org.acumos.licensemanager.client.model.LicenseAction} object.
   * @param solutionId a {@link java.lang.String} object.
   * @param userId a {@link java.lang.String} object.
   */
  public VerifyLicenseRequest(LicenseAction action, String solutionId, String userId) {
    this.action.add(action);
    this.solutionId = solutionId;
    this.userId = userId;
  }

  /**
   * <p>Constructor for VerifyLicenseRequest.</p>
   *
   * @param action an array of {@link org.acumos.licensemanager.client.model.LicenseAction} objects.
   * @param solutionId a {@link java.lang.String} object.
   * @param userId a {@link java.lang.String} object.
   */
  public VerifyLicenseRequest(LicenseAction[] action, String solutionId, String userId) {
    this.action = Arrays.asList(action);
    this.solutionId = solutionId;
    this.userId = userId;
  }

  /**
   * <p>Constructor for VerifyLicenseRequest.</p>
   *
   * @param action a {@link java.util.List} object.
   * @param solutionId a {@link java.lang.String} object.
   * @param userId a {@link java.lang.String} object.
   */
  public VerifyLicenseRequest(List<LicenseAction> action, String solutionId, String userId) {
    this.action = action;
    this.solutionId = solutionId;
    this.userId = userId;
  }

  /** {@inheritDoc} */
  @Override
  public void setActions(List<LicenseAction> action) {
    this.action = action;
  }

  /** {@inheritDoc} */
  @Override
  public List<LicenseAction> getActions() {
    return action;
  }

  /** {@inheritDoc} */
  @Override
  public void addAction(LicenseAction action) {
    this.action.add(action);
  }

  /** {@inheritDoc} */
  @Override
  public void setSolutionId(String solutionId) {
    this.solutionId = solutionId;
  }

  /** {@inheritDoc} */
  @Override
  public String getSolutionId() {
    return solutionId;
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



}
