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

import org.acumos.cds.domain.MLPRightToUse;

/**
 * <p>
 * Implements {@link org.acumos.licensemanager.client.model.ICreatedRtu} which
 * supports the {@link org.acumos.licensemanager.client.model.ILicenseCreator}.
 * </p>
 *
 * @version 0.0.2
 */
public class CreatedRtu implements ICreatedRtu {

  /**
   * Initial request.
   */
  private ICreateRTURequest initialRequest;

  /**
   * Created RTU in CDS.
   */
  private boolean createdRtu;

  /**
   * Updated RTU in CDS.
   */
  private boolean updatedRtu;

  /**
   * List of RTUs created.
   */
  private List<MLPRightToUse> rtu = new ArrayList<MLPRightToUse>();

  /**
   * <p>
   * Constructor for CreatedRtu.
   * </p>
   */
  public CreatedRtu() {
  }

  @Override
  public final ICreateRTURequest getRequest() {
    return initialRequest;
  }

  /**
   * <p>
   * Set the request for reference later.
   * </p>
   *
   * @param request the original create request
   */
  public final void setRequest(final ICreateRTURequest request) {
    initialRequest = request;
  }


  @Override
  public final boolean isCreated() {
    return createdRtu;
  }


  /**
   * <p>
   * Setter for the field <code>created</code>.
   * </p>
   *
   * @param created indicate that rtu was created
   */
  public final void setCreated(final boolean created) {
    this.createdRtu = created;
  }


  @Override
  public final boolean isUpdated() {
    return updatedRtu;
  }

  /**
   * <p>
   * Setter for the field <code>updated</code>.
   * </p>
   *
   * @param updated indicate that rtu was updated
   */
  public final void setUpdated(final boolean updated) {
    this.updatedRtu = updated;
  }


  /**
   * <p>
   * Adds {@link org.acumos.cds.domain.MLPRightToUse} that was
   * created / updated during processing.
   * </p>
   *
   * @param rightToUse a {@link org.acumos.cds.domain.MLPRightToUse} object.
   */
  public final void addRtu(final MLPRightToUse rightToUse) {
    rtu.add(rightToUse);
  }

  @Override
  public final List<MLPRightToUse> getRtus() {
    return rtu;
  }


}
