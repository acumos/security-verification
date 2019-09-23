/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
 * Modifications Copyright (C) 2019 Nordix Foundation.
 * ===================================================================================* This Acumos software file is distributed by AT&T and Tech Mahindra
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

package org.acumos.securityverification.transport;

/** Model for message returned on failure, to be serialized as JSON. */
public class ErrorTransport implements SVServiceResponse {

  private Integer status;
  private String error;
  private String exception;

  /** Builds an empty object. */
  public ErrorTransport() {
    // no-arg constructor
  }

  /**
   * Builds an object with the specified values.
   *
   * @param statusCode Integer value like 400
   * @param errMsg Explanation
   */
  public ErrorTransport(int statusCode, String errMsg) {
    this(statusCode, errMsg, null);
  }

  /**
   * Builds an object with the specified status code, message and a String version of the exception.
   *
   * @param statusCode Integer value like 500
   * @param errMsg Explanation
   * @param exception Exception that should be reported; optional and ignored if null.
   */
  public ErrorTransport(int statusCode, String errMsg, Exception exception) {
    this.status = statusCode;
    this.error = errMsg;
    if (exception != null) {
      final int enough = 512;
      String exString = exception.toString();
      String exceptionMsg = exString.length() > enough ? exString.substring(0, enough) : exString;
      this.exception = exceptionMsg;
    }
  }

  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }

  public String getException() {
    return exception;
  }

  public void setException(String exception) {
    this.exception = exception;
  }
}
