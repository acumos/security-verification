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

package org.acumos.licensemanager.exceptions;

import org.springframework.web.client.RestClientResponseException;

/**
 * When getting, updating, or creating a right to use this exception captures the issue.
 *
 * @version 0.0.2
 */
public class RightToUseException extends Exception {

  /** Internal exception being wrapped by RTU exception. */
  private final RestClientResponseException cdsRestClientException;

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

  /**
   * Getter for the field <code>cdsRestClientException</code>.
   *
   * @return the cdsRestClientException
   */
  public final RestClientResponseException getCdsRestClientException() {
    return cdsRestClientException;
  }

  private static final long serialVersionUID = 1L;
}
