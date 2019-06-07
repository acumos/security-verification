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
import org.acumos.securityverification.transport.SVResponse;
import org.acumos.securityverification.utils.SVConstants;
import org.acumos.securityverification.utils.SecurityVerificationJsonParser;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
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

	public Workflow securityVerificationScan(String solutionId, String revisionId, String workflowId, String loggedInUserId) {

		Workflow workflow = new Workflow();
		try {
			LogConfig.setEnteringMDCs("security-verification-client-library", "securityVerificationScan");
			logger.info("Inside securityVerificationScan, solutionId: {} revisionId: {} workflowId: {}", solutionId,
					revisionId, workflowId);
			if (!isScanInputParameterFound(solutionId, revisionId, workflowId)) {
				logger.info(
						"Inside securityVerificationScan, Input parameter not found:: solutionId: {} revisionId: {} workflowId: {}",
						solutionId, revisionId, workflowId);
			}

			ICommonDataServiceRestClient client = getCcdsClient(cdmsClientUrl, cdmsClientUsername, cdmsClientPwd);

			if (client != null && isScanInputParameterFound(solutionId, revisionId, workflowId)) {
				logger.info("CCDS CALL GET /solution/{solutionId} ");
				MLPSolution mlpSolution = client.getSolution(solutionId);

				if (mlpSolution != null) {
					logger.info("CCDS CALL GET /solution/{solutionId}/revision::--");

					MLPSolutionRevision mlpSolutionRevision = client.getSolutionRevision(solutionId, revisionId);

					String mlpRevisionId = null;
					logger.info("mlpSolutionRevision.getRevisionId(): {}", mlpSolutionRevision.getRevisionId());

					if (mlpSolutionRevision != null && mlpSolutionRevision.getRevisionId() != null && revisionId != null
							&& mlpSolutionRevision.getRevisionId().equals(revisionId)) {
						verificationSiteConfig();
						String ownerUserId = mlpSolutionRevision.getUserId();
						mlpRevisionId = mlpSolutionRevision.getRevisionId();
						logger.info("mlpSolutionRevision.getVersion(): {}", mlpSolutionRevision.getVersion());
						// modelTypeCode is PR (predictor) and toolkitTypeCode is CP
						// (composite-solution)
						// GET /revision/{revisionId}/artifact
						if (mlpSolution.getModelTypeCode() != null && mlpSolution.getToolkitTypeCode() != null
								&& mlpSolution.getModelTypeCode().equalsIgnoreCase(SVConstants.MODEL_TYPE_CODE)
								&& mlpSolution.getToolkitTypeCode().equalsIgnoreCase(SVConstants.TOOL_KIT_TYPE_CODE)) {
							logger.info("It is a Composite Solution and solutionId: {}  revisionId: {} ", solutionId,
									revisionId);

							workflowPermissionDeterminationSimpleSolution(workflowId, workflow, client, loggedInUserId,
									ownerUserId, solutionId, revisionId);

							if (workflow.isWorkflowAllowed()) {
								List<MLPArtifact> mlpArtifactList = client.getSolutionRevisionArtifacts(null,
										mlpRevisionId);
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
								// using the member nodeSolutionId, retrieves the list of revisions for the
								// solutionId
								SecurityVerificationCdump securityVerificationCdump = parseJSON
										.parseCdumpJsonFile(byteArrayOutputStream.toString());
								List<SecurityVerificationCdumpNode> securityVerificationCdumpNodes = securityVerificationCdump
										.getNodes();
								if (securityVerificationCdumpNodes != null) {
									for (SecurityVerificationCdumpNode securityVerificationCdumpNode : securityVerificationCdumpNodes) {
										logger.info("------CDUMP CHILD NODE -------------------");
										List<MLPSolutionRevision> mlpCdumpSolutionRevisions = client
												.getSolutionRevisions(securityVerificationCdumpNode.getNodeSolutionId());
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
												workflowPermissionDeterminationCompositeSolution(workflowId, workflow,
														client, loggedInUserId, ownerUserId, securityVerificationCdumpNode,
														mlpCdumpSolutionRevision, revisionId);
											}
										}
									}
								}
							}
						} else {
							logger.info("It is a Simple model and solutionId: {}  revisionId: {} ", solutionId,
									revisionId);
							if (mlpSolutionRevision.getRevisionId() != null
									&& mlpSolutionRevision.getRevisionId().equals(revisionId)) {
								workflowPermissionDeterminationSimpleSolution(workflowId, workflow, client, loggedInUserId,
										ownerUserId, solutionId, revisionId);
							}
						}
					} else {
						// ACUMOS-2555
						workflow.setWorkflowAllowed(false);
						workflow.setReason(SVConstants.REASON_NOT_FOUND);
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

	private void workflowPermissionDeterminationCompositeSolution(String workflowId, Workflow workflow,
			ICommonDataServiceRestClient client, String loggedInUserId, String ownerUserId,
			SecurityVerificationCdumpNode securityVerificationCdumpNode, MLPSolutionRevision mlpCdumpSolutionRevision,
			String revisionId) throws Exception {
		logger.info(
				"Inside workflowPermissionDeterminationCompositeSolution method call. workflowId: {} userId: {} revisionId: {}",
				workflowId, loggedInUserId, revisionId);
		ILicenseVerifier licenseVerifier = new LicenseVerifier(client);
		ResponseEntity<SVResponse> svResponse = null;
		boolean rtuFlag = true;
		boolean workFlowAllowed = true;
		StringBuilder reason = new StringBuilder();
		ByteArrayOutputStream byteArrayOutputStream = null;

		List<MLPArtifact> mlpArtifactList = client.getSolutionRevisionArtifacts(null, revisionId);
		for (MLPArtifact mlpArtifact : mlpArtifactList) {
			logger.info("getArtifactTypeCode: {} mlpArtifactGetName: {} ", mlpArtifact.getArtifactTypeCode(),
					mlpArtifact.getName());
					// Don't use ARTIFACT_TYPE_SCANRESULT as a filter for now, until scancode.json is stored with a different code
					//			if (mlpArtifact.getArtifactTypeCode().equalsIgnoreCase(SVConstants.ARTIFACT_TYPE_SCANRESULT)
					//					|| mlpArtifact.getName().equalsIgnoreCase("scanresult.json")) {
			if (mlpArtifact.getName().equalsIgnoreCase("scanresult.json")) {

				String nexusURI = "";
				nexusURI = mlpArtifactList.stream().filter(
				// Don't use ARTIFACT_TYPE_SCANRESULT as a filter for now, until scancode.json is stored with a different code
				//						mlpArt -> mlpArt.getArtifactTypeCode().equalsIgnoreCase(SVConstants.ARTIFACT_TYPE_SCANRESULT))
						mlpArt -> mlpArt.getName().equalsIgnoreCase("scanresult.json"))
						.findFirst().get().getUri();
				logger.info("mlpArtifact nexusURI: {}", nexusURI);

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

				String scanResultRootLicenseType = SecurityVerificationJsonParser
						.scanResultRootLicenseType(byteArrayOutputStream.toString());
				logger.info("scanResultRootLicenseType: ({}) ", scanResultRootLicenseType);
				// Proprietary models have a recognized licenseType, not null, "", or SPDX
				if (!StringUtils.isEmpty(scanResultRootLicenseType) && !scanResultRootLicenseType.equals("SPDX")) {
					//  if owner skip 
					if(ownerUserId == loggedInUserId){
						return;
					}
					if (workflowId.equalsIgnoreCase(SVConstants.DOWNLOAD)) {
						VerifyLicenseRequest licenseDownloadRequest = new VerifyLicenseRequest(LicenseAction.DOWNLOAD,
								securityVerificationCdumpNode.getNodeSolutionId(), loggedInUserId);
						licenseDownloadRequest.addAction(LicenseAction.DOWNLOAD);
						ILicenseVerification verifyUserRTU = licenseVerifier.verifyRtu(licenseDownloadRequest);
						// returns true or false if rtu exists
						rtuFlag = verifyUserRTU.isAllowed(LicenseAction.DOWNLOAD);
						logger.info("verifyUserRTU.isAllowed: {} ", rtuFlag);
					}
					if (workflowId.equalsIgnoreCase(SVConstants.DEPLOY)) {
						VerifyLicenseRequest licenseDownloadRequest = new VerifyLicenseRequest(LicenseAction.DEPLOY,
								securityVerificationCdumpNode.getNodeSolutionId(), loggedInUserId);
						licenseDownloadRequest.addAction(LicenseAction.DEPLOY);
						ILicenseVerification verifyUserRTU = licenseVerifier.verifyRtu(licenseDownloadRequest);
						// returns true or false if rtu exists
						rtuFlag = verifyUserRTU.isAllowed(LicenseAction.DEPLOY);
						logger.info("verifyUserRTU.isAllowed: {} ", rtuFlag);
					}
				}
			}
		}

		workFlowAllowed = rtuFlag;
		if (!rtuFlag) {
			reason.append("No Right to use");
		}

		if (isValidInvokeScan(licenseScan, workflowId) || isValidInvokeScan(securityScan, workflowId)) {
			svResponse = invokesScan(securityVerificationCdumpNode.getNodeSolutionId(),
					mlpCdumpSolutionRevision.getRevisionId(), workflowId);
			logger.info(" InvokesScan result StatusCode: {}  StatusCodeValue: {}", svResponse.getStatusCode(),
					svResponse.getStatusCodeValue());
		}

		logger.info("Before isValidWorkFlow:licenseVerify ");
		// if user != owner && isValid workflow
		if (ownerUserId != loggedInUserId && isValidWorkFlow(licenseVerify, workflowId)) {
			if (mlpCdumpSolutionRevision.getVerifiedLicense() == null) {
				logger.info("license scan not yet started");
				if (reason.length() > 1) {
					reason.append(",");
				}
				workFlowAllowed = false;
				reason.append("license scan not yet started");
			} else {
				if (mlpCdumpSolutionRevision.getVerifiedLicense().equalsIgnoreCase("IP")) {
					logger.info("license scan in-progress");
					if (reason.length() > 1) {
						reason.append(",");
					}
					workFlowAllowed = false;
					reason.append("license scan in-progress");

				}
				if (mlpCdumpSolutionRevision.getVerifiedLicense().equalsIgnoreCase("FA")) {
					// logger.info("license scan failed, mlpSolutionRevision.getVerifiedLicense:{}",
		 		// 		mlpSolutionRevision.getVerifiedLicense());
					if (reason.length() > 1) {
						reason.append(",");
					}

					SecurityVerificationJsonParser parseJSON = new SecurityVerificationJsonParser();
					String scanResultReason = parseJSON
							.scanResultReason(byteArrayOutputStream.toString());
					logger.info("scanResultReason: {} ", scanResultReason);
					if (scanResultReason != null) {
						reason.append("license scan failed, reason: ");
						reason.append(scanResultReason);
					} else {
						reason.append("license scan failed: unknown reason (null)");
						reason.append(scanResultReason);
					}

					workFlowAllowed = false;
				}
		 }
		}

		workflow.setWorkflowAllowed(workFlowAllowed);
		workflow.setReason(reason.toString());
		logger.info("WorkflowAllowed: {} Reason: {}", workFlowAllowed, reason.toString());
	}

	private void workflowPermissionDeterminationSimpleSolution(String workflowId, Workflow workflow,
			ICommonDataServiceRestClient client, String loggedInUserId, String ownerUserId, String solutionId, String revisionId) throws Exception {
		logger.info(
				"Inside workflowPermissionDeterminationSimpleSolution method call. workflowId: {} userId: {} solutionId: {} revisionId: {}",
				workflowId, loggedInUserId, solutionId, revisionId);
		ILicenseVerifier licenseVerifier = new LicenseVerifier(client);
		ResponseEntity<SVResponse> svResponse = null;
		boolean rtuFlag = true;
		boolean workFlowAllowed = true;
		StringBuilder reason = new StringBuilder();
		ByteArrayOutputStream byteArrayOutputStream = null;

		List<MLPArtifact> mlpArtifactList = client.getSolutionRevisionArtifacts(null, revisionId);
		for (MLPArtifact mlpArtifact : mlpArtifactList) {
			logger.info("getArtifactTypeCode: {} mlpArtifactGetName: {} ", mlpArtifact.getArtifactTypeCode(),
					mlpArtifact.getName());
					// Don't use ARTIFACT_TYPE_SCANRESULT as a filter for now, until scancode.json is stored with a different code
					//			if (mlpArtifact.getArtifactTypeCode().equalsIgnoreCase(SVConstants.ARTIFACT_TYPE_SCANRESULT)
					//					|| mlpArtifact.getName().equalsIgnoreCase("scanresult.json")) {
			if (mlpArtifact.getName().equalsIgnoreCase("scanresult.json")) {

				String nexusURI = "";
				nexusURI = mlpArtifactList.stream().filter(
				// Don't use ARTIFACT_TYPE_SCANRESULT as a filter for now, until scancode.json is stored with a different code
				//						mlpArt -> mlpArt.getArtifactTypeCode().equalsIgnoreCase(SVConstants.ARTIFACT_TYPE_SCANRESULT))
						mlpArt -> mlpArt.getName().equalsIgnoreCase("scanresult.json"))
						.findFirst().get().getUri();
				logger.info("mlpArtifact nexusURI: {}", nexusURI);

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
				String scanResultRootLicenseType = parseJSON
						.scanResultRootLicenseType(byteArrayOutputStream.toString());

				logger.info("scanResultRootLicenseType: ({}) ", scanResultRootLicenseType);
				// Proprietary models have a recognized licenseType, not null, "", or SPDX
		
				if (!StringUtils.isEmpty(scanResultRootLicenseType) && !scanResultRootLicenseType.equals("SPDX")) {
					// if owner skip rtu check
					if(ownerUserId != loggedInUserId){
						if (workflowId.equalsIgnoreCase(SVConstants.DOWNLOAD)) {
							VerifyLicenseRequest licenseDownloadRequest = new VerifyLicenseRequest(LicenseAction.DOWNLOAD,
									solutionId, loggedInUserId);
							licenseDownloadRequest.addAction(LicenseAction.DOWNLOAD);
							ILicenseVerification verifyUserRTU = licenseVerifier.verifyRtu(licenseDownloadRequest);
							// returns true or false if rtu exists
							rtuFlag = verifyUserRTU.isAllowed(LicenseAction.DOWNLOAD);
							logger.info("verifyUserRTU.isAllowed: {} ", rtuFlag);
						}
						if (workflowId.equalsIgnoreCase(SVConstants.DEPLOY)) {
							VerifyLicenseRequest licenseDownloadRequest = new VerifyLicenseRequest(LicenseAction.DEPLOY,
									solutionId, loggedInUserId);
							licenseDownloadRequest.addAction(LicenseAction.DEPLOY);
							ILicenseVerification verifyUserRTU = licenseVerifier.verifyRtu(licenseDownloadRequest);
							// returns true or false if rtu exists
							rtuFlag = verifyUserRTU.isAllowed(LicenseAction.DEPLOY);
							logger.info("verifyUserRTU.isAllowed: {} ", rtuFlag);
						}
					}
				}
			}
		}

		workFlowAllowed = rtuFlag;
		if (!rtuFlag) {
			reason.append("No Right to use");
		}

		if (isValidInvokeScan(licenseScan, workflowId) || isValidInvokeScan(securityScan, workflowId)) {
			svResponse = invokesScan(solutionId, revisionId, workflowId);
			logger.info(" InvokesScan result StatusCode: {}  StatusCodeValue: {}", svResponse.getStatusCode(),
					svResponse.getStatusCodeValue());
		}

		MLPSolutionRevision mlpSolutionRevision = client.getSolutionRevision(solutionId, revisionId);
		logger.info("Before isValidWorkFlow:licenseVerify ");
		// if owner of model skip workflow check
		if (ownerUserId != loggedInUserId && isValidWorkFlow(licenseVerify, workflowId)) {
			if (mlpSolutionRevision.getVerifiedLicense() == null) {
				logger.info("license scan not yet started");
				if (reason.length() > 1) {
					reason.append(",");
				}
				workFlowAllowed = false;
				reason.append("license scan not yet started");
			} else {
				if (mlpSolutionRevision.getVerifiedLicense().equalsIgnoreCase("IP")) {
					logger.info("license scan in-progress, mlpSolutionRevision.getVerifiedLicense: {}",
							mlpSolutionRevision.getVerifiedLicense());
					if (reason.length() > 1) {
						reason.append(",");
					}
					workFlowAllowed = false;
					reason.append("license scan in-progress");
				}
				if (mlpSolutionRevision.getVerifiedLicense().equalsIgnoreCase("FA")) {
					logger.info("license scan failed, mlpSolutionRevision.getVerifiedLicense:{}",
							mlpSolutionRevision.getVerifiedLicense());
					if (reason.length() > 1) {
						reason.append(",");
					}

					String scanResultReason = SecurityVerificationJsonParser
							.scanResultReason(byteArrayOutputStream.toString());
					logger.info("scanResultReason: {} ", scanResultReason);
					if (scanResultReason != null) {
						reason.append("license scan failed, reason: ");
						reason.append(scanResultReason);
					} else {
						reason.append("license scan failed: unknown reason (null)");
					}

					workFlowAllowed = false;
				}
		 }
		}

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
		logger.info(
				"licenseScan: {} securityScan: {} licenseVerify: {} securityVerify: {} allowedLicenseObjectMap: {} ",
				licenseScan.size(), securityScan.size(), licenseVerify.size(), securityVerify.size(),
				allowedLicenseObjectMap.size());
	}

	private boolean isValidInvokeScan(Map<String, String> siteConfigMap, String workFlowId) {
		String siteConfigWorkFlowId = siteConfigMap.get(workFlowId);
		logger.info("Inside isValidInvokeScan. workFlowId: {}  siteConfigWorkFlowId: {}", workFlowId,
				siteConfigWorkFlowId);
		if (siteConfigMap.containsKey(workFlowId)) {
			if (siteConfigWorkFlowId.equals(SVConstants.TRUE)) {
				return true;
			} else {
			return false;
			}
		} else {
		return false;
		}
	}

	private boolean isValidWorkFlow(Map<String, String> siteConfigMap, String workFlowId) {
		String siteConfigWorkFlowId = siteConfigMap.get(workFlowId);
		logger.info("Inside isValidWorkFlow. workFlowId: {}  siteConfigWorkFlowId: {}", workFlowId,
				siteConfigWorkFlowId);
		if (siteConfigMap.containsKey(workFlowId)) {
			if (siteConfigWorkFlowId.equals(SVConstants.TRUE)) {
				return true;
			} else {
			return false;
			}
		} else {
		return false;
		}
	}

	private ICommonDataServiceRestClient getCcdsClient(final String cdmsClientUrl, final String cdmsClientUsername,
			final String cdmsClientPwd) {
		logger.info("Inside getCcdsClient");
		ICommonDataServiceRestClient client = CommonDataServiceRestClientImpl.getInstance(cdmsClientUrl,
				cdmsClientUsername, cdmsClientPwd);
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

	private ResponseEntity<SVResponse> invokesScan(String nodeSolutionId, String revisionId, String workflowId) {
		logger.info("Inside isValidInvokeScan. securityVerificationApiUrl  {}", securityVerificationApiUrl);
		HttpHeaders headers = new HttpHeaders();
		RestTemplate restTemplate = new RestTemplate();
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		String svApiUrl = svApiUrlBuilder(nodeSolutionId, revisionId, workflowId, securityVerificationApiUrl);
		logger.info("Security verification Api Url {}" + svApiUrl);
		ResponseEntity<SVResponse> svResponse = restTemplate.exchange(svApiUrl, HttpMethod.POST, entity, SVResponse.class);
		logger.info("Security verification Api result {}", svResponse.getBody());
		return svResponse;
	}

	private String svApiUrlBuilder(String nodeSolutionId, String revisionId, String workflowId,
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
		url.append(workflowId);
		return url.toString();
	}

	private boolean isScanInputParameterFound(String solutionId, String revisionId, String workflowId) {
		logger.info("Inside isScanInputParameterFound");
		return !StringUtils.isEmpty(solutionId) && !StringUtils.isEmpty(revisionId) && !StringUtils.isEmpty(workflowId);
	}

}
