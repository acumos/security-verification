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

package org.acumos.securityverification.utils;

import java.io.StringReader;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.acumos.securityverification.domain.AllowedLicense;
import org.acumos.securityverification.domain.LicenseScan;
import org.acumos.securityverification.domain.LicenseVerify;
import org.acumos.securityverification.domain.SecurityScan;
import org.acumos.securityverification.domain.SecurityVerificationCdump;
import org.acumos.securityverification.domain.SecurityVerificationCdumpNode;
import org.acumos.securityverification.domain.SecurityVerify;
import org.acumos.securityverification.domain.Verification;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecurityVerificationJsonParser {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public SecurityVerificationCdump parseCdumpJsonFile(String jsonString) throws Exception {

		logger.debug(" parseCdumpJsonFile in ParseJSON Start");
		SecurityVerificationCdump securityVerificationCdump = new SecurityVerificationCdump();
		try {
			Object obj = new JSONParser().parse(new StringReader(jsonString));
			JSONObject jo = (JSONObject) obj;
			String version = (String) jo.get("version");
			logger.debug("\n Revision version::-- {}", version);

			securityVerificationCdump.setCname(jo.get("cname").toString());
			securityVerificationCdump.setVersion(jo.get("version").toString());
			securityVerificationCdump.setCid(jo.get("cid").toString());
			securityVerificationCdump.setSolutionId(jo.get("solutionId").toString());
			List<SecurityVerificationCdumpNode> securityVerificationCdumpNodes = new ArrayList<>();

			JSONArray nodes = (JSONArray) jo.get(SVConstants.NODES);
			if (nodes != null && !nodes.isEmpty()) {
				Iterator node = nodes.iterator();
				int nodeCount = 0;
				while (node.hasNext()) {
					Iterator<Map.Entry> itr4 = ((Map) node.next()).entrySet().iterator();
					logger.debug("Nodes {}", nodeCount);

					SecurityVerificationCdumpNode securityVerificationCdumpNode = new SecurityVerificationCdumpNode();
					while (itr4.hasNext()) {
						Map.Entry pair = itr4.next();
						String key = (String) pair.getKey();
						String val = (String) pair.getValue().toString();
						if (pair.getKey().toString().equals("name")) {
							logger.debug("name::-- {}", pair.getValue());
							securityVerificationCdumpNode.setName(pair.getValue().toString());
						}
						if (pair.getKey().toString().equals("nodeId")) {
							logger.debug("nodeId::-- {}", pair.getValue());
							securityVerificationCdumpNode.setNodeId(pair.getValue().toString());
						}
						if (pair.getKey().toString().equals("nodeSolutionId")) {
							logger.debug("nodeSolutionId::-- {}", pair.getValue());
							securityVerificationCdumpNode.setNodeSolutionId(pair.getValue().toString());
						}
						if (pair.getKey().toString().equals("nodeVersion")) {
							logger.debug("NodeVersion::-- {}", pair.getValue());
							securityVerificationCdumpNode.setNodeVersion(pair.getValue().toString());
						}
					}
					securityVerificationCdumpNodes.add(securityVerificationCdumpNode);
				}
				securityVerificationCdump.setNodes(securityVerificationCdumpNodes);
			}

		} catch (Exception e) {
			logger.error("parseJsonFile failed {}", e);
			throw e;
		}
		logger.debug("parseJsonFile in ParseJSON End");
		return securityVerificationCdump;
	}

	public Verification parseSiteConfigJson(JSONObject jsonObject) {

		JSONObject verificationObject = (JSONObject) jsonObject.get("verification");

		Verification verification = new Verification();

		if (verificationObject.get(SVConstants.LICENSESCAN) != null) {
			JSONObject licenseScanObject = (JSONObject) verificationObject.get(SVConstants.LICENSESCAN);
			LicenseScan licenseScan = new LicenseScan();
			licenseScan.setCreated(Boolean.valueOf(licenseScanObject.get(SVConstants.CREATED).toString()));
			licenseScan.setDeploy(Boolean.valueOf(licenseScanObject.get(SVConstants.DEPLOY).toString()));
			licenseScan.setDownload(Boolean.valueOf(licenseScanObject.get(SVConstants.DOWNLOAD).toString()));
			licenseScan
					.setPublishCompany(Boolean.valueOf(licenseScanObject.get(SVConstants.PUBLISHCOMPANY).toString()));
			licenseScan.setPublishPublic(Boolean.valueOf(licenseScanObject.get(SVConstants.PUBLISHPUBLIC).toString()));
			licenseScan.setShare(Boolean.valueOf(licenseScanObject.get(SVConstants.SHARE).toString()));
			licenseScan.setUpdated(Boolean.valueOf(licenseScanObject.get(SVConstants.UPDATED).toString()));
			verification.setLicenseScan(licenseScan);
		}

		if (verificationObject.get(SVConstants.LICENSESCAN) != null) {
			JSONObject securityScanObject = (JSONObject) verificationObject.get(SVConstants.SECURITYSCAN);
			SecurityScan securityScan = new SecurityScan();
			securityScan.setCreated(Boolean.valueOf(securityScanObject.get(SVConstants.CREATED).toString()));
			securityScan.setDeploy(Boolean.valueOf(securityScanObject.get(SVConstants.DEPLOY).toString()));
			securityScan.setDownload(Boolean.valueOf(securityScanObject.get(SVConstants.DOWNLOAD).toString()));
			securityScan
					.setPublishCompany(Boolean.valueOf(securityScanObject.get(SVConstants.PUBLISHCOMPANY).toString()));
			securityScan
					.setPublishPublic(Boolean.valueOf(securityScanObject.get(SVConstants.PUBLISHPUBLIC).toString()));
			securityScan.setShare(Boolean.valueOf(securityScanObject.get(SVConstants.SHARE).toString()));
			securityScan.setUpdated(Boolean.valueOf(securityScanObject.get(SVConstants.UPDATED).toString()));
			verification.setSecurityScan(securityScan);

		}

		if (verificationObject.get(SVConstants.SECURITYSCAN) != null) {
			JSONObject securityScanObject = (JSONObject) verificationObject.get(SVConstants.SECURITYSCAN);
			SecurityScan securityScan = new SecurityScan();
			securityScan.setCreated(Boolean.valueOf(securityScanObject.get(SVConstants.CREATED).toString()));
			securityScan.setDeploy(Boolean.valueOf(securityScanObject.get(SVConstants.DEPLOY).toString()));
			securityScan.setDownload(Boolean.valueOf(securityScanObject.get(SVConstants.DOWNLOAD).toString()));
			securityScan
					.setPublishCompany(Boolean.valueOf(securityScanObject.get(SVConstants.PUBLISHCOMPANY).toString()));
			securityScan
					.setPublishPublic(Boolean.valueOf(securityScanObject.get(SVConstants.PUBLISHPUBLIC).toString()));
			securityScan.setShare(Boolean.valueOf(securityScanObject.get(SVConstants.SHARE).toString()));
			securityScan.setUpdated(Boolean.valueOf(securityScanObject.get(SVConstants.UPDATED).toString()));
			verification.setSecurityScan(securityScan);
		}

		if (verificationObject.get(SVConstants.LICENSEVERIFY) != null) {
			JSONObject licenseVerifyObject = (JSONObject) verificationObject.get(SVConstants.LICENSEVERIFY);
			LicenseVerify licenseVerify = new LicenseVerify();
			licenseVerify.setDeploy(Boolean.valueOf(licenseVerifyObject.get(SVConstants.DEPLOY).toString()));
			licenseVerify.setDownload(Boolean.valueOf(licenseVerifyObject.get(SVConstants.DOWNLOAD).toString()));
			licenseVerify
					.setPublishCompany(Boolean.valueOf(licenseVerifyObject.get(SVConstants.PUBLISHCOMPANY).toString()));
			licenseVerify
					.setPublishPublic(Boolean.valueOf(licenseVerifyObject.get(SVConstants.PUBLISHPUBLIC).toString()));
			licenseVerify.setShare(Boolean.valueOf(licenseVerifyObject.get(SVConstants.SHARE).toString()));
			verification.setLicenseVerify(licenseVerify);
		}

		if (verificationObject.get(SVConstants.SECURITYVERIFY) != null) {
			JSONObject securityVerifyObject = (JSONObject) verificationObject.get(SVConstants.SECURITYVERIFY);
			SecurityVerify securityVerify = new SecurityVerify();
			securityVerify.setDeploy(Boolean.valueOf(securityVerifyObject.get(SVConstants.DEPLOY).toString()));
			securityVerify.setDownload(Boolean.valueOf(securityVerifyObject.get(SVConstants.DOWNLOAD).toString()));
			securityVerify.setPublishCompany(
					Boolean.valueOf(securityVerifyObject.get(SVConstants.PUBLISHCOMPANY).toString()));
			securityVerify
					.setPublishPublic(Boolean.valueOf(securityVerifyObject.get(SVConstants.PUBLISHPUBLIC).toString()));
			securityVerify.setShare(Boolean.valueOf(securityVerifyObject.get(SVConstants.SHARE).toString()));
			verification.setSecurityVerify(securityVerify);
		}

		if (verificationObject.get(SVConstants.EXTERNALSCAN) != null) {
			String externalScanObject = (String) verificationObject.get(SVConstants.EXTERNALSCAN);
			logger.debug("\n externalScanObject::-- {} ", externalScanObject);
			verification.setExternalScan(Boolean.valueOf(externalScanObject));

		}

		if (verificationObject.get(SVConstants.ALLOWEDLICENSE) != null) {
			JSONArray allowedLicenseJsonList = (JSONArray) verificationObject.get(SVConstants.ALLOWEDLICENSE);
			List<AllowedLicense> allowedLicenseList = new ArrayList<>();
			for (Object object : allowedLicenseJsonList) {
				JSONObject jj = (JSONObject) object;
				logger.debug("type: {}  Value {}  ", jj.get(SVConstants.TYPE), jj.get(SVConstants.VALUE));
				AllowedLicense allowedLicense = new AllowedLicense();
				allowedLicense.setType(jj.get(SVConstants.TYPE).toString());
				allowedLicense.setValue(jj.get(SVConstants.VALUE).toString());
				allowedLicenseList.add(allowedLicense);
			}
			verification.setAllowedLicense(allowedLicenseList);
		}
		return verification;
	}

	/**
	 * @param jsonObject
	 * @param key
	 * @param subClassNameKey
	 * @return
	 */
	public String parseSiteConfigJson(JSONObject jsonObject, String key, String subClassNameKey) {

		JSONObject verificationObject = (JSONObject) jsonObject.get(key);
		JSONObject subObject = (JSONObject) verificationObject.get(subClassNameKey);
		return (String) subObject.get(key);
	}

	/**
	 * @param jsonString
	 * @return
	 * @throws Exception
	 */
	public JSONObject stringToJsonObject(String jsonString) throws Exception {
		try {
			logger.debug("jsonString::: {} jsonString.length::: {}", jsonString, jsonString.length());
			String strTemp1 = jsonString.replaceAll("\\\\\"", "\"");
			String strTemp2 = strTemp1.substring(1, strTemp1.length() - 1);
			JSONParser parser = new JSONParser();
			JSONObject jsonObject = (JSONObject) parser.parse(strTemp2);
			return jsonObject;
		} catch (ParseException e) {
			logger.error("Exception Occurred Json parsing", e);
			throw e;
		}
	}
}
