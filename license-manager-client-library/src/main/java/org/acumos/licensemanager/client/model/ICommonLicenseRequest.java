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

import java.util.List;

/**
 * <p>
 * When a Common Data service request is made for this library
 * These are the common properties we will need create, update, verify
 * a right to use.
 * </p>
 *
 * CreatedRtu
 *
 * @version 0.0.2
 */
public interface ICommonLicenseRequest {
  /**
   * <p>
   * Get the solution ID used in CCDS queries.
   * </p>
   *
   * @return a {@link java.lang.String} object.
   */
  String getSolutionId();

  /**
   * <p>
   * Get list of userIds that will be used to  verify/create/update a RTU.
   * </p>
   *
   * @return a {@link java.lang.String} object.
   */
  List<String> getUserIds();


}
