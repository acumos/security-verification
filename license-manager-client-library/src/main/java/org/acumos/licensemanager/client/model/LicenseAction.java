
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
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * This file is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ===============LICENSE_END==================================================
 */
package org.acumos.licensemanager.client.model;

/**
 * <p>
 * Actions that require a license / right to use.
 * </p>
 *
 * @version 0.0.2
 */
public enum LicenseAction {
  /**
   * Download action in the portal
   * Or through docker pull.
   */
  DOWNLOAD,
  /**
   *  Deploy action in the portal.
   */
  DEPLOY
}
