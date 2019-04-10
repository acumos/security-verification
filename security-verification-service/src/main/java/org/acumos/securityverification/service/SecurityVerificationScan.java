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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.UUID;

import org.acumos.cds.AccessTypeCode;
import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.domain.MLPDocument;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.nexus.client.NexusArtifactClient;
import org.acumos.nexus.client.RepositoryLocation;
import org.acumos.securityverification.exception.AcumosServiceException;
import org.acumos.securityverification.utils.SVServiceConstants;
import org.acumos.securityverification.utils.SecurityVerificationServiceUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

public class SecurityVerificationScan implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	String solutionId;
	String revisionId;
	Environment env;

	SecurityVerificationScan(String solutionId, String revisionId, Environment env1) {
		this.solutionId = solutionId;
		this.revisionId = revisionId;
		this.env = env1;
	}

	@Override
	public void run() {
		logger.debug("SecurityVerification thread is created");
		UUID uidNumber = UUID.randomUUID();
		String folder = uidNumber.toString();
		try {
			updateVerifiedLicenseStatus(solutionId, "in-progress");
			SecurityVerificationServiceUtils.executeScript(SVServiceConstants.SCRIPTFILE_DUMP_MODEL, solutionId,
					revisionId, folder, env);
			SecurityVerificationServiceUtils.executeScript(SVServiceConstants.SCRIPTFILE_LICENSE_SCAN, solutionId,
					revisionId, folder, env);
			// Upload scanresult.json
			String scanResultJsonFilePath = scanOutJsonLocation(folder, SVServiceConstants.SCAN_RESULT_JSON);
			File scanResultJsonFile = SecurityVerificationServiceUtils.readScanOutput(scanResultJsonFilePath);
			logger.debug("scanResultJsonFile: {}", scanResultJsonFile);
			uploadToArtifact(solutionId, revisionId, scanResultJsonFile);
			logger.debug("ScanResult Json uploadToArtifact successfully");
			// Upload scancode.json
			String scanCodeJsonFilePath = scanOutJsonLocation(folder, SVServiceConstants.SCAN_CODE_JSON);
			File scanCodeJsonFile = SecurityVerificationServiceUtils.readScanOutput(scanCodeJsonFilePath);
			logger.debug("scanCodeJsonFile: {}", scanCodeJsonFile);
			uploadToArtifact(solutionId, revisionId, scanCodeJsonFile);
			if(scanResultVerifiedLicensStatus(scanResultJsonFilePath).equalsIgnoreCase("ture")) {
				updateVerifiedLicenseStatus(solutionId, "successful");
			}
			if(scanResultVerifiedLicensStatus(scanResultJsonFilePath).equalsIgnoreCase("false")) {
				updateVerifiedLicenseStatus(solutionId, "failed");
			}

		} catch (Exception e) {
			logger.debug("Exception: ", e);
		}

	}

	
	private void uploadToArtifact(String solutionId, String revisionId, File file)
			throws AcumosServiceException, FileNotFoundException {
		long fileSizeByKB = file.length();
		if (fileSizeByKB > 0) {
			logger.debug("in side if conditoin fileSizeByKB  {}", fileSizeByKB);
			ICommonDataServiceRestClient client = getCcdsClient();
			MLPSolution mlpSolution = client.getSolution(solutionId);
			String userId = mlpSolution.getUserId();
			UploadArtifactSVOutput uploadArtifactSVOutput = new UploadArtifactSVOutput();
			MLPDocument document = uploadArtifactSVOutput.addRevisionDocument(solutionId, revisionId,
					AccessTypeCode.PR.toString(), userId, file);
		}
	}

	private void updateVerifiedLicenseStatus(String solutionId, String verifiedLicense) {

		ICommonDataServiceRestClient client = getCcdsClient();
		List<MLPSolutionRevision> mlpSolutionRevisions = client.getSolutionRevisions(solutionId);
		for (MLPSolutionRevision mlpSolutionRevision : mlpSolutionRevisions) {
			mlpSolutionRevision.setVerifiedLicense(verifiedLicense);
			client.updateSolutionRevision(mlpSolutionRevision);
		}

	}

	private ICommonDataServiceRestClient getCcdsClient() {
		ICommonDataServiceRestClient client = new CommonDataServiceRestClientImpl(
				env.getProperty(SVServiceConstants.CDMS_CLIENT_URL),
				env.getProperty(SVServiceConstants.CDMS_CLIENT_USER),
				env.getProperty(SVServiceConstants.CDMS_CLIENT_PWD), null);
		return client;
	}

	private String scanOutJsonLocation(String folder,String jsonFlieName) {
		StringBuilder scanJsonOutFliePath = new StringBuilder();
		scanJsonOutFliePath.append(SVServiceConstants.SECURITY_SCAN);
		scanJsonOutFliePath.append(SVServiceConstants.FORWARD_SLASH);
		scanJsonOutFliePath.append(folder);
		scanJsonOutFliePath.append(jsonFlieName);
		return scanJsonOutFliePath.toString();
	}

	private String scanResultVerifiedLicensStatus(String jsonFilePath) throws Exception {
		String verifiedLicenseStatus =null;
		try {
		    JSONParser parser = new JSONParser();
	        JSONObject data = (JSONObject) parser.parse(new FileReader(jsonFilePath));
	        String json = data.toJSONString();
	    	logger.trace("scanresult.Json: {}",json);
			 verifiedLicenseStatus = (String) data.get("verifiedLicense");
			logger.debug("verifiedLicenseStatus: {}",verifiedLicenseStatus);
		} catch (Exception e) {
			 logger.debug("Exception: {}",e);
			 throw e;
		}
		return verifiedLicenseStatus;
	}

}
