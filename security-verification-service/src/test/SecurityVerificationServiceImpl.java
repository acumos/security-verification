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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.lang.invoke.MethodHandles;
import java.lang.StringBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.domain.MLPArtifact;
import org.acumos.cds.domain.MLPCatalog;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.cds.domain.MLPSiteConfig;
import org.acumos.securityverification.exception.AcumosServiceException;
import org.acumos.securityverification.utils.JsonRequest;
import org.acumos.securityverification.utils.SVServiceConstants;
import org.acumos.securityverification.utils.SecurityVerificationServiceUtils;
import org.acumos.securityverification.controller.ScanResult;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientResponseException;

@Service
public class SecurityVerificationServiceImpl implements ISecurityVerificationService {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired
	private Environment env;

	public void setEnvironment(Environment env1) {
		env = env1;
	}

	@PostConstruct
	private void initialize() throws Exception {
		ICommonDataServiceRestClient client = getCcdsClient();
		if (client != null) {
      try {
        MLPSiteConfig mlpSiteConfig = client.getSiteConfig(SVServiceConstants.SITE_VERIFICATION_KEY);
        if (StringUtils.isEmpty(mlpSiteConfig)) {
          logger.debug("initialize site_config");
          createSiteConfig(client);
        }
      } catch (Exception e) {
				logger.debug("initialize() : Connection to CDS failed");
				logger.error("initialize() : Connection to CDS failed with exception ", e);
				try {
					Thread.sleep(20000);
				} catch (InterruptedException ie) {
					logger.error("initialize() : Connection to CDS failed");
				}
			}
		}
	}

	@Override
	public void securityVerification(String solutionId, String revisionId, ICommonDataServiceRestClient client) throws Exception {
		logger.debug("Inside securityVerification");
    try {
      MLPSolutionRevision mlpSolutionRevision = client.getSolutionRevision(solutionId, revisionId);
  		mlpSolutionRevision.setVerifiedLicense("IP");
  		client.updateSolutionRevision(mlpSolutionRevision);
      // Start Jenkins job
		} catch (Exception e) {
			logger.debug("Exception: ", e);
		}
	}

  @Override
	public void saveScanResult(String solutionId, String revisionId, JsonRequest<ScanResult> scanResult, ICommonDataServiceRestClient client) throws Exception {
		logger.debug("Inside saveScanResult");

		try {
			// Upload scanresult.json
      String result = scanResult.getBody().getVerifiedLicense();
      String verifiedLicense = scanResult.getBody().getVerifiedLicense();
      String reason = scanResult.getBody().getReason(); // result.getString("reason");
			logger.debug("scanResult verifiedLicense: {}, reason: {}", verifiedLicense, reason);
      UUID uidNumber = UUID.randomUUID();
  		String folder = uidNumber.toString();
      StringBuilder scanResultJsonFilePath = new StringBuilder();
  		scanResultJsonFilePath.append(SVServiceConstants.FORWARD_SLASH);
  		scanResultJsonFilePath.append(SVServiceConstants.MAVEN);
  		scanResultJsonFilePath.append(SVServiceConstants.FORWARD_SLASH);
  		scanResultJsonFilePath.append(SVServiceConstants.SECURITY_SCAN);
  		scanResultJsonFilePath.append(SVServiceConstants.FORWARD_SLASH);
  		scanResultJsonFilePath.append(folder);
  		scanResultJsonFilePath.append(SVServiceConstants.SCAN_RESULT_JSON);
      File scanResultJsonFile = new File(scanResultJsonFilePath.toString());
      BufferedWriter writer = new BufferedWriter(new FileWriter(scanResultJsonFile));
      writer.write(result.toString());
      writer.close();
      logger.debug("scanResultJsonFile: {}", scanResultJsonFilePath);
  		uploadToArtifact(solutionId, revisionId, scanResultJsonFile);
  	  if(verifiedLicense.equalsIgnoreCase("true")) {
  			updateVerifiedLicenseStatus(solutionId, "SU");
  		}
  		if(verifiedLicense.equalsIgnoreCase("false")) {
  			updateVerifiedLicenseStatus(solutionId, "FA");
  		}

		} catch (Exception e) {
			logger.debug("Exception: ", e);
		}

	}

