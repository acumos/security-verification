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
 * When a Common Data service request is made for this library These are the common properties we
 * will need create, update, verify a right to use. CreatedRtu
 */
public interface ICommonLicenseRequest {
  /**
   * Get the solution ID used in CCDS queries.
   *
   * @return a {@link java.lang.String} object.
   */
  String getSolutionId();

  /**
   * Get list of userIds that will be used to verify/create/update a RTU.
   *
   * @return a {@link java.lang.String} object.
   */
  List<String> getUserIds();

  /**
   * If set to true then the request to create a RTU will apply to every user using the acumos
   * platform. If set to false then the RTU being created is a user specific RTU. Default is false.
   *
   * @return Is the right to use specific to a user or applicable site wide
   * @see org.acumos.cds.domain.MLPRightToUse#site
   */
  boolean isSiteWide();
}
