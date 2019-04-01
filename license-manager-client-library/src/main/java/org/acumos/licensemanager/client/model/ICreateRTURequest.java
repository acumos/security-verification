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

import java.util.List;

/**
 * Construct a request to create a RTU.
 *
 * @version 0.0.2
 */
public interface ICreateRTURequest extends ILicenseRequest {

  /**
   * <p>
   * Optional - a list of Strings which will be converted to
   * {@link org.acumos.cds.domain.MLPRtuReference} during creation of the RTU.
   * Recomended we use UUID for each rtu reference to be inline with
   * new RTU system in Clio.
   * </p>
   *
   * @return list of UUIs used for creating a MLPRtuReference
   * @see org.acumos.cds.domain.MLPRtuReference
   */
  List<String> getRTURefs();

  /**
   * <p>
   * If set to true then the request to create a RTU will apply to
   * every user using the acumos platform.
   * If set to false then the RTU being created is a user specific
   * RTU. Default is false.
   * </p>
   *
   * @return Is the right to use specific to a user or applicable site wide
   * @see org.acumos.cds.domain.MLPRightToUse#site
   */
  boolean isSiteWide();

  /**
   * <p>
   * Set to true if you want a solution to have a site wide right to use.
   * This avoid having to create a RTU for every user.
   * </p>
   *
   * @param siteWide create rtu for solution for entire site
   * @see org.acumos.cds.domain.MLPRightToUse#site
   */
  void setSiteWide(boolean siteWide);

  /**
   * <p>
   * Returns the RTUId that will be created.
   * </p>
   *
   * @return Right to use ID
   * @see org.acumos.cds.domain.MLPRightToUse#rtuId
   */
  Long getRTUId();

  /**
   * <p>
   * Sets the RTUId.
   * </p>
   *
   * @param rtuId id for the right to use
   * @see org.acumos.cds.domain.MLPRightToUse#rtuId
   */
  void setRtuId(long rtuId);

  /**
   * In Boreas only supporting one generated UUID for the
   * right to use. If you want to add additional rtuRefs
   * you can use this api to update and all refs will be created.
   * This is not a required property when creating a right to use.
   *
   * @param rtuRefs UUID for each right to use
   * @see org.acumos.cds.domain.MLPRightToUse#rtuReferences
   */
  void setRtuRefs(List<String> rtuRefs);

  /**
   * <p>
   * Provide rtu references as String.
   * </p>
   *
   * @param rtuRefs an array of {@link java.lang.String} objects.
   */
  void setRtuRefs(String[] rtuRefs);





}
