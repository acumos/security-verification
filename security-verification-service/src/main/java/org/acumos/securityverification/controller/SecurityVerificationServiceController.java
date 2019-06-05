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
package org.acumos.securityverification.controller;

import java.lang.invoke.MethodHandles;

import javax.servlet.http.HttpServletResponse;

import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.securityverification.logging.LogConfig;
import org.acumos.securityverification.service.ISecurityVerificationService;
import org.acumos.securityverification.transport.ErrorTransport;
import org.acumos.securityverification.transport.SVResponse;
import org.acumos.securityverification.transport.SuccessTransport;
import org.acumos.securityverification.utils.SVServiceConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
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
	private Environment env;

	public void setEnvironment(Environment env1) {
		env = env1;
	}
	
	@Autowired
	ISecurityVerificationService securityVerificationService;

	@ApiOperation(value = "Security Verification Service Scan.", response = SuccessTransport.class)
	@RequestMapping(value = "/" + SVServiceConstants.SOLUTIONID + "/{solutionId}/" + SVServiceConstants.REVISIONID
			+ "/{revisionId}/" + SVServiceConstants.WORKFLOWID
			+ "/{workflowId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public SVResponse securityVerification(@PathVariable("solutionId") String solutionId,
			@PathVariable("revisionId") String revisionId, @PathVariable("workflowId") String workflowId) {
		logger.debug("Inside securityVerification service scan solutionId  {}  revisionId  {}", solutionId, revisionId);
		try {
			LogConfig.setEnteringMDCs("security-verification-client-library","securityVerificationScan");
			ICommonDataServiceRestClient client = getCcdsClient();
			securityVerificationService.securityVerification(solutionId, revisionId, client);
			LogConfig.clearMDCDetails();
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			logger.warn("securityVerification failed: {}", ex.toString());
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "securityVerification failed", ex);
		}

	}

	@ApiOperation(value = "Add default SiteConfig Verification.")
	@RequestMapping(value = SVServiceConstants.UPDATE_SITE_CONFIG, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public SVResponse siteConfigVerification() throws Exception {
		logger.debug("Inside siteConfigVerification adding default SiteConfig Verification Json");
		try {
			LogConfig.setEnteringMDCs("security-verification-client-library","securityVerificationScan");
			ICommonDataServiceRestClient client = getCcdsClient();
			String siteConfigJson = null;
			if(client!=null)
			siteConfigJson = securityVerificationService.createSiteConfig(client);
			LogConfig.clearMDCDetails();
			return new SuccessTransport(HttpServletResponse.SC_OK, siteConfigJson);
		} catch (Exception ex) {
			logger.warn("createSiteConfig failed: {}", ex.toString());
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "createSiteConfig failed", ex);
		}

	}
	
	private ICommonDataServiceRestClient getCcdsClient() {
		ICommonDataServiceRestClient client = new CommonDataServiceRestClientImpl(
				env.getProperty(SVServiceConstants.CDMS_CLIENT_URL),
				env.getProperty(SVServiceConstants.CDMS_CLIENT_USER),
				env.getProperty(SVServiceConstants.CDMS_CLIENT_PWD), null);
		return client;
	}

}
