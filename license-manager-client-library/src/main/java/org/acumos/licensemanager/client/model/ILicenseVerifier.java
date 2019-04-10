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

package org.acumos.licensemanager.client.model;

import org.acumos.licensemanager.exceptions.RightToUseException;

/**
 * <p>
 * An object that verifies the license by using the right to use authority.
 * </p>
 *
 * @version 0.0.2
 */
public interface ILicenseVerifier {

  /**
   * <p>
   * Checks for siteWide RTU for a solutionId
   * If no siteWide RTU exists check for userId.
   * </p>
   *
   * @param licenseDownloadRequest a
   *   {@link org.acumos.licensemanager.client.model.IVerifyLicenseRequest}
   *  object.
   * @return a
   *   {@link org.acumos.licensemanager.client.model.ILicenseVerification}
   *   object.
   * @throws org.acumos.licensemanager.exceptions.RightToUseException
   *  when exception was not able to be verified
   */
  ILicenseVerification verifyRTU(
      IVerifyLicenseRequest licenseDownloadRequest)
        throws RightToUseException;
}