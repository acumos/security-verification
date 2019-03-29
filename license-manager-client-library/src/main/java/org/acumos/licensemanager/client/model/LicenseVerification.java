/*-
* ===============LICENSE_START=======================================================
 * Acumos Apache-2.0
 * ===================================================================================
 * Copyright (C) 2019 Nordix Foundation.
 * ===================================================================================
 * This Acumos software file is distributed by AT&T and Tech Mahindra
 *  under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  This file is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * ===============LICENSE_END=========================================================
*/
package org.acumos.licensemanager.client.model;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

/**
 * <p>LicenseVerification class.</p>
 *
 * Verified that there is a right to use for specified userId and solutionID
 *
 * @author est.tech
 * @version 0.0.2
 */
public class LicenseVerification implements ILicenseVerification {

  private Map<LicenseAction, Boolean> allowedToUse;
  List<Exception> rtuException = new ArrayList<Exception>();


  /**
   * <p>Constructor for LicenseVerification.</p>
   */
  public LicenseVerification() {
    if (allowedToUse == null) {
      allowedToUse = new EnumMap<LicenseAction, Boolean>(LicenseAction.class);
    }
  }

  /**
   * <p>addAction.</p>
   *
   * @param action a {@link org.acumos.licensemanager.client.model.LicenseAction} object.
   * @param allowed a boolean.
   */
  public void addAction(LicenseAction action, boolean allowed) {
    allowedToUse.put(action, allowed);
  }

  /**
   * <p>Getter for the field <code>allowedToUse</code>.</p>
   *
   * @return the allowedToUse
   */
  public Map<LicenseAction, Boolean> getAllowedToUse() {
    return ImmutableMap.copyOf(allowedToUse);
  }

  /** {@inheritDoc} */
  @Override
  public void addError(Exception ex) {
    rtuException.add(ex);
  }

  /**
   * <p>Getter for the field <code>rtuException</code>.</p>
   *
   * @return the rtuException
   */
  public List<Exception> getRtuException() {
    return rtuException;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isAllowed(LicenseAction action) {
    if(allowedToUse.get(action) == null){
      return false;
    }
    return allowedToUse.get(action).booleanValue();
  }

}
