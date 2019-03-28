/*-
 * ===============LICENSE_START=======================================================
 * Acumos Apache-2.0
 * ===================================================================================
 * Copyright (C) 2019 Ericsson Software Technology AB. All rights reserved.
 * ===================================================================================
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
 * ===============LICENSE_END=========================================================
 */

package org.acumos.licensemanager.client.model;

import java.util.ArrayList;
import java.util.List;

import org.acumos.cds.domain.MLPRightToUse;

/**
 * <p>CreatedRtu class.</p>
 *
 *CreatedRtu
 *
 * @author est.tech
 * @version 0.0.2
 */
public class CreatedRtu implements ICreatedRtu{

  ICreateRTURequest request;
  boolean created;
  boolean updated;
  List<MLPRightToUse> rtu = new ArrayList<MLPRightToUse>();
  List<Exception> rtuException = new ArrayList<Exception>();

  /**
   * <p>Constructor for CreatedRtu.</p>
   */
  public CreatedRtu() {
  }

  /** {@inheritDoc} */
  @Override
  public ICreateRTURequest getRequest() {
    return request;
  }

  /** {@inheritDoc} */
  @Override
  public void setRequest(ICreateRTURequest request) {
    this.request = request;
  }

  /**
   * <p>isCreated.</p>
   *
   * @return if rtu was created
   */
  public boolean isCreated() {
    return created;
  }

  /**
   * <p>Setter for the field <code>created</code>.</p>
   *
   * @param created indicate that rtu was created
   */
  public void setCreated(boolean created) {
    this.created = created;
  }

  /**
   * <p>isUpdated.</p>
   *
   * @return if rtu was updated instead of created
   */
  public boolean isUpdated() {
    return updated;
  }

  /**
   * <p>Setter for the field <code>updated</code>.</p>
   *
   * @param updated indicate that rtu was updated
   */
  public void setUpdated(boolean updated) {
    this.updated = updated;
  }


  /**
   * <p>addRtu.</p>
   *
   * @param rightToUse a {@link org.acumos.cds.domain.MLPRightToUse} object.
   */
  public void addRtu(MLPRightToUse rightToUse) {
    rtu.add(rightToUse);
  }

  /** {@inheritDoc} */
  @Override
  public List<MLPRightToUse> getRtus() {
    return rtu;
  }

  /** {@inheritDoc} */
  public void addError(Exception ex) {
    rtuException.add(ex);
  }

  /** {@inheritDoc} */
  @Override
  public List<Exception> getRtuException() {
    return rtuException;
  }

}
