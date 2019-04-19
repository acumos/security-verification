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

package org.acumos.licensemanager.client.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Request object to verify a user's right to use for a specific action passed to the {@link
 * org.acumos.licensemanager.client.model.ILicenseVerifier}.
 */
public class VerifyLicenseRequest extends BaseLicenseRequest implements IVerifyLicenseRequest {

  /** License Actions to be verified. */
  private List<LicenseAction> licenseAction = new ArrayList<LicenseAction>();

  /** Constructor for VerifyLicenseRequest. */
  public VerifyLicenseRequest() {}

  /**
   * Constructor for VerifyLicenseRequest.
   *
   * @param action a {@link org.acumos.licensemanager.client.model.LicenseAction} object.
   * @param solutionId a {@link java.lang.String} object.
   * @param userId a {@link java.lang.String} object.
   */
  public VerifyLicenseRequest(
      final LicenseAction action, final String solutionId, final String userId) {
    this.licenseAction.add(action);
    setSolutionId(solutionId);
    addUserId(userId);
  }

  /**
   * Constructor for VerifyLicenseRequest.
   *
   * @param action an array of {@link org.acumos.licensemanager.client.model.LicenseAction} objects.
   * @param solutionId a {@link java.lang.String} object.
   * @param userId a {@link java.lang.String} object.
   */
  public VerifyLicenseRequest(
      final LicenseAction[] action, final String solutionId, final String userId) {
    this.licenseAction = Arrays.asList(action);
    setSolutionId(solutionId);
    addUserId(userId);
  }

  /**
   * Constructor for VerifyLicenseRequest.
   *
   * @param action a {@link java.util.List} object.
   * @param solutionId a {@link java.lang.String} object.
   * @param userId a {@link java.lang.String} object.
   */
  public VerifyLicenseRequest(
      final List<LicenseAction> action, final String solutionId, final String userId) {
    this.licenseAction = action;
    setSolutionId(solutionId);
    addUserId(userId);
  }

  /**
   * Set the actions to be verified.
   *
   * @param action a {@link java.util.List} object.
   */
  public final void setActions(final List<LicenseAction> action) {
    this.licenseAction = action;
  }

  @Override
  public final List<LicenseAction> getActions() {
    return licenseAction;
  }

  /**
   * Convenience method to add additional actions.
   *
   * @param action {@link org.acumos.licensemanager.client.model.LicenseAction} object.
   */
  public final void addAction(final LicenseAction action) {
    this.licenseAction.add(action);
  }
}
