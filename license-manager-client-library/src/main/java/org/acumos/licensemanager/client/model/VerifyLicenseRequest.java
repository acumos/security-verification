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
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * This file is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ===============LICENSE_END==================================================
 * */

package org.acumos.licensemanager.client.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * Request object to verify a user's right to use for a specific action passed
 * to the {@link ILicenseVerifier}.
 * </p>
 *
 *
 * @version 0.0.2
 */
public class VerifyLicenseRequest implements IVerifyLicenseRequest {

  /**
   * License Actions to be verified.
   */
  private List<LicenseAction> licenseAction = new ArrayList<LicenseAction>();

  /**
   * solution ID to look up in CDS.
   */
  private String solutionIdStr;

  /**
   * userIds list.
   */
  private List<String> userIdsList = new ArrayList<>();

  /**
   * <p>
   * Constructor for VerifyLicenseRequest.
   * </p>
   */
  public VerifyLicenseRequest() {
  }

  /**
   *
   * @param action     a
   *     {@link org.acumos.licensemanager.client.model.LicenseAction}
   *     object.
   * @param solutionId a {@link java.lang.String} object.
   * @param userId     a {@link java.lang.String} object.
   */
  public VerifyLicenseRequest(final LicenseAction action,
    final String solutionId,
    final String userId) {
    this.licenseAction.add(action);
    this.solutionIdStr = solutionId;
    this.userIdsList.add(userId);
  }

  /**
   * @param action     an array of
   *      {@link org.acumos.licensemanager.client.model.LicenseAction} objects.
   * @param solutionId a {@link java.lang.String} object.
   * @param userId     a {@link java.lang.String} object.
   */
  public VerifyLicenseRequest(
      final LicenseAction[] action,
      final String solutionId,
      final String userId) {
    this.licenseAction = Arrays.asList(action);
    this.solutionIdStr = solutionId;
    this.userIdsList.add(userId);
  }

  /**
   * <p>
   * Constructor for VerifyLicenseRequest.
   * </p>
   *
   * @param action     a {@link java.util.List} object.
   * @param solutionId a {@link java.lang.String} object.
   * @param userId     a {@link java.lang.String} object.
   */
  public VerifyLicenseRequest(
      final List<LicenseAction> action,
      final String solutionId,
      final String userId) {
    this.licenseAction = action;
    this.solutionIdStr = solutionId;
    this.userIdsList.add(userId);
  }

  @Override
  public final void setActions(final List<LicenseAction> action) {
    this.licenseAction = action;
  }

  @Override
  public final List<LicenseAction> getActions() {
    return licenseAction;
  }

  @Override
  public final void addAction(final LicenseAction action) {
    this.licenseAction.add(action);
  }

  @Override
  public final void setSolutionId(final String solutionId) {
    this.solutionIdStr = solutionId;
  }

  @Override
  public final String getSolutionId() {
    return solutionIdStr;
  }

  @Override
  public final void setUserIds(final List<String> userIds) {
    this.userIdsList = userIds;
  }

  @Override
  public final void addUserId(final String userId) {
    this.userIdsList.add(userId);
  }

  @Override
  public final List<String> getUserIds() {
    return userIdsList;
  }

}
