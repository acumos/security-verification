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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.domain.MLPArtifact;
import org.acumos.cds.domain.MLPCatalog;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.securityverification.exception.AcumosServiceException;
import org.acumos.securityverification.utils.SVServiceConstants;
import org.acumos.securityverification.utils.SecurityVerificationServiceUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

public class SecurityVerificationScan implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private String solutionId;
	private String revisionId;
	private Environment env;
	private ICommonDataServiceRestClient client;

	SecurityVerificationScan(String solutionId, String revisionId, Environment env1,
			ICommonDataServiceRestClient client) {
		this.solutionId = solutionId;
		this.revisionId = revisionId;
		this.env = env1;
		this.client = client;
	}

	@Override
	public void run() {
		logger.debug("SecurityVerification thread is created");
		UUID uidNumber = UUID.randomUUID();
		String folder = uidNumber.toString();
		try {
			updateVerifiedLicenseStatus(solutionId, "IP");
			SecurityVerificationServiceUtils.executeScript(SVServiceConstants.SCRIPTFILE_DUMP_MODEL, solutionId,
					revisionId, folder, env);
			// Upload scanresult.json
			String scanResultJsonFilePath = scanOutJsonLocation(folder, SVServiceConstants.SCAN_RESULT_JSON);
			File scanResultJsonFile = SecurityVerificationServiceUtils.readScanOutput(scanResultJsonFilePath);
			logger.debug("scanResultJsonFile: {}", scanResultJsonFile);
			uploadToArtifact(solutionId, revisionId, scanResultJsonFile);
			// Upload scancode.json
			String scanCodeJsonFilePath = scanOutJsonLocation(folder, SVServiceConstants.SCAN_CODE_JSON);
			File scanCodeJsonFile = SecurityVerificationServiceUtils.readScanOutput(scanCodeJsonFilePath);
			logger.debug("scanCodeJsonFile: {}", scanCodeJsonFile);
			uploadToArtifact(solutionId, revisionId, scanCodeJsonFile);
			if(scanResultVerifiedLicensStatus(scanResultJsonFilePath).equalsIgnoreCase("true")) {
				updateVerifiedLicenseStatus(solutionId, "SU");
			}
			if(scanResultVerifiedLicensStatus(scanResultJsonFilePath).equalsIgnoreCase("false")) {
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
		List<MLPSolutionRevision> mlpSolutionRevisions = client.getSolutionRevisions(solutionId);
		for (MLPSolutionRevision mlpSolutionRevision : mlpSolutionRevisions) {
			mlpSolutionRevision.setVerifiedLicense(verifiedLicense);
			client.updateSolutionRevision(mlpSolutionRevision);
		}

	}

	private String scanOutJsonLocation(String folder,String jsonFileName) {
		logger.debug("Inside scanOutJsonLocation");
		StringBuilder scanJsonOutFilePath = new StringBuilder();
		scanJsonOutFilePath.append(SVServiceConstants.FORWARD_SLASH);
		scanJsonOutFilePath.append(SVServiceConstants.MAVEN);
		scanJsonOutFilePath.append(SVServiceConstants.FORWARD_SLASH);
		scanJsonOutFilePath.append(SVServiceConstants.SECURITY_SCAN);
		scanJsonOutFilePath.append(SVServiceConstants.FORWARD_SLASH);
		scanJsonOutFilePath.append(folder);
		scanJsonOutFilePath.append(jsonFileName);
		return scanJsonOutFilePath.toString();
	}

	private String scanResultVerifiedLicensStatus(String jsonFilePath) throws Exception {
		logger.debug("Inside scanResultVerifiedLicensStatus");
		String verifiedLicenseStatus =null;
		try {
		    JSONParser parser = new JSONParser();
	        JSONObject data = (JSONObject) parser.parse(new FileReader(jsonFilePath));
	        String json = data.toJSONString();
	    	logger.trace("scanresult.Json: {}",json);
			 verifiedLicenseStatus = (String) data.get("verifiedLicense");
			logger.debug("scanresult.Json: verifiedLicenseStatus: {}",verifiedLicenseStatus);
		} catch (Exception e) {
			 logger.debug("Exception: {}",e);
			 throw e;
		}
		return verifiedLicenseStatus;
	}

}
