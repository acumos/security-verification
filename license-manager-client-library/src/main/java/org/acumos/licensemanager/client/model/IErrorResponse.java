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
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * This file is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ===============LICENSE_END==================================================
 */

package org.acumos.licensemanager.client.model;

import java.util.List;

/**
 * <p>
 *  Object that will contain the errors that are captured during
 *  processing with common data service or other external services.
 * </p>
 *
 *CreatedRtu
 *
 * @version 0.0.2
 */
public interface IErrorResponse {
  /**
   * <p>
   * Adding error that can be handled after complete processing.
   * </p>
   *
   * @param exception placeholder for exception handling after handling all rtus
   */
  void addError(Exception exception);

  /**
   * <p>
   * During processing of request we may capture an exception that can be
   * handled after all requests are made.
   * </p>
   *
   * @return list of exceptions found during processing requests to CDS
   */
  List<Exception> getRtuException();
}
