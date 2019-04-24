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
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * This file is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ===============LICENSE_END==================================================
 */

package org.acumos.licensemanager.jsonvalidator.model;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.InputStream;
import org.acumos.licensemanager.jsonvalidator.exceptions.LicenseJsonException;

/**
 * License JSON validator Input json in different formats string and will return a report if the
 * json is validated 1. correct json 2. validates against schema
 */
public interface ILicenseJsonValidator {

  /**
   * Validates license json string against license schema.
   *
   * @param jsonString a {@link java.lang.String} object.
   * @return ILicenseJsonVerification results of parsing jsonstring and any errors against json
   *     schema
   * @throws LicenseJsonException if any.
   */
  LicenseJsonValidationResults validateLicenseJson(String jsonString) throws LicenseJsonException;

  /**
   * Validates license json node against license schema.
   *
   * @param node a {@link com.fasterxml.jackson.databind.JsonNode} object.
   * @return a {@link org.acumos.licensemanager.jsonvalidator.model.LicenseJsonValidationResults}
   *     object.
   * @throws LicenseJsonException if any.
   */
  LicenseJsonValidationResults validateLicenseJson(JsonNode node) throws LicenseJsonException;

  /**
   * Validates license json node against license schema using input stream.
   *
   * @param node a {@link com.fasterxml.jackson.databind.JsonNode} object.
   * @return a {@link org.acumos.licensemanager.jsonvalidator.model.LicenseJsonValidationResults}
   *     object.
   * @throws LicenseJsonException if any.
   */
  LicenseJsonValidationResults validateLicenseJson(InputStream node) throws LicenseJsonException;
}
