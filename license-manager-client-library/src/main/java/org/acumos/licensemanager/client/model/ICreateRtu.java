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

package org.acumos.licensemanager.client.model;

import java.io.Serializable;
import java.util.List;

/** Construct a request to create a RTU. */
public interface ICreateRtu extends ICommonLicenseRequest, Serializable {

  /**
   * Optional - a list of Strings which will be converted to {@link
   * org.acumos.cds.domain.MLPRtuReference} during creation of the RTU. Recomended we use UUID for
   * each rtu reference to be inline with new RTU system in Clio.
   *
   * @return list of UUIs used for creating a MLPRtuReference
   * @see org.acumos.cds.domain.MLPRtuReference
   */
  List<String> getRtuRefs();
}
