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

package org.acumos.licensemanager.exceptions;

import java.io.Serializable;
import java.util.List;
import org.acumos.cds.domain.MLPRightToUse;
import org.springframework.web.client.RestClientResponseException;

/** When getting, updating, or creating a right to use this exception captures the issue. */
public class RightToUseException extends Exception implements Serializable {

  /** Internal exception being wrapped by RTU exception. */
  private final RestClientResponseException cdsRestClientException;

  private String solutionId;
  private List<MLPRightToUse> rtus;

  /**
   * Creates exception for any RTU operation error.
   *
   * @param message provide text for message
   * @param restException rest client response error
   */
  public RightToUseException(
      final String message, final RestClientResponseException restException) {
    super(message);
    cdsRestClientException = restException;
  }

  public String getSolutionId() {
    return solutionId;
  }

  public RightToUseException setSolutionId(String solutionId) {
    this.solutionId = solutionId;
    return this;
  }

  /**
   * Creates exception for RTU creation errors
   *
   * @param message provide text for message
   * @param solutionId solution id that has an issue
   * @param rtus list of rtus found based on solution
   */
  public RightToUseException(
      final String message, final String solutionId, final List<MLPRightToUse> rtus) {
    super(message);
    cdsRestClientException = null;
    this.setSolutionId(solutionId);
    this.setRtus(rtus);
  }

  /**
   * Getter for the field <code>cdsRestClientException</code>.
   *
   * @return the cdsRestClientException
   */
  public final RestClientResponseException getCdsRestClientException() {
    return cdsRestClientException;
  }

  public List<MLPRightToUse> getRtus() {
    return rtus;
  }

  public RightToUseException setRtus(List<MLPRightToUse> rtus) {
    this.rtus = rtus;
    return this;
  }

  private static final long serialVersionUID = 3714073159231864295L;
}
