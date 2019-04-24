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

package org.acumos.licensemanager.jsonvalidator.main;

import com.networknt.schema.ValidationMessage;
import java.io.File;
import java.io.FileInputStream;
import java.lang.invoke.MethodHandles;
import org.acumos.licensemanager.jsonvalidator.LicenseJsonValidator;
import org.acumos.licensemanager.jsonvalidator.model.LicenseJsonValidationResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** License JSON Verifier Main program. Input to main program: JSON file path */
public class LicenseJsonValidatorMain {

  /** Logger for any exception handling. */
  private static final Logger LOGGER =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  /** No not allow for utility class from being instantiated. */
  protected LicenseJsonValidatorMain() {
    // prevents calls from subclass
    throw new UnsupportedOperationException();
  }

  /**
   * Main program can be used with the following arguments requires position order.
   * LicenseJsonVerifierMain PATHTOJSON
   *
   * @param args an array of {@link java.lang.String} objects.
   * @throws java.lang.Exception if any.
   */
  public static void main(final String[] args) throws Exception {
    String filePath = "";
    if (args.length > 0) {
      filePath = args[0];
    }
    System.out.println("Validating file: " + filePath);
    File file = new File(filePath);
    try {
      FileInputStream fio = new FileInputStream(file);
      LicenseJsonValidator validator = new LicenseJsonValidator();
      LicenseJsonValidationResults results = validator.validateLicenseJson(fio);
      for (ValidationMessage message : results.getJsonSchemaErrors()) {
        System.out.println(message.getMessage());
      }
    } catch (Exception e) {
      System.err.println(e);
      LOGGER.error("not able to validate filePath:" + filePath, e);
      throw e;
    }
  }
}
