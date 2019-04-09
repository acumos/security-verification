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

package org.acumos.securityverification.utils;

import java.io.StringReader;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.acumos.securityverification.domain.SecurityVerificationCdump;
import org.acumos.securityverification.domain.SecurityVerificationCdumpNode;

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

	public String externalScanValue(JSONObject verificationObject) {
		logger.debug("EXTERNALSCAN");
		String externalScanObject = null;
		if (verificationObject.get(SVConstants.EXTERNALSCAN) != null) {
			externalScanObject = (String) verificationObject.get(SVConstants.EXTERNALSCAN);
			logger.debug("externalScanObject {} ", externalScanObject);
		}
		return externalScanObject;
	}

	public Map<String, String> allowedLicenseMap(JSONObject verificationObject) {
		logger.debug("ALLOWEDLICENSE");
		Map<String, String> allowedLicenseObjectMap = new HashMap<>();
		if (verificationObject.get(SVConstants.ALLOWEDLICENSE) != null) {
			JSONArray allowedLicenseJsonList = (JSONArray) verificationObject.get(SVConstants.ALLOWEDLICENSE);
			for (Object object : allowedLicenseJsonList) {
				JSONObject jj = (JSONObject) object;
				String typeKeyStr = (String) jj.get(SVConstants.TYPE);
				String typeKeyvalue = (String) jj.get(SVConstants.VALUE);
				logger.debug("type: {}  Value:{}", typeKeyStr, typeKeyvalue);
				allowedLicenseObjectMap.put(typeKeyStr, typeKeyvalue);
			}
		}
		return allowedLicenseObjectMap;
	}

	public Map<String, String> siteConfigMap(JSONObject verificationObject, String verificationObjectKey) {
		logger.debug("Creating {} Map", verificationObjectKey);
		Map<String, String> verificationObjectMap = new HashMap<>();
		if (verificationObject.get(verificationObjectKey) != null) {
			JSONObject siteConfigObject = (JSONObject) verificationObject.get(verificationObjectKey);
			for (Object key : siteConfigObject.keySet()) {
				String keyStr = (String) key;
				String keyvalue = (String) siteConfigObject.get(keyStr);
				logger.debug("key: {} value {}", keyStr, keyvalue);
				verificationObjectMap.put(keyStr, keyvalue);
			}
		}
		return verificationObjectMap;
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
			logger.debug("Inside stringToJsonObject. jsonString: {} jsonString.length: {}", jsonString,
					jsonString.length());
			JSONParser parser = new JSONParser();
			JSONObject jsonObject = (JSONObject) parser.parse(jsonString);
			return jsonObject;
		} catch (ParseException e) {
			logger.error("ParseException:", e);
			throw e;
		}
	}
}
