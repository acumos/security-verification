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

package org.acumos.licensemanager.jsonvalidator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.ValidationMessage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import org.acumos.licensemanager.jsonvalidator.exceptions.LicenseJsonException;
import org.acumos.licensemanager.jsonvalidator.model.ILicenseJsonValidator;
import org.acumos.licensemanager.jsonvalidator.model.LicenseJsonValidationResults;
import org.acumos.licensemanager.jsonvalidator.resource.LicenseJsonSchema;

/**
 * LicenseJsonValidator will verify the license.json to ensure there are no json errors or json
 * schema errors
 *
 * @version 0.0.2
 */
public class LicenseJsonValidator implements ILicenseJsonValidator {

  @Override
  public LicenseJsonValidationResults validateLicenseJson(String jsonString)
      throws LicenseJsonException {
    // read in json schema
    LicenseJsonValidationResults results = new LicenseJsonValidationResults();

    JsonNode node;
    try {
      node = getJsonNodeFromStringContent(jsonString);
    } catch (Exception e) {
      throw new LicenseJsonException("not able to parse json", e);
    }

    validate(node, results);

    return results;
    // assertThat(errors.size(), is(1));
  }

  @Override
  public final LicenseJsonValidationResults validateLicenseJson(InputStream inputStream)
      throws LicenseJsonException {
    // read in json schema
    LicenseJsonValidationResults results = new LicenseJsonValidationResults();
    ObjectMapper mapper = new ObjectMapper();
    JsonNode node;
    try {
      node = mapper.readTree(inputStream);
    } catch (IOException e) {
      throw new LicenseJsonException("issue reading input", e);
    }
    validate(node, results);

    return results;
    // assertThat(errors.size(), is(1));
  }

  @Override
  public final LicenseJsonValidationResults validateLicenseJson(JsonNode node)
      throws LicenseJsonException {
    // read in json schema
    LicenseJsonValidationResults results = new LicenseJsonValidationResults();

    validate(node, results);

    return results;
    // assertThat(errors.size(), is(1));
  }

  private void validate(final JsonNode node, final LicenseJsonValidationResults results)
      throws LicenseJsonException {
    JsonSchema schema;
    try {
      schema = LicenseJsonSchema.getSchema();
    } catch (IOException e) {
      throw new LicenseJsonException("could not load schema", e);
    }

    Set<ValidationMessage> errors = schema.validate(node);
    results.setJsonSchemaErrors(errors);
  }

  /**
   * getJsonNodeFromStringContent.
   *
   * @param content a {@link java.lang.String} object.
   * @return a {@link com.fasterxml.jackson.databind.JsonNode} object.
   * @throws java.lang.Exception if any.
   */
  protected JsonNode getJsonNodeFromStringContent(final String content) throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode node = mapper.readTree(content);
    return node;
  }
}
