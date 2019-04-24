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

package org.acumos.licensemanager.jsonvalidator.resource;

import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** LicenseJsonSchema class. */
public final class LicenseJsonSchema {

  /** Logger for any exception handling. */
  private static final Logger LOGGER =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  /** Do not instantiate. */
  private LicenseJsonSchema() {}

  /** Name of the json schema for license. */
  private static final String JSONSCHEMANAME = "/license.schema.json";

  /** JsonSchema object for validation of license schema. */
  private static JsonSchema jsonSchema;

  /**
   * Get the license json schema as JsonSchema.
   *
   * @return a {@link com.networknt.schema.JsonSchema} object.
   * @throws java.io.IOException if any.
   */
  public static JsonSchema getSchema() throws IOException {
    if (jsonSchema != null) {
      return jsonSchema;
    }
    JsonSchemaFactory factory = JsonSchemaFactory.getInstance();
    URL schemaUrl = LicenseJsonSchema.class.getResource(JSONSCHEMANAME);
    try (InputStream is = schemaUrl.openStream()) {
      jsonSchema = factory.getSchema(is);
    } catch (IOException e) {
      LOGGER.error("unable to process license schema {}", e);
    }
    return jsonSchema;
  }
}
