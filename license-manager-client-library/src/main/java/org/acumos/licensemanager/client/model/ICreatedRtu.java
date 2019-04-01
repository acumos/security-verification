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

import org.acumos.cds.domain.MLPRightToUse;

/**
 * <p>
 * The output of the RTU creation process {@link ILicenseCreator}.
 * </p>
 *
 * CreatedRtu
 *
 * @version 0.0.2
 */
public interface ICreatedRtu extends IErrorResponse {
  /**
   * <p>
   * If rtu was updated or there was an error will return false.
   * </p>
   *
   * @return if the RTU was created
   */
  boolean isCreated();

    /**
   * <p>
   * Setter for the field <code>created</code>.
   * </p>
   *
   * @param created indicate that rtu was created
   */
  void setCreated(boolean created);

  /**
   *
   * @return if instead of creating we updated the rtu This will indicate
   *   that the rtu references will be overriden
   */
  boolean isUpdated();

  /**
   * <p>
   * Returns the original request for creating the RTU.
   * </p>
   *
   * @return original request
   */
  ICreateRTURequest getRequest();

  /**
   * <p>
   * Set the request for reference later.
   * </p>
   *
   * @param request the original create request
   */
  void setRequest(ICreateRTURequest request);

  /**
   * <p>
   * Get the CDS {@link org.acumos.cds.domain.MLPRightToUse} created or updated
   * during the processing.
   * </p>
   *
   * @return list of Rtus created in CDS
   */
  List<MLPRightToUse> getRtus();


    /**
   * <p>
   * Adds {@link org.acumos.cds.domain.MLPRightToUse} that was
   * created / updated during processing.
   * </p>
   *
   * @param rightToUse a {@link org.acumos.cds.domain.MLPRightToUse} object.
   */
  void addRtu(MLPRightToUse rightToUse);


    /**
   * <p>
   * Setter for the field <code>updated</code>.
   * </p>
   *
   * @param updated indicate that rtu was updated
   */
  void setUpdated(boolean updated);



}
