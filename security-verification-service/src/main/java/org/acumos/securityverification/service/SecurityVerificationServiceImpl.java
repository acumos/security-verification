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

import java.lang.invoke.MethodHandles;

import javax.annotation.PostConstruct;

import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.domain.MLPSiteConfig;
import org.acumos.securityverification.utils.SVServiceConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class SecurityVerificationServiceImpl implements ISecurityVerificationService {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired
	private Environment env;

	public void setEnvironment(Environment env1) {
		env = env1;
	}

	@PostConstruct
	private void initialize() {
		ICommonDataServiceRestClient client = getCcdsClient();
		if (client != null) {
			MLPSiteConfig mlpSiteConfig = client.getSiteConfig(SVServiceConstants.SITE_VERIFICATION_KEY);
			if (StringUtils.isEmpty(mlpSiteConfig)) {
				logger.debug("initialize site_config");
				createSiteConfig();
			}
		}
	}

	@Override
	public void securityVerification(String solutionId, String revisionId) throws Exception {
		logger.debug("Inside securityVerification");
		SecurityVerificationScan securityVerificationRunner = new SecurityVerificationScan(solutionId, revisionId, env);
		Thread t = new Thread(securityVerificationRunner);
		t.start();
	}

	@Override
	public String createSiteConfig() {

		ICommonDataServiceRestClient client = getCcdsClient();
		MLPSiteConfig mlpSiteConfigFromDB = null;
		if (client != null) {
			MLPSiteConfig mlpSiteConfig = client.getSiteConfig(SVServiceConstants.SITE_VERIFICATION_KEY);
			if (StringUtils.isEmpty(mlpSiteConfig)) {
				String siteConfigJsonFromConfiguration = env.getProperty("siteConfig.verification");
				logger.debug("siteConfig.verification  {} ", siteConfigJsonFromConfiguration);
				MLPSiteConfig config = new MLPSiteConfig();
				config.setConfigKey(SVServiceConstants.SITE_VERIFICATION_KEY);
				config.setConfigValue(siteConfigJsonFromConfiguration);
				logger.debug("Before createSiteConfig...");
				String ss = "";
				mlpSiteConfigFromDB = (MLPSiteConfig) client.createSiteConfig(config);
				logger.debug("After createSiteConfig...");
			}
		}
		return mlpSiteConfigFromDB.getConfigValue();
	}

	private ICommonDataServiceRestClient getCcdsClient() {
		ICommonDataServiceRestClient client = new CommonDataServiceRestClientImpl(
				env.getProperty(SVServiceConstants.CDMS_CLIENT_URL),
				env.getProperty(SVServiceConstants.CDMS_CLIENT_USER),
				env.getProperty(SVServiceConstants.CDMS_CLIENT_PWD), null);
		return client;
	}
}