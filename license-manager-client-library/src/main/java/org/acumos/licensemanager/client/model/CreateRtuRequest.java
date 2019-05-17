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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/** CreateRtu class. */
public class CreateRtuRequest extends BaseLicenseRequest implements ICreateRtu, Serializable {

  private static final long serialVersionUID = -8649516345536462977L;

  /** rtu refs to apply to each RTU. */
  private List<String> rtuRefsAsStr = new ArrayList<String>();

  /** Constructor for CreateRTURequest. */
  public CreateRtuRequest() {}

  /**
   * Constructor for CreateRTURequest.
   *
   * @param solId a {@link java.lang.String} object.
   * @param userIds an array of {@link java.lang.String} objects.
   */
  public CreateRtuRequest(final String solId, final String[] userIds) {
    setSolutionId(solId);
    setUserIds(Arrays.asList(userIds));
  }

  /**
   * Constructor for CreateRTURequest.
   *
   * @param solId a {@link java.lang.String} object.
   * @param userIds a {@link java.lang.String} object.
   */
  public CreateRtuRequest(final String solId, final String userIds) {
    setSolutionId(solId);
    addUserId(userIds);
  }

  @Override
  public final List<String> getRtuRefs() {
    if (rtuRefsAsStr.isEmpty()) {
      rtuRefsAsStr.add(UUID.randomUUID().toString());
    }
    return rtuRefsAsStr;
  }

  /**
   * In Boreas only supporting one generated UUID for the right to use. If you want to add
   * additional rtuRefs you can use this api to update and all refs will be created. This is not a
   * required property when creating a right to use.
   *
   * @param rtuRefs UUID for each right to use
   * @see org.acumos.cds.domain.MLPRightToUse#rtuReferences
   */
  public final void setRtuRefs(final List<String> rtuRefs) {
    rtuRefsAsStr = rtuRefs;
  }

  /**
   * Provide rtu references as String.
   *
   * @param rtuRefs an array of {@link java.lang.String} objects.
   */
  public final void setRtuRefs(final String[] rtuRefs) {
    this.rtuRefsAsStr.addAll(Arrays.asList(rtuRefs));
  }
}
