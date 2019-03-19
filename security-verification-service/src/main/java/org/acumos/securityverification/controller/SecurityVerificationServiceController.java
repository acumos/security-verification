/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
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
package org.acumos.securityverification.controller;

import org.acumos.securityverification.service.ISecurityVerificationService;
import org.acumos.securityverification.transport.SVResonse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;

@RestController
public class SecurityVerificationServiceController extends AbstractController {

	Logger logger = LoggerFactory.getLogger(SecurityVerificationServiceController.class);
	
	@Autowired
	ISecurityVerificationService securityVerificationService;
	
	@ApiOperation(value = "Security Verification Service Scan.")
	@RequestMapping(value = "/scan/{solutionId}/{revisionId}/{workflowId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<SVResonse> securityVerification(@PathVariable("solutionId") String solutionId,
			@PathVariable("revisionId") String revisionId, @PathVariable("workflowId") String workflowId) {
		logger.debug("Inside security verification server scan... ");
//		String solutionId = SanitizeUtils.sanitize(securityVerificationRequest.getSolutionId());
//        String revisionId = SanitizeUtils.sanitize(securityVerificationRequest.getRevisionId());
        
        logger.debug("securityVerification solutionId {}  revisionId{}",solutionId,revisionId );
        SVResonse svResonse = new SVResonse();
        try {
        	//TODO Need to add logic. development is in progress
        	 securityVerificationService.securityVerification(solutionId,revisionId);
        	 svResonse.setScanSucess("Development is in progress");
        	
        }catch (Exception e) {
        	logger.error("Exception Occurred Security Verification :{}" + "solutionId",e);
		}
		return new ResponseEntity<SVResonse>(svResonse, null, HttpStatus.OK);
	}


}
