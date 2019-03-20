/*-
* ===============LICENSE_START=======================================================
 * Acumos Apache-2.0
 * ===================================================================================
 * Copyright (C) 2019 Ericsson Software Technology AB. All rights reserved.
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
package org.acumos.licensemanager.service.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class LicenseResponse implements IErrorResponse {

  private Map<String, Boolean> allowedToUse;
  List<Exception> rtuException = new ArrayList<Exception>();


  public LicenseResponse() {
    if (allowedToUse == null) {
      allowedToUse = new HashMap<String, Boolean>();
    }

  }

  public void addWorkflow(String workflow, boolean allowed) {
    allowedToUse.put(workflow, allowed);
  }

  /**
   * @return the allowedToUse
   */
  public Map<String, Boolean> getAllowedToUse() {
    return ImmutableMap.copyOf(allowedToUse);
  }

  @Override
  public void addError(Exception ex) {
    rtuException.add(ex);
  }

    /**
   * @return the rtuException
   */
  public List<Exception> getRtuException() {
    return rtuException;
  }

}
