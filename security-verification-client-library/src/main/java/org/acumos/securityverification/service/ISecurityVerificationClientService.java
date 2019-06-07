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

import org.acumos.securityverification.domain.Workflow;


/**
 * Defines the interface of the Java-based library to be imported by Acumos components that will
 * call the S-V service. As described in the spec above section "Security
 * Verification Library", this library will provide a function that will "check
 * if a workflow should proceed, based upon the admin requirements for
 * verification related to that workflow, and the status of verification for a
 * solution/revision".
 */
public interface ISecurityVerificationClientService {

	/** Checks the check if a workflow should proceed, based upon the admin
	 * requirements for verification related to that workflow, and the status of
	 * verification for a solution/revision.
	 * @param solutionId, required field
	 * @param revisionId, required field
	 * @param workflowId, required field
	 * @return workflow information
	 * @throws Exception
	 */
	public Workflow securityVerificationScan(String solutionId, String revisionId, String workflowId, String userId) throws Exception;

}
