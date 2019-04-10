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

package org.acumos.licensemanager.client;

import java.lang.invoke.MethodHandles;
import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * LicenseVerifier will verify that user or site has the RTU for a solution id for a specific
 * action.
 *
 * <p>In Boreas release the action we only have one RTU for all actions.
 */
public class SolutionRevisionVerifier {

  /** Logger for any exceptions that happen while creating a RTU with CDS. */
  private static final Logger LOGGER =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  /** dataClient must be provided by consumer of this library. */
  private final ICommonDataServiceRestClient dataClient;

  /**
   * Constructor for LicenseVerifier.
   *
   * @param dataServiceClient a {@link org.acumos.cds.client.ICommonDataServiceRestClient} object.
   */
  public SolutionRevisionVerifier(final ICommonDataServiceRestClient dataServiceClient) {
    this.dataClient = dataServiceClient;
  }

  // public final ILicenseVerification verifyRevision(final String revision, final String userId)
  //     throws RightToUseException {
  //       // revision --> solution

  //       // dataClient.getSolutionRevision(arg0, arg1);
  //       LicenseVerifier verifier = new LicenseVerifier(dataClient);

  //   }

}
