/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2019 Nordix Foundation
 * ===================================================================================
 * This Acumos software file is distributed by Nordix Foundation
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.acumos.cds.domain.MLPRightToUse_;
import org.acumos.cds.transport.RestPageRequest;

public class RtuSearchRequest {

  private String solutionId;
  private boolean site;

  private boolean isOr;
  private RestPageRequest pageRequest;
  private List<String> userIds;

  /* Default constructor */
  public RtuSearchRequest() {}

  public RestPageRequest getPageRequest() {
    return pageRequest;
  }

  public void setPageRequest(RestPageRequest pageRequest) {
    this.pageRequest = pageRequest;
  }

  public boolean isOr() {
    return isOr;
  }

  public void setOr(boolean isOr) {
    this.isOr = isOr;
  }

  public boolean isSite() {
    return site;
  }

  public void setSite(boolean site) {
    this.site = site;
  }

  public String getSolutionId() {
    return solutionId;
  }

  public void setSolutionId(String solutionId) {
    this.solutionId = solutionId;
  }

  @Override
  public String toString() {
    return "RtuSearchRequest [site="
        + site
        + ", solutionId="
        + solutionId
        + ", isOr="
        + isOr
        + ", pageRequest="
        + pageRequest
        + "]";
  }

  public boolean isEmptyOrNullString(String input) {
    boolean isEmpty = false;
    if (null == input || 0 == input.trim().length()) {
      isEmpty = true;
    }
    return isEmpty;
  }

  public Map<String, Object> paramsMap() {
    HashMap<String, Object> map = new HashMap<>();
    if (site) map.put(MLPRightToUse_.SITE, site);
    if (!isEmptyOrNullString(solutionId)) map.put(MLPRightToUse_.SOLUTION_ID, solutionId);

    return map;
  }

  public void setUserIds(List<String> userIds) {
    this.userIds = userIds;
  }

  public List<String> getUserIds() {
    return userIds;
  }
}
