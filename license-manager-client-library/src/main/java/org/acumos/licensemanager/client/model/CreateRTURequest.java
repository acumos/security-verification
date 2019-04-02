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
public class CreateRTURequest extends BaseLicenseRequest
  implements ICreateRTURequest {


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
    setSolutionId(solId);
    setUserIds(Arrays.asList(uIds));
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
    setSolutionId(solId);
    addUserId(uId);
  }


  @Override
  public final List<String> getRTURefs() {
    if (rtuRefsAsStr == null) {
      rtuRefsAsStr.add(UUID.randomUUID().toString());
    }
    return rtuRefsAsStr;
  }

  /**
   * In Boreas only supporting one generated UUID for the
   * right to use. If you want to add additional rtuRefs
   * you can use this api to update and all refs will be created.
   * This is not a required property when creating a right to use.
   *
   * @param rtuRefs UUID for each right to use
   * @see org.acumos.cds.domain.MLPRightToUse#rtuReferences
   */
  public final void setRtuRefs(final List<String> rtuRefs) {
    rtuRefsAsStr = rtuRefs;
  }

  /**
   * <p>
   * Provide rtu references as String.
   * </p>
   *
   * @param rtuRefs an array of {@link java.lang.String} objects.
   */
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

  /**
   * <p>
   * Sets the RTUId.
   * </p>
   *
   * @param rtuId id for the right to use
   * @see org.acumos.cds.domain.MLPRightToUse#rtuId
   */
  public final void setRtuId(final long rtuId) {
    rtuIdCds = rtuId;
  }

  @Override
  public final boolean isSiteWide() {
    return siteWideRtu;
  }

  /**
   * <p>
   * Set to true if you want a solution to have a site wide right to use.
   * This avoid having to create a RTU for every user.
   * </p>
   *
   * @param siteWide create rtu for solution for entire site
   * @see org.acumos.cds.domain.MLPRightToUse#site
   */
  public final void setSiteWide(final boolean siteWide) {
    siteWideRtu = siteWide;
  }

}
