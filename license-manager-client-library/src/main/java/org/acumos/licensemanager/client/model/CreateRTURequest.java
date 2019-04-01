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
 */

package org.acumos.licensemanager.client.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * <p>
 * CreateRTURequest class.
 * </p>
 *
 * CreatedRtu
 *
 * @version 0.0.2
 */
public class CreateRTURequest implements ICreateRTURequest {
  /**
   * Solution Id for CDS.
   */
  private String solutionIdCds;
  /**
   * userIds to create RTUs for.
   */
  private List<String> userIdsCds = new ArrayList<String>();

  /**
   * rtu refs to apply to each RTU.
   */
  private List<String> rtuRefsAsStr = new ArrayList<String>();

  /**
   *  siteWide RTU.
   */
  private boolean siteWideRtu = false;

  /**
   * rtuId for identifying rtu.
   */
  private Long rtuIdCds;

  /**
   * <p>
   * Constructor for CreateRTURequest.
   * </p>
   */
  public CreateRTURequest() {

  }

  /**
   * <p>
   * Constructor for CreateRTURequest.
   * </p>
   *
   * @param solId a {@link java.lang.String} object.
   * @param uIds  an array of {@link java.lang.String} objects.
   */
  public CreateRTURequest(final String solId, final String[] uIds) {
    solutionIdCds = solId;
    userIdsCds.addAll(Arrays.asList(uIds));
  }

  /**
   * <p>
   * Constructor for CreateRTURequest.
   * </p>
   *
   * @param solId a {@link java.lang.String} object.
   * @param uId   a {@link java.lang.String} object.
   */
  public CreateRTURequest(final String solId, final String uId) {
    solutionIdCds = solId;
    userIdsCds.add(uId);
  }

  @Override
  public final String getSolutionId() {
    return solutionIdCds;
  }

  @Override
  public final void setSolutionId(final String solutionId) {
    solutionIdCds = solutionId;
  }

  @Override
  public final void setUserIds(final List<String> userIds) {
    userIdsCds = userIds;
  }

  @Override
  public final void addUserId(final String userId) {
    userIdsCds.add(userId);
  }

  @Override
  public final List<String> getUserIds() {
    return userIdsCds;
  }

  @Override
  public final List<String> getRTURefs() {
    if (rtuRefsAsStr == null) {
      rtuRefsAsStr.add(UUID.randomUUID().toString());
    }
    return rtuRefsAsStr;
  }

  @Override
  public final void setRtuRefs(final List<String> rtuRefs) {
    rtuRefsAsStr = rtuRefs;
  }

  @Override
  public final void setRtuRefs(final String[] rtuRefs) {
    this.rtuRefsAsStr.addAll(Arrays.asList(rtuRefs));
  }

  @Override
  public final Long getRTUId() {
    if (rtuIdCds == null) {
      return new Random().nextLong();
    }
    return rtuIdCds;
  }

  @Override
  public final void setRtuId(final long rtuId) {
    rtuIdCds = rtuId;
  }

  @Override
  public final boolean isSiteWide() {
    return siteWideRtu;
  }

  @Override
  public final void setSiteWide(final boolean siteWide) {
    siteWideRtu = siteWide;
  }

}
