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

import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import org.acumos.licensemanager.jsonvalidator.model.LicenseJsonValidationResults;
import org.junit.Test;

/** License Json Validator Test. */
public class LicenseJsonValidatorTest {

  @Test
  public void validLicenseJson() throws Exception {
    JsonNode goodJson = getJsonNodeFromClasspath("/good-license.json");
    LicenseJsonValidator validator = new LicenseJsonValidator();
    LicenseJsonValidationResults results = validator.validateLicenseJson(goodJson);
    assertEquals(true, results.getJsonSchemaErrors().isEmpty());
  }

  @Test
  public void partialLicenseJson() throws Exception {
    JsonNode partialLicense = getJsonNodeFromClasspath("/partial-license.json");
    LicenseJsonValidator validator = new LicenseJsonValidator();
    LicenseJsonValidationResults results = validator.validateLicenseJson(partialLicense);
    System.out.println(results.getJsonSchemaErrors());
    assertEquals(0, results.getJsonSchemaErrors().size());
  }

  @Test
  public void invalidLicenseJson() throws Exception {
    JsonNode badJson = getJsonNodeFromClasspath("/bad-license.json");
    LicenseJsonValidator validator = new LicenseJsonValidator();
    LicenseJsonValidationResults results = validator.validateLicenseJson(badJson);
    assertEquals(false, results.getJsonSchemaErrors().isEmpty());

    JsonNode badJson2 = getJsonNodeFromClasspath("/invalid-types-license.json");
    results = validator.validateLicenseJson(badJson2);
    assertEquals(false, results.getJsonSchemaErrors().isEmpty());
  }

  protected JsonNode getJsonNodeFromClasspath(String name) throws Exception {
    InputStream is1 = LicenseJsonValidatorTest.class.getResourceAsStream(name);

    ObjectMapper mapper = new ObjectMapper();
    JsonNode node = mapper.readTree(is1);
    return node;
  }
}
