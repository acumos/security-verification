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
import java.io.FileNotFoundException;
import java.lang.invoke.MethodHandles;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.acumos.cds.AccessTypeCode;
import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.domain.MLPDocument;
import org.acumos.cds.domain.MLPSiteConfig;
import org.acumos.securityverification.exception.AcumosServiceException;
import org.acumos.securityverification.utils.SVServiceConstants;
import org.acumos.securityverification.utils.SecurityVerificationServiceUtils;
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
			MLPSiteConfig mlpSiteConfig = client.getSiteConfig(SVServiceConstants.CONFIGKEY);
			if (StringUtils.isEmpty(mlpSiteConfig)) {
				logger.debug("initialize site_config");
				createSiteConfig();
			}
		}
	}

	@Override
	public String securityVerification(String solutionId, String revisionId) throws Exception {

		logger.debug("Inside SecurityVerificationServiceImpl");
		UUID uidNumber = UUID.randomUUID();
		String folder = uidNumber.toString();
		logger.debug("Before executeScript..");
		
		SecurityVerificationServiceUtils.executeScript(SVServiceConstants.SCRIPTFILE_DUMP_MODEL, solutionId, revisionId,
				folder);
		SecurityVerificationServiceUtils.executeScript(SVServiceConstants.SCRIPTFILE_LICENSE_SCAN, solutionId,
				revisionId, folder);

		logger.debug("After executeScript..");
		// TODO Need add logic
		File fileScanCodeJson = SecurityVerificationServiceUtils.readScanOutput(SVServiceConstants.SECURITY_SCAN
				+ SVServiceConstants.BACKSLASH + folder + SVServiceConstants.SCAN_CODE_JSON);
		uploadToArtifact(solutionId, revisionId, fileScanCodeJson);
		File fileScanresultJson = SecurityVerificationServiceUtils.readScanOutput(SVServiceConstants.SECURITY_SCAN
				+ SVServiceConstants.BACKSLASH + folder + SVServiceConstants.SCAN_RESULT_JSON);
		uploadToArtifact(solutionId, revisionId, fileScanresultJson);
		// SecurityVerificationServiceUtils.readScript(SVServiceConstants.SCAN_OUTPUT_LOCATION,SVServiceConstants.SCAN_CODE_JSON);
		// SecurityVerificationServiceUtils.readScript(SVServiceConstants.SCAN_OUTPUT_LOCATION,SVServiceConstants.SCAN_RESULT_JSON);

		return null;
	}

	@Override
	public String createSiteConfig() {
		String siteConfigJsonFromConfiguration = env.getProperty("siteConfig.verification");
		logger.debug("siteConfig.verification  {} ", siteConfigJsonFromConfiguration);
		ICommonDataServiceRestClient client = getCcdsClient();
		MLPSiteConfig config = new MLPSiteConfig();
		config.setConfigKey(SVServiceConstants.CONFIGKEY);
		config.setConfigValue(siteConfigJsonFromConfiguration);
		logger.debug("Before createSiteConfig...");
		String ss = "";
		MLPSiteConfig mlpSiteConfigFromDB = (MLPSiteConfig) client.createSiteConfig(config);
		logger.debug("After createSiteConfig...");
		return mlpSiteConfigFromDB.getConfigValue();
	}

	private void uploadToArtifact(String solutionId, String revisionId, File file)
			throws AcumosServiceException, FileNotFoundException {
		long fileSizeByKB = file.length();
		if (fileSizeByKB > 0) {
			logger.debug("in side if conditoin fileSizeByKB  {}", fileSizeByKB);
			// String userId= mlpSolution.getUserId();//TODO Need to be discussed
			String userId = "";// TODO Need to be discussed, do we need to pass is via client or do we need to
								// call server and get it.
			UploadArtifactSVOutput uploadArtifactSVOutput = new UploadArtifactSVOutput();
			MLPDocument document = uploadArtifactSVOutput.addRevisionDocument(solutionId, revisionId,
					AccessTypeCode.PR.toString(), userId, file);
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
