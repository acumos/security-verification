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

import java.util.List;

/**
 * A request object that must be created before calling {@link
 * org.acumos.licensemanager.client.model .ILicenseVerifier#verifyRtu(IVerifyLicenseRequest)}.
 * CreatedRtu
 */
public interface IVerifyLicenseRequest extends ICommonLicenseRequest {

  /**
   * Get the list of actions to be verified.
   *
   * @return a {@link java.util.List} object.
   */
  List<LicenseAction> getActions();
}