  private void uploadToArtifact(String solutionId, String revisionId, File file)
			throws AcumosServiceException, FileNotFoundException {
		logger.debug("Inside uploadToArtifact");
		if (file != null) {
			long fileSizeByKB = file.length();
			if (fileSizeByKB > 0) {
				logger.debug("In side if conditoin fileSizeByKB  {}", fileSizeByKB);
				ICommonDataServiceRestClient client = new CommonDataServiceRestClientImpl(env.getProperty("cdms.client.url"), env.getProperty("cdms.client.username"), env.getProperty("cdms.client.password"), null);
				//client.setRequestId(MDC.get(ONAPLogConstants.MDCs.REQUEST_ID));

				MLPSolution mlpSolution = client.getSolution(solutionId);
				String userId = mlpSolution.getUserId();
				List<MLPArtifact> mlpArtifactList = client.getSolutionRevisionArtifacts(null, revisionId);//solutionIdIgnored
				String version = null;
				List<Integer> mlpArtifactVersionList = new ArrayList<>();
				for (MLPArtifact mlpArtifact : mlpArtifactList) {
					mlpArtifactVersionList.add(Integer.parseInt(mlpArtifact.getVersion()));
				}
				version = String.valueOf(findMaxVersion(mlpArtifactVersionList));
				UploadArtifactSVOutput uploadArtifactSVOutput = new UploadArtifactSVOutput(env);
				uploadArtifactSVOutput.addCreateArtifact(solutionId, revisionId, version, userId, file);
			}
		}
	}

	private Integer findMaxVersion(List<Integer> list) {
		logger.debug("Inside findMaxVersion");
		// check list is empty or not
		if (list == null || list.size() == 0) {
			return Integer.MIN_VALUE;
		}
		// create a new list to avoid modification in the original list
		List<Integer> sortedlist = new ArrayList<>(list);
		// sort list in natural order
		Collections.sort(sortedlist);
		// last element in the sorted list would be maximum
		int version = sortedlist.get(sortedlist.size() - 1);
		return version;
	}

	private void updateVerifiedLicenseStatus(String solutionId, String verifiedLicense) {
		logger.debug("Inside updateVerifiedLicenseStatus, solutionId: {} Status: {}",solutionId, verifiedLicense);
		ICommonDataServiceRestClient client = new CommonDataServiceRestClientImpl(env.getProperty("cdms.client.url"), env.getProperty("cdms.client.username"), env.getProperty("cdms.client.password"), null);

		List<MLPSolutionRevision> mlpSolutionRevisions = client.getSolutionRevisions(solutionId);
		for (MLPSolutionRevision mlpSolutionRevision : mlpSolutionRevisions) {
			mlpSolutionRevision.setVerifiedLicense(verifiedLicense);
			client.updateSolutionRevision(mlpSolutionRevision);
		}

	}

	@Override
	public String createSiteConfig(ICommonDataServiceRestClient client) throws Exception {
		logger.debug("Inside createSiteConfig");
		MLPSiteConfig mlpSiteConfigFromDB = null;
		MLPSiteConfig mlpSiteConfig = null;
		try {
			mlpSiteConfig = client.getSiteConfig(SVServiceConstants.SITE_VERIFICATION_KEY);
		} catch (RestClientResponseException ex) {
			logger.error("getSiteConfig failed, server reports: {}", ex.getResponseBodyAsString());
			throw ex;
		}
		if (StringUtils.isEmpty(mlpSiteConfig)) {
			logger.debug("getSiteConfig verification is empty");
			String siteConfigJsonFromConfiguration = env.getProperty("siteConfig.verification");
			logger.debug("siteConfig.verification env: {} ", siteConfigJsonFromConfiguration);
			MLPSiteConfig config = new MLPSiteConfig();
			config.setConfigKey(SVServiceConstants.SITE_VERIFICATION_KEY);
			config.setConfigValue(siteConfigJsonFromConfiguration);
			try {
				logger.debug("Before createSiteConfig");
				mlpSiteConfigFromDB = (MLPSiteConfig) client.createSiteConfig(config);
				logger.debug("After createSiteConfig created");
			} catch (RestClientResponseException ex) {
				logger.error("createSiteConfig failed, server reports: {}", ex.getResponseBodyAsString());
				throw ex;
			}
		}
		return mlpSiteConfigFromDB != null ? mlpSiteConfigFromDB.getConfigValue()
				: "site_config verification already exists";
	}

	private ICommonDataServiceRestClient getCcdsClient() {
		logger.debug("Inside getCcdsClient");
		ICommonDataServiceRestClient client = new CommonDataServiceRestClientImpl(
				env.getProperty(SVServiceConstants.CDMS_CLIENT_URL),
				env.getProperty(SVServiceConstants.CDMS_CLIENT_USER),
				env.getProperty(SVServiceConstants.CDMS_CLIENT_PWD), null);
		return client;
	}
}
