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



import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acumos.securityverification.config.SVConstants;
import org.acumos.securityverification.model.SVResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController 
@Api(value="Controller to kick off scans",tags="Security Verification Service APIs")
public class ScanController extends AbstractController {

	
	public ScanController() {
		// Property values are injected after the constructor finishes
	}
	

	/**
	 * 
	 */
	@ApiOperation(value = "Invoke scanning as needed, based upon site-config settings that enable scan invocation points in workflows.", 
			response = SVResponse.class)
	@ApiResponses(value = {
			@ApiResponse(code = 500, 
					message = "Something bad happened", response = SVResponse.class),
			@ApiResponse(code = 404, 
				message = "Solution/Revision Not Found", 
				response = SVResponse.class),
			@ApiResponse(code = 400, 
			message = "Request was malformed", 
			response = SVResponse.class),
	})
	@PostMapping(value = SVConstants.API_PATH_SCAN_RESULT) 
	public SVResponse invokeScan(HttpServletResponse response,
			@PathVariable(required = true) String solutionId, 
			@PathVariable(required = true) String revisionId, 
			@PathVariable(required = true) String workflowId ){
		
		return null;
	}
	
}
