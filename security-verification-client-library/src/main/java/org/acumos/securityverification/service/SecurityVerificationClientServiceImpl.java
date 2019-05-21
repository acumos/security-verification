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

import java.io.ByteArrayOutputStream;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Map;

import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.domain.MLPArtifact;
import org.acumos.cds.domain.MLPSiteConfig;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.licensemanager.client.LicenseVerifier;
import org.acumos.licensemanager.client.model.ILicenseVerification;
import org.acumos.licensemanager.client.model.ILicenseVerifier;
import org.acumos.licensemanager.client.model.LicenseAction;
import org.acumos.licensemanager.client.model.VerifyLicenseRequest;
import org.acumos.nexus.client.NexusArtifactClient;
import org.acumos.nexus.client.RepositoryLocation;
import org.acumos.securityverification.domain.SecurityVerificationCdump;
import org.acumos.securityverification.domain.SecurityVerificationCdumpNode;
import org.acumos.securityverification.domain.Workflow;
import org.acumos.securityverification.logging.LogConfig;
import org.acumos.securityverification.transport.SVResonse;
import org.acumos.securityverification.utils.SVConstants;
import org.acumos.securityverification.utils.SecurityVerificationJsonParser;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class SecurityVerificationClientServiceImpl implements ISecurityVerificationClientService {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private String securityVerificationApiUrl;
	private String cdmsClientUrl;
	private String cdmsClientUsername;
	private String cdmsClientPwd;
	private String nexusClientUrl;
	private String nexusClientUsername;
	private String nexusClientPwd;

	private Map<String, String> licenseScan;
	private Map<String, String> securityScan;
	private Map<String, String> licenseVerify;
	private Map<String, String> securityVerify;
	private Map<String, String> allowedLicenseObjectMap;
	private String externalScanObject;

	public SecurityVerificationClientServiceImpl(final String securityVerificationApiUrl, final String cdmsClientUrl,
			final String cdmsClientUsername, final String cdmsClientPwd, final String nexusClientUrl,
			final String nexusClientUsername, final String nexusClientPwd) {

		this.securityVerificationApiUrl = securityVerificationApiUrl;
		this.cdmsClientUrl = cdmsClientUrl;
		this.cdmsClientUsername = cdmsClientUsername;
		this.cdmsClientPwd = cdmsClientPwd;
		this.nexusClientUrl = nexusClientUrl;
		this.nexusClientUsername = nexusClientUsername;
		this.nexusClientPwd = nexusClientPwd;

	}

	public Workflow securityVerificationScan(String solutionId, String revisionId, String worflowId) {
		
		Workflow workflow = new Workflow();
		try {
			LogConfig.setEnteringMDCs("security-verification-client-library","securityVerificationScan");
			logger.info("solutionId: {} revisionId:: {} worflowId:: {}", solutionId, revisionId, worflowId);
			ICommonDataServiceRestClient client = getCcdsClient(cdmsClientUrl, cdmsClientUsername, cdmsClientPwd);

			if (client != null) {
				logger.info("CCDS CALL GET /solution/{solutionId} ");
				MLPSolution mlpSolution = client.getSolution(solutionId);
				if (mlpSolution != null) {

					logger.info("CCDS CALL GET /solution/{solutionId}/revision::--");
					List<MLPSolutionRevision> mlpSolutionRevisions = client.getSolutionRevisions(solutionId);

					String mlpRevisionId = null;
					for (MLPSolutionRevision mlpSolutionRevision : mlpSolutionRevisions) {
						logger.info("mlpSolutionRevision.getRevisionId(): {}", mlpSolutionRevision.getRevisionId());
						if (mlpSolutionRevision.getRevisionId() != null
								&&  revisionId!= null && mlpSolutionRevision.getRevisionId().equals(revisionId)) {
							verificationSiteConfig();
							String userId = mlpSolutionRevision.getUserId();
							mlpRevisionId = mlpSolutionRevision.getRevisionId();
							logger.info("mlpSolutionRevision.getVersion(): {}", mlpSolutionRevision.getVersion());
							// modelTypeCode is PR (predictor) and toolkitTypeCode is CP (composite-solution)
							// GET /revision/{revisionId}/artifact
							if (mlpSolution.getModelTypeCode() != null && mlpSolution.getToolkitTypeCode() != null 
									&& mlpSolution.getModelTypeCode().equalsIgnoreCase(SVConstants.MODEL_TYPE_CODE) && mlpSolution.getToolkitTypeCode()
											.equalsIgnoreCase(SVConstants.TOOL_KIT_TYPE_CODE)) {
								logger.info("It is a Composite Solution and solutionId: {}  revisionId: {} ",solutionId, revisionId);
								List<MLPArtifact> mlpArtifactList = client.getSolutionRevisionArtifacts(null,mlpRevisionId);
								String nexusURI = "";
								nexusURI = mlpArtifactList.stream()
										.filter(mlpArt -> mlpArt.getArtifactTypeCode()
												.equalsIgnoreCase(SVConstants.ARTIFACT_TYPE_CDUMP))
										.findFirst().get().getUri();
								logger.info("mlpArtifact nexusURI: {}", nexusURI);

								ByteArrayOutputStream byteArrayOutputStream = null;
								NexusArtifactClient nexusArtifactClient = getNexusClient(nexusClientUrl,
										nexusClientUsername, nexusClientPwd);

								byteArrayOutputStream = nexusArtifactClient.getArtifact(nexusURI);
								logger.info("getBluePrintNexus: byteArrayOutputStream length: {}",
										byteArrayOutputStream.size());
								logger.info("byteArrayOutputStream.toString(): {}", byteArrayOutputStream.toString());
								
								SecurityVerificationJsonParser parseJSON = new SecurityVerificationJsonParser();
								// using the member nodeSolutionId, retrieves the list of revisions for the solutionId
								SecurityVerificationCdump securityVerificationCdump = parseJSON.parseCdumpJsonFile(byteArrayOutputStream.toString());
								List<SecurityVerificationCdumpNode> securityVerificationCdumpNodes = securityVerificationCdump.getNodes();
								if (securityVerificationCdumpNodes != null) {
									for (SecurityVerificationCdumpNode securityVerificationCdumpNode : securityVerificationCdumpNodes) {
										logger.info("------CDUMP CHILD NODE -------------------");
										List<MLPSolutionRevision> mlpCdumpSolutionRevisions = client.getSolutionRevisions(securityVerificationCdumpNode.getNodeSolutionId());
										for (MLPSolutionRevision mlpCdumpSolutionRevision : mlpCdumpSolutionRevisions) {

											if (securityVerificationCdumpNode.getNodeVersion()
													.equalsIgnoreCase(mlpCdumpSolutionRevision.getVersion())) {
												logger.info(
														" ############################################ \nCdumpNode NodeSolutionId getName: {} "
																+ "\nCdumpNode NodeSolutionId getNodeId: {} "
																+ "\nCdumpNode NodeSolutionId getNodeSolutionId: {}"
																+ "\nCdumpNode NodeSolutionId getNodeVersion: {} "
																+ "\nCdumpNode SolutionRevision getRevisionId {} "
																+ "\nCdumpNode SolutionRevision getVersion {} "
																+ "\n executes the Scan: TRUE "
																+ "\n############################################",
														securityVerificationCdumpNode.getName(),
														securityVerificationCdumpNode.getNodeId(),
														securityVerificationCdumpNode.getNodeSolutionId(),
														securityVerificationCdumpNode.getNodeVersion(),
														mlpCdumpSolutionRevision.getRevisionId(),
														mlpCdumpSolutionRevision.getVersion());
												workflowPermissionDeterminationCompositeSolution(worflowId, workflow,
														client, userId, securityVerificationCdumpNode,
														mlpCdumpSolutionRevision, revisionId);
											}
										}
									}
								}
							} else {
								logger.info("It is a Simple model and solutionId: {}  revisionId: {} ", solutionId,
										revisionId);
									if (mlpSolutionRevision.getRevisionId() != null
											&& mlpSolutionRevision.getRevisionId().equals(revisionId)) {
										workflowPermissionDeterminationSimpleSolution(worflowId, workflow, client,
												userId, solutionId, revisionId);
									}
							}

						} else {
							// ACUMOS-2555
							workflow.setWorkflowAllowed(false);
							workflow.setReason(SVConstants.REASON_NOT_FOUND);
						}

					}

				}

			}
			LogConfig.clearMDCDetails();
		} catch (Exception e) {
			logger.error("Exception", e);
			workflow.setWorkflowAllowed(false);
			workflow.setSvException(e.getMessage());
		}
		return workflow;
	}

	private void workflowPermissionDeterminationCompositeSolution(String worflowId, Workflow workflow,
			ICommonDataServiceRestClient client, String userId,
			SecurityVerificationCdumpNode securityVerificationCdumpNode, MLPSolutionRevision mlpCdumpSolutionRevision,String revisionId)
			throws Exception {
		logger.info("Inside workflowPermissionDeterminationCompositeSolution method call. worflowId: {} userId: {} revisionId: {}",
				worflowId, userId, revisionId);
		ILicenseVerifier licenseVerifier = new LicenseVerifier(client);
		ResponseEntity<SVResonse> svResponse = null;
		boolean rtuFlag = true;
		boolean workFlowAllowed = true;
		StringBuilder reason = new StringBuilder();

		 List<MLPArtifact> mlpArtifactList = client.getSolutionRevisionArtifacts(null, revisionId);
		for (MLPArtifact mlpArtifact : mlpArtifactList) {
			logger.info("getArtifactTypeCode: {} mlpArtifactGetName: {} ",mlpArtifact.getArtifactTypeCode(), mlpArtifact.getName());
			if (mlpArtifact.getArtifactTypeCode().equalsIgnoreCase(SVConstants.ARTIFACT_TYPE_SCANRESULT)
					|| mlpArtifact.getName().equalsIgnoreCase("scanresult.json")) {

				String nexusURI = "";
				nexusURI = mlpArtifactList.stream().filter(
						mlpArt -> mlpArt.getArtifactTypeCode().equalsIgnoreCase(SVConstants.ARTIFACT_TYPE_SCANRESULT))
						.findFirst().get().getUri();
				logger.info("mlpArtifact nexusURI: {}", nexusURI);

				ByteArrayOutputStream byteArrayOutputStream = null;
				try {
					NexusArtifactClient nexusArtifactClient = getNexusClient(nexusClientUrl, nexusClientUsername,
							nexusClientPwd);
					byteArrayOutputStream = nexusArtifactClient.getArtifact(nexusURI);
				} catch (Exception e) {
					logger.info("Exception", e);
					throw e;
				}
				logger.info("get scanresult from Nexus: byteArrayOutputStream length: {}",
						byteArrayOutputStream.size());
				logger.info("byteArrayOutputStream.toString(): {}", byteArrayOutputStream.toString());

				SecurityVerificationJsonParser parseJSON = new SecurityVerificationJsonParser();
				String scanResultRootLicenseType = parseJSON.scanResultRootLicenseType(byteArrayOutputStream.toString());
				logger.info("scanResultRootLicenseType: {} ",scanResultRootLicenseType);
				if (scanResultRootLicenseType != null && scanResultRootLicenseType != "SPDX") {
					if (worflowId.equalsIgnoreCase(SVConstants.DOWNLOAD)) {
						VerifyLicenseRequest licenseDownloadRequest = new VerifyLicenseRequest(LicenseAction.DOWNLOAD,
								securityVerificationCdumpNode.getNodeSolutionId(), userId);
						licenseDownloadRequest.addAction(LicenseAction.DOWNLOAD);
						ILicenseVerification verifyUserRTU = licenseVerifier.verifyRtu(licenseDownloadRequest);
						// returns true or false if rtu exists
						rtuFlag = verifyUserRTU.isAllowed(LicenseAction.DOWNLOAD);
					}
					if (worflowId.equalsIgnoreCase(SVConstants.DEPLOY)) {
						VerifyLicenseRequest licenseDownloadRequest = new VerifyLicenseRequest(LicenseAction.DEPLOY,
								securityVerificationCdumpNode.getNodeSolutionId(), userId);
						licenseDownloadRequest.addAction(LicenseAction.DEPLOY);
						ILicenseVerification verifyUserRTU = licenseVerifier.verifyRtu(licenseDownloadRequest);
						// returns true or false if rtu exists
						rtuFlag = verifyUserRTU.isAllowed(LicenseAction.DEPLOY);
					}
				}
			}
		}
		 
		workFlowAllowed = rtuFlag;
		if (!rtuFlag) {
			reason.append("No Right to use");
		}

		if (isValidInvokeScan(licenseScan, worflowId) || isValidInvokeScan(securityScan, worflowId)) {
			svResponse = invokesScan(securityVerificationCdumpNode.getNodeSolutionId(),
					mlpCdumpSolutionRevision.getRevisionId(), worflowId);
			logger.info(" InvokesScan result StatusCode: {}  StatusCodeValue: {}", svResponse.getStatusCode(),
					svResponse.getStatusCodeValue());
		}

		logger.info("Before isValidWorkFlow:licenseVerify ");
		if (isValidWorkFlow(licenseVerify, worflowId)) {
			if (mlpCdumpSolutionRevision.getVerifiedLicense() != null
					&& mlpCdumpSolutionRevision.getVerifiedLicense().equalsIgnoreCase("in-progress")) {
				logger.info("license scan in-progress");
				if (reason.length() > 1) {
					reason.append(",");
				}
				workFlowAllowed = false;
				reason.append("license scan in-progress");
				
			}
			if (mlpCdumpSolutionRevision.getVerifiedLicense() != null
					&& mlpCdumpSolutionRevision.getVerifiedLicense().equalsIgnoreCase("failed")) {
				logger.info("license scan failed");
				if (reason.length() > 1) {
					reason.append(",");
				}
				workFlowAllowed = false;
				reason.append("license scan failed");
			}
		}

		/*if (workFlowAllowed && !isValidWorkFlow(licenseVerify, worflowId)) {
			workFlowAllowed = false;
		}*/

		/*if (workFlowAllowed && !isValidWorkFlow(securityVerify, worflowId)) {
			workFlowAllowed = false;
		}*/

		workflow.setWorkflowAllowed(workFlowAllowed);
		workflow.setReason(reason.toString());
		logger.info("WorkflowAllowed: {} Reason: {}", workFlowAllowed, reason.toString());
	}

	private void workflowPermissionDeterminationSimpleSolution(String worflowId, Workflow workflow,
			ICommonDataServiceRestClient client, String userId, String solutionId, String revisionId) throws Exception {
		logger.info("Inside workflowPermissionDeterminationSimpleSolution method call. worflowId: {} userId: {} solutionId: {} revisionId: {}",
				worflowId, userId, solutionId, revisionId);
		ILicenseVerifier licenseVerifier = new LicenseVerifier(client);
		ResponseEntity<SVResonse> svResponse = null;
		boolean rtuFlag = true;
		boolean workFlowAllowed = true;
		StringBuilder reason = new StringBuilder();

		List<MLPArtifact> mlpArtifactList = client.getSolutionRevisionArtifacts(null, revisionId);
		for (MLPArtifact mlpArtifact : mlpArtifactList) {
			logger.info("getArtifactTypeCode: {} mlpArtifactGetName: {} ",mlpArtifact.getArtifactTypeCode(),mlpArtifact.getName());
			if (mlpArtifact.getArtifactTypeCode().equalsIgnoreCase(SVConstants.ARTIFACT_TYPE_SCANRESULT)
					|| mlpArtifact.getName().equalsIgnoreCase("scanresult.json")) {

				String nexusURI = "";
				nexusURI = mlpArtifactList.stream().filter(
						mlpArt -> mlpArt.getArtifactTypeCode().equalsIgnoreCase(SVConstants.ARTIFACT_TYPE_SCANRESULT))
						.findFirst().get().getUri();
				logger.info("mlpArtifact nexusURI: {}", nexusURI);

				ByteArrayOutputStream byteArrayOutputStream = null;
				try {
					NexusArtifactClient nexusArtifactClient = getNexusClient(nexusClientUrl, nexusClientUsername,
							nexusClientPwd);
					byteArrayOutputStream = nexusArtifactClient.getArtifact(nexusURI);
				} catch (Exception e) {
					logger.info("Exception", e);
					throw e;
				}
				logger.info("get scanresult from Nexus: byteArrayOutputStream length: {}",
						byteArrayOutputStream.size());
				logger.info("byteArrayOutputStream.toString(): {}", byteArrayOutputStream.toString());

				SecurityVerificationJsonParser parseJSON = new SecurityVerificationJsonParser();
				String scanResultRootLicenseType = parseJSON.scanResultRootLicenseType(byteArrayOutputStream.toString());

				logger.info("scanResultRootLicenseType: {} ",scanResultRootLicenseType);
				if (scanResultRootLicenseType != null && scanResultRootLicenseType != "SPDX") {
					if (worflowId.equalsIgnoreCase(SVConstants.DOWNLOAD)) {
						VerifyLicenseRequest licenseDownloadRequest = new VerifyLicenseRequest(LicenseAction.DOWNLOAD,
								solutionId, userId);
						licenseDownloadRequest.addAction(LicenseAction.DOWNLOAD);
						ILicenseVerification verifyUserRTU = licenseVerifier.verifyRtu(licenseDownloadRequest);
						// returns true or false if rtu exists
						rtuFlag = verifyUserRTU.isAllowed(LicenseAction.DOWNLOAD);
					}
					if (worflowId.equalsIgnoreCase(SVConstants.DEPLOY)) {
						VerifyLicenseRequest licenseDownloadRequest = new VerifyLicenseRequest(LicenseAction.DEPLOY,
								solutionId, userId);
						licenseDownloadRequest.addAction(LicenseAction.DEPLOY);
						ILicenseVerification verifyUserRTU = licenseVerifier.verifyRtu(licenseDownloadRequest);
						// returns true or false if rtu exists
						rtuFlag = verifyUserRTU.isAllowed(LicenseAction.DEPLOY);
					}
				}
			}
		}

		workFlowAllowed = rtuFlag;
		if (!rtuFlag) {
			reason.append("No Right to use");
		}

		if (isValidInvokeScan(licenseScan, worflowId) || isValidInvokeScan(securityScan, worflowId)) {
			svResponse = invokesScan(solutionId, revisionId, worflowId);
			logger.info(" InvokesScan result StatusCode: {}  StatusCodeValue: {}", svResponse.getStatusCode(),svResponse.getStatusCodeValue());
		}
		
		MLPSolutionRevision mlpSolutionRevision = client.getSolutionRevision(solutionId, revisionId);
		logger.info("Before isValidWorkFlow:licenseVerify ");
		if (isValidWorkFlow(licenseVerify, worflowId)) {
			if (mlpSolutionRevision.getVerifiedLicense() != null
					&& mlpSolutionRevision.getVerifiedLicense().equalsIgnoreCase("IP")) {
				logger.info("license scan in-progress, mlpSolutionRevision.getVerifiedLicense: {}", mlpSolutionRevision.getVerifiedLicense());
				if (reason.length() > 1) {
					reason.append(",");
				}
				workFlowAllowed = false;
				reason.append("license scan in-progress");
			}
			if (mlpSolutionRevision.getVerifiedLicense() != null
					&& mlpSolutionRevision.getVerifiedLicense().equalsIgnoreCase("FA")) {
				logger.info("license scan failed, mlpSolutionRevision.getVerifiedLicense:{}", mlpSolutionRevision.getVerifiedLicense());
				if (reason.length() > 1) {
					reason.append(",");
				}
				workFlowAllowed = false;
				reason.append("license scan failed");
			}
		}
		
		/*if (workFlowAllowed && !isValidWorkFlow(licenseVerify, worflowId)) {
			workFlowAllowed = false;
		}

		if (workFlowAllowed && !isValidWorkFlow(securityVerify, worflowId)) {
			workFlowAllowed = false;
		}*/

		workflow.setWorkflowAllowed(workFlowAllowed);
		workflow.setReason(reason.toString());
		logger.info("WorkflowAllowed: {} Reason: {}", workFlowAllowed, reason.toString());
	}
	
	private void verificationSiteConfig() throws Exception {
		logger.info("Inside verificationSiteConfig method call");
		SecurityVerificationJsonParser parseJSON = new SecurityVerificationJsonParser();
		ICommonDataServiceRestClient client = getCcdsClient(cdmsClientUrl, cdmsClientUsername, cdmsClientPwd);
		JSONObject siteConfigDataJsonObj = new JSONObject();
		if (client != null) {
			MLPSiteConfig mlpSiteConfig = client.getSiteConfig(SVConstants.SITE_VERIFICATION_KEY);
			if (mlpSiteConfig != null) {
				siteConfigDataJsonObj = parseJSON.stringToJsonObject(mlpSiteConfig.getConfigValue().toString());
			} else {
				String createSiteConfigUrl = securityVerificationApiUrl + SVConstants.SITE_CONFIG_UPDATE;
				RestTemplate restTemplate = new RestTemplate();
				String siteConfigJson = restTemplate.getForObject(createSiteConfigUrl, String.class);
				siteConfigDataJsonObj = parseJSON.stringToJsonObject(siteConfigJson);
			}
		}
		licenseScan = parseJSON.siteConfigMap(siteConfigDataJsonObj, SVConstants.LICENSESCAN);
		securityScan = parseJSON.siteConfigMap(siteConfigDataJsonObj, SVConstants.SECURITYSCAN);
		licenseVerify = parseJSON.siteConfigMap(siteConfigDataJsonObj, SVConstants.LICENSEVERIFY);
		securityVerify = parseJSON.siteConfigMap(siteConfigDataJsonObj, SVConstants.SECURITYVERIFY);
		allowedLicenseObjectMap = parseJSON.allowedLicenseMap(siteConfigDataJsonObj);
		externalScanObject = parseJSON.externalScanValue(siteConfigDataJsonObj);
		logger.info("licenseScan: {} securityScan: {} licenseVerify: {} securityVerify: {} allowedLicenseObjectMap: {} ",
				licenseScan.size(),securityScan.size(), licenseVerify.size(), securityVerify.size(), allowedLicenseObjectMap.size());
	}

	private boolean isValidInvokeScan(Map<String, String> siteConfigMap, String workFlowId) {
		String siteConfigWorkFlowId = siteConfigMap.get(workFlowId);
		logger.info("Inside isValidInvokeScan. workFlowId: {}  siteConfigWorkFlowId: {}", workFlowId, siteConfigWorkFlowId);
		if (siteConfigMap.containsKey(workFlowId) && siteConfigWorkFlowId.equals(SVConstants.TRUE)) {
			return true;
		}
		return false;
	}

	private boolean isValidWorkFlow(Map<String, String> siteConfigMap, String workFlowId) {
		String siteConfigWorkFlowId = siteConfigMap.get(workFlowId);
		logger.info("Inside isValidWorkFlow. workFlowId: {}  siteConfigWorkFlowId: {}", workFlowId, siteConfigWorkFlowId);
		if (siteConfigMap.containsKey(workFlowId) && siteConfigWorkFlowId.equals(SVConstants.TRUE)) {
			return true;
		}
		return false;
	}

	private ICommonDataServiceRestClient getCcdsClient(final String cdmsClientUrl, final String cdmsClientUsername,
			final String cdmsClientPwd) {
		logger.info("Inside getCcdsClient");
		ICommonDataServiceRestClient client = CommonDataServiceRestClientImpl.getInstance(cdmsClientUrl, cdmsClientUsername,cdmsClientPwd);
		return client;
	}

	private NexusArtifactClient getNexusClient(final String nexusClientUrl, final String nexusClientUsername,
			final String nexusClientPwd) {
		logger.info("Inside getNexusClient");
		RepositoryLocation repositoryLocation = new RepositoryLocation();
		repositoryLocation.setId("1");
		repositoryLocation.setUrl(nexusClientUrl);
		repositoryLocation.setUsername(nexusClientUsername);
		repositoryLocation.setPassword(nexusClientPwd);

		NexusArtifactClient artifactClient = new NexusArtifactClient(repositoryLocation);
		return artifactClient;
	}

	private ResponseEntity<SVResonse> invokesScan(String nodeSolutionId, String revisionId, String worflowId) {
		logger.info("Inside isValidInvokeScan. securityVerificationApiUrl  {}", securityVerificationApiUrl);
		HttpHeaders headers = new HttpHeaders();
		RestTemplate restTemplate = new RestTemplate();
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		String svApiUrl = svApiUrlBuilder(nodeSolutionId, revisionId, worflowId, securityVerificationApiUrl);
		logger.info("Security verification Api Url {}" + svApiUrl);
		ResponseEntity<SVResonse> svResonse = restTemplate.exchange(svApiUrl, HttpMethod.POST, entity, SVResonse.class);
		logger.info("Security verification Api result {}", svResonse.getBody());
		return svResonse;
	}

	private String svApiUrlBuilder(String nodeSolutionId, String revisionId, String worflowId,
			String securityVerificationApiUrl) {
		StringBuilder url = new StringBuilder();
		url.append(securityVerificationApiUrl);
		url.append(SVConstants.FORWARD_SLASH);
		url.append(SVConstants.SOLUTIONID);
		url.append(SVConstants.FORWARD_SLASH);
		url.append(nodeSolutionId);
		url.append(SVConstants.FORWARD_SLASH);
		url.append(SVConstants.REVISIONID);
		url.append(SVConstants.FORWARD_SLASH);
		url.append(revisionId);
		url.append(SVConstants.FORWARD_SLASH);
		url.append(SVConstants.WORKFLOWID);
		url.append(SVConstants.FORWARD_SLASH);
		url.append(worflowId);
		return url.toString();
	}

}