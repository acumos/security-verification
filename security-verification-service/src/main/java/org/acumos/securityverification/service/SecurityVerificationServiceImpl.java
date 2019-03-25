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
package org.acumos.securityverification.service;

import java.io.File;

import org.acumos.cds.AccessTypeCode;
import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.domain.MLPDocument;
import org.acumos.cds.domain.MLPSiteConfig;
import org.acumos.securityverification.utils.SVServiceConstants;
import org.acumos.securityverification.utils.SVUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class SecurityVerificationServiceImpl extends AbstractServiceImpl implements ISecurityVerificationService {

	Logger logger = LoggerFactory.getLogger(SecurityVerificationServiceImpl.class);
	
	@Autowired
	private Environment env;
	
	public void setEnvironment (Environment env1){
		env = env1;
	}
	
	@Override
	public String securityVerification(String solutionId, String revisionId) throws Exception {

		logger.debug("Inside SecurityVerificationServiceImpl");
		
		
		return null;
	}

	@Override
	public String createSiteConfigCall() {
		
		String siteConfigJsonFromConfiguration = env.getProperty("siteConfig.verification");
		logger.debug("siteConfig.verification  {} ",siteConfigJsonFromConfiguration);
		ICommonDataServiceRestClient client = getCcdsClient();
		MLPSiteConfig config = new MLPSiteConfig();
		config.setConfigKey(SVServiceConstants.CONFIGKEY);
		config.setConfigValue(siteConfigJsonFromConfiguration);
		config.setUserId("26fcd4bf-8819-41c1-b46c-87ec2f7a39f8"); // TODO Need to be discuss, user table C_User role map
		logger.debug("Before createSiteConfig...");
		MLPSiteConfig mlpSiteConfigFromDB = (MLPSiteConfig) client.createSiteConfig(config);
		logger.debug("After createSiteConfig...");
		return mlpSiteConfigFromDB.getConfigValue();
	}
	
	
	
}
