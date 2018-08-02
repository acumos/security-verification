/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2018 AT&T Intellectual Property. All rights reserved.
 * ===================================================================================
 * This Acumos software file is distributed by AT&T 
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
package org.acumos.securityverification.controller;

import javax.servlet.http.HttpServletResponse;

import org.acumos.securityverification.config.SVConstants;
import org.acumos.securityverification.model.VerificationResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ScanResultsController extends AbstractController {

	@GetMapping(SVConstants.API_PATH_VERIFY + "/" + SVConstants.API_SOLUTION_ID + "/" + SVConstants.API_REVISION_ID
			+ "/" + SVConstants.API_WORKFLOW_ID)
	public VerificationResponse getVerificationStatusFor(@PathVariable(required = true) String solutionId,
			@PathVariable(required = true) String revisionId, @PathVariable(required = true) String workflowId, HttpServletResponse response) {
		// @TODO methodBody
		return null;
	}
}
