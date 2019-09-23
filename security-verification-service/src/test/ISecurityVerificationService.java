/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
 * ===================================================================================
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
 * ===============LICENSE_END=========================================================
 */
package org.acumos.securityverification.service;

import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.securityverification.transport.ScanResult;
import org.acumos.securityverification.utils.JsonRequest;

public interface ISecurityVerificationService {

	/**This method starts a Jenkins job for security verification.
	 * @param solutionId
	 * @param revisionId
	 * @param client
	 * @throws Exception
	 */
	public void securityVerification(String solutionId, String revisionId, ICommonDataServiceRestClient client)  throws Exception;

  /**This method saves an external ScanResult for security verification.
	 * @param solutionId
	 * @param revisionId
	 * @param client
	 * @throws Exception
	 */
	public void saveScanResult(String solutionId, String revisionId, JsonRequest<ScanResult> scanResult, ICommonDataServiceRestClient client)  throws Exception;

	/**This method call CCDS site-config to add the site-config json in database.
	 * @param client
	 * @return site-config json string.
	 * @throws Exception
	 */
	public String createSiteConfig(ICommonDataServiceRestClient client) throws Exception;

}