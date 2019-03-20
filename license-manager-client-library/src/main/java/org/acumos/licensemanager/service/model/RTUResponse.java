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

package org.acumos.licensemanager.service.model;

import java.util.ArrayList;
import java.util.List;

import org.acumos.cds.domain.MLPRightToUse;

public class RTUResponse implements IErrorResponse{

  CreateRTURequest request;
  boolean created;
  boolean updated;
  List<MLPRightToUse> rtu = new ArrayList<MLPRightToUse>();
  List<Exception> rtuException = new ArrayList<Exception>();

  public RTUResponse() {
  }

  /**
   * @return the request
   */
  public CreateRTURequest getRequest() {
    return request;
  }

  /**
   * @param request the original create request
   */
  public void setRequest(CreateRTURequest request) {
    this.request = request;
  }

  /**
   * @return if rtu was created
   */
  public boolean isCreated() {
    return created;
  }

  /**
   * @param created indicate that rtu was created
   */
  public void setCreated(boolean created) {
    this.created = created;
  }

  /**
   * @return if rtu was updated instead of created
   */
  public boolean isUpdated() {
    return updated;
  }

  /**
   * @param updated indicate that rtu was updated
   */
  public void setUpdated(boolean updated) {
    this.updated = updated;
  }

  public void addRtu(MLPRightToUse rightToUse) {
    rtu.add(rightToUse);
  }

  public List<MLPRightToUse> getRtus() {
    return rtu;
  }

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
