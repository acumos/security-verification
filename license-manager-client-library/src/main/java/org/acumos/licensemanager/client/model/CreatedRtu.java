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
import java.util.List;
import org.acumos.cds.domain.MLPRightToUse;

/**
 * Implements {@link org.acumos.licensemanager.client.model.ICreatedRtuResponse} which supports the
 * {@link org.acumos.licensemanager.client.model.ILicenseCreator}.
 */
public class CreatedRtu implements ICreatedRtuResponse, Serializable {

  private static final long serialVersionUID = 8159918115256669078L;

  /** Initial request. */
  private ICreateRtu initialRequest;

  /** Created RTU in CDS. */
  private boolean createdRtuInCds;

  /** Updated RTU in CDS. */
  private boolean updatedRtuInCds;

  /** List of RTUs created. */
  private List<MLPRightToUse> rtu = new ArrayList<MLPRightToUse>();

  @Override
  public final ICreateRtu getRequest() {
    return initialRequest;
  }

  /**
   * Set the request for reference later.
   *
   * @param request the original create request
   */
  public final void setRequest(final ICreateRtu request) {
    initialRequest = request;
  }

  @Override
  public final boolean isCreated() {
    return createdRtuInCds;
  }

  /**
   * Setter for the field <code>created</code>.
   *
   * @param created indicate that rtu was created
   */
  public final void setCreated(final boolean created) {
    this.createdRtuInCds = created;
  }

  @Override
  public final boolean isUpdated() {
    return updatedRtuInCds;
  }

  /**
   * Setter for the field <code>updated</code>.
   *
   * @param updated indicate that rtu was updated
   */
  public final void setUpdated(final boolean updated) {
    this.updatedRtuInCds = updated;
  }

  /**
   * Adds {@link org.acumos.cds.domain.MLPRightToUse} that was created / updated during processing.
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
