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

package org.acumos.licensemanager.jsonvalidator.exceptions;

import java.io.Serializable;

/** When getting, updating, or creating a right to use this exception captures the issue. */
public class LicenseJsonException extends Exception implements Serializable {

  /** Internal exception being wrapped by RTU exception. */
  private final Exception jsonParseException;

  /**
   * Creates exception for any RTU operation error.
   *
   * @param message provide text for message
   * @param restException rest client response error
   */
  public LicenseJsonException(final String message, final Exception restException) {
    super(message);
    jsonParseException = restException;
  }

  /**
   * Creates exception for any RTU operation error.
   *
   * @param message provide text for message
   */
  public LicenseJsonException(final String message) {
    super(message);
    jsonParseException = null;
  }

  /**
   * Getter for the field <code>jsonParseException</code>.
   *
   * @return the jsonParseException
   */
  public final Exception getJsonParseException() {
    return jsonParseException;
  }

  private static final long serialVersionUID = -7896327154019469541L;
}
