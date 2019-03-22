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

import java.lang.invoke.MethodHandles;

import javax.servlet.http.HttpServletResponse;

import org.acumos.securityverification.service.ISecurityVerificationService;
import org.acumos.securityverification.transport.ErrorTransport;
import org.acumos.securityverification.transport.SVResonse;
import org.acumos.securityverification.transport.SuccessTransport;
import org.acumos.securityverification.utils.SVServiceConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;

@RestController
public class SecurityVerificationServiceController extends AbstractController {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	@Autowired
	ISecurityVerificationService securityVerificationService;

	@ApiOperation(value = "Security Verification Service Scan.", response = SuccessTransport.class)
	@RequestMapping(value = "/" + SVServiceConstants.SOLUTIONID + "/{solutionId}/" + SVServiceConstants.REVISIONID
			+ "/{revisionId}/" + SVServiceConstants.WORKFLOWID
			+ "/{workflowId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public SVResonse securityVerification(@PathVariable("solutionId") String solutionId,
			@PathVariable("revisionId") String revisionId, @PathVariable("workflowId") String workflowId) {
		logger.debug("Inside securityVerification service scan... ");
		logger.debug("securityVerification solutionId  {}  revisionId  {}", solutionId, revisionId);

		try {
			securityVerificationService.securityVerification(solutionId, revisionId);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			logger.warn("securityVerification failed: {}", ex.toString());
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "securityVerification failed", ex);
		}

	}

	@ApiOperation(value = "Add default SiteConfig Verification.")
	@RequestMapping(value = SVServiceConstants.UPDATE_SITE_CONFIG, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public SVResonse siteConfigVerification() throws Exception {
		logger.debug("Inside siteConfigVerification adding default SiteConfig Verification Json");

		try {
			String siteConfigJson = securityVerificationService.createSiteConfig();
			return new SuccessTransport(HttpServletResponse.SC_OK, siteConfigJson);
		} catch (Exception ex) {
			logger.warn("createSiteConfig failed: {}", ex.toString());
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "createSiteConfig failed", ex);
		}

	}

}
