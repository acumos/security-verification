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

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * When making a request for a license RTU consolidating
 * common functionality.
 * </p>
 */
public abstract class BaseLicenseRequest implements ICommonLicenseRequest {

  /**
   * Solution Id for CDS.
   */
  private String solutionIdCds;
  /**
   * userIds to create RTUs for.
   */
  private List<String> userIdsCds = new ArrayList<String>();

  /**
   * <p>
   * Set the solution ID used in CCDS queries.
   * </p>
   *
   * @param solutionId a {@link java.lang.String} object.
   */
  public final void setSolutionId(final String solutionId) {
    solutionIdCds = solutionId;
  }

  /**
   * <p>
   * Set list of userIds that will be used to verify/create/update a RTU.
   * </p>
   *
   * @param userIds a List of userIds.
   */
  public final void setUserIds(final List<String> userIds) {
    userIdsCds = userIds;
  }

  /**
   * <p>
   * Adds a user ID that will be used to verify/create/update a RTU.
   * </p>
   *
   * @param userId a {@link java.lang.String} object.
   */
  public final void addUserId(final String userId) {
    userIdsCds.add(userId);
  }


  @Override
  public final String getSolutionId() {
    return solutionIdCds;
  }


  @Override
  public final List<String> getUserIds() {
    return userIdsCds;
  }





}
