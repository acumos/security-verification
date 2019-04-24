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

package org.acumos.licensemanager.jsonvalidator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import org.acumos.licensemanager.jsonvalidator.exceptions.LicenseJsonException;
import org.acumos.licensemanager.jsonvalidator.model.LicenseJsonValidationResults;
import org.junit.Test;
import org.mockito.Mockito;

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
  public void validateErrorHandling() throws Exception {
    ObjectMapper objectMapper = Mockito.mock(ObjectMapper.class);
    Mockito.when(objectMapper.readTree(Mockito.any(JsonParser.class))).thenThrow(IOException.class);

    LicenseJsonValidator validator = new LicenseJsonValidator(objectMapper);

    try {
      validator.validateLicenseJson("{}");
      fail("LicenseJsonException exception expected");
    } catch (LicenseJsonException e) {
      assertEquals("could not convert string to bytes", e.getMessage());
    }
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
  public void badInput() throws Exception {
    LicenseJsonValidator validator = new LicenseJsonValidator();
    InputStream in = Mockito.mock(InputStream.class);
    Mockito.when(in.read()).thenThrow(new IOException());
    InputStream inputStreamSpy = Mockito.spy(in);
    try {
      validator.validateLicenseJson(inputStreamSpy);
      fail("should have thrown error");
    } catch (LicenseJsonException e) {
      assertNotNull(e);
    }
    Mockito.verify(inputStreamSpy).close();
  }

  @Test
  public void partialLicenseJsonAsString() throws Exception {
    String partialLicense = convertStreamToString("/partial-license.json");
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

  private JsonNode getJsonNodeFromClasspath(String name) throws Exception {
    InputStream is1 = LicenseJsonValidatorTest.class.getResourceAsStream(name);
    InputStream inputStreamSpy = Mockito.spy(is1);

    ObjectMapper mapper = new ObjectMapper();
    JsonNode node = mapper.readTree(inputStreamSpy);
    Mockito.verify(inputStreamSpy).close();
    return node;
  }

  private String convertStreamToString(String name) throws IOException {
    InputStream is1 = LicenseJsonValidatorTest.class.getResourceAsStream(name);
    try {
      Scanner s = new Scanner(is1);
      s.useDelimiter("\\A");
      String content = s.hasNext() ? s.next() : "";
      is1.close();
      s.close();
      return content;
    } finally {
      if (is1 != null) {
        is1.close();
      }
    }
  }
}
