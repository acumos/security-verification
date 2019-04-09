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
import org.acumos.licensemanager.exceptions.RightToUseException;
import org.acumos.nexus.client.NexusArtifactClient;
import org.acumos.nexus.client.RepositoryLocation;
import org.acumos.securityverification.domain.SecurityVerificationCdump;
import org.acumos.securityverification.domain.SecurityVerificationCdumpNode;
import org.acumos.securityverification.domain.Workflow;
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
		logger.debug("revisionId:: {} worflowId:: {}", revisionId, worflowId);

		try {
			ICommonDataServiceRestClient client = getClient(cdmsClientUrl, cdmsClientUsername, cdmsClientPwd);
			if (client != null) {
				logger.debug("CCDS CALL GET /solution/{solutionId} ");
				MLPSolution mlpSolution = client.getSolution(solutionId);
				if (mlpSolution != null) {

					logger.debug("CCDS CALL GET /solution/{solutionId}/revision::--");
					List<MLPSolutionRevision> mlpSolutionRevisions = client.getSolutionRevisions(solutionId);

					String mlpRevisionId = null;
					for (MLPSolutionRevision mlpSolutionRevision : mlpSolutionRevisions) {
						logger.debug(
								"mlpSolutionRevision.getRevisionId()::--> {}" + mlpSolutionRevision.getRevisionId());
						if (mlpSolutionRevision.getRevisionId() != null
								&& mlpSolutionRevision.getRevisionId().equals(revisionId)) {
							verificationSiteConfig();
							String userId = mlpSolutionRevision.getUserId();
							mlpRevisionId = mlpSolutionRevision.getRevisionId();
							logger.debug(
									"mlpSolutionRevision.getVersion():::--> {}" + mlpSolutionRevision.getVersion());
							// modelTypeCode is PR (predictor) and the toolkitTypeCode is CP (composite
							// solution) then retrieves the CD (CDUMP) artifact for the revisionId
							// GET /revision/{revisionId}/artifact
							if (mlpSolution.getModelTypeCode().equalsIgnoreCase(SVConstants.MODEL_TYPE_CODE)
									&& mlpSolution.getToolkitTypeCode()
											.equalsIgnoreCase(SVConstants.TOOL_KIT_TYPE_CODE)) {

								logger.debug("Call CDS to get CDUMP artifact URL..");
								List<MLPArtifact> mlpArtifactList = client.getSolutionRevisionArtifacts(null,
										mlpRevisionId);
								String nexusURI = "";
								nexusURI = mlpArtifactList.stream()
										.filter(mlpArt -> mlpArt.getArtifactTypeCode()
												.equalsIgnoreCase(SVConstants.ARTIFACT_TYPE_CDUMP))
										.findFirst().get().getUri();
								logger.debug("mlpArtifact nexusURI::-- {}" + nexusURI);

								ByteArrayOutputStream byteArrayOutputStream = null;
								NexusArtifactClient nexusArtifactClient = getNexusClient(nexusClientUrl,
										nexusClientUsername, nexusClientPwd);

								byteArrayOutputStream = nexusArtifactClient.getArtifact(nexusURI);
								logger.debug("getBluePrintNexus: byteArrayOutputStream length:-- {}"
										+ byteArrayOutputStream.size());
								logger.debug("byteArrayOutputStream.toString():::---> {}"
										+ byteArrayOutputStream.toString());

								// TODO need to be removed CDUMP json file
								SecurityVerificationJsonParser parseJSON = new SecurityVerificationJsonParser();
								// using the member nodeSolutionId, retrieves the list of revisions for the
								// solutionId
								SecurityVerificationCdump securityVerificationCdump = parseJSON
										.parseCdumpJsonFile(byteArrayOutputStream.toString());
								List<SecurityVerificationCdumpNode> securityVerificationCdumpNodes = securityVerificationCdump
										.getNodes();
								if (securityVerificationCdumpNodes != null) {
									for (SecurityVerificationCdumpNode securityVerificationCdumpNode : securityVerificationCdumpNodes) {
										logger.debug("------CDUMP CHILD NODE -------------------");
										List<MLPSolutionRevision> mlpCdumpSolutionRevisions = client
												.getSolutionRevisions(
														securityVerificationCdumpNode.getNodeSolutionId());
										for (MLPSolutionRevision mlpCdumpSolutionRevision : mlpCdumpSolutionRevisions) {

											if (securityVerificationCdumpNode.getNodeVersion()
													.equalsIgnoreCase(mlpCdumpSolutionRevision.getVersion())) {
												logger.debug(
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
												// TODO Call to scan invocation logic
												// ACUMOS-2558: TBD verifiedVulnerability or verifiedLicense are null
												// and unable to find validationStatusCode in MLPSolutionRevision.
												mlpCdumpSolutionRevision.getVerifiedLicense();
												mlpCdumpSolutionRevision.getVerifiedVulnerability();
												workflowPermissionDetermination(worflowId, workflow, client, userId,
														securityVerificationCdumpNode, mlpCdumpSolutionRevision);
											}
										}
									}
								}
							} else {
								logger.debug(
										" executes the Scan: TRUE it is a simple model and the S-V library executes the Scan Invocation Logic");
								// TODO scan invocation logic
								workflow.setWorkflowAllowed(true);
								ResponseEntity<SVResonse> svResonse = invokesScan(solutionId, revisionId, worflowId);
							}

						} else {
							// ACUMOS-2555
							workflow.setWorkflowAllowed(false);
							workflow.setReason(SVConstants.REASON_NOT_FOUND);
						}

					}

				}

			}
		} catch (Exception e) {
			logger.error("Exception", e);
			workflow.setWorkflowAllowed(false);
			workflow.setSvException(e.getMessage());
		}
		return workflow;
	}

	private void workflowPermissionDetermination(String worflowId, Workflow workflow,
			ICommonDataServiceRestClient client, String userId,
			SecurityVerificationCdumpNode securityVerificationCdumpNode, MLPSolutionRevision mlpCdumpSolutionRevision)
			throws RightToUseException {
		ILicenseVerifier licenseVerifier = new LicenseVerifier(client);
		ResponseEntity<SVResonse> svResonse = null;
		if (worflowId.equalsIgnoreCase(SVConstants.DOWNLOAD)) {
			VerifyLicenseRequest licenseDownloadRequest = new VerifyLicenseRequest(LicenseAction.DOWNLOAD,
					securityVerificationCdumpNode.getNodeSolutionId(), userId);
			licenseDownloadRequest.addAction(LicenseAction.DOWNLOAD);
			ILicenseVerification verifyUserRTU = licenseVerifier.verifyRTU(licenseDownloadRequest);
			// returns true or false if rtu exists
			workflow.setWorkflowAllowed(verifyUserRTU.isAllowed(LicenseAction.DOWNLOAD));
		}
		if (worflowId.equalsIgnoreCase(SVConstants.DEPLOY)) {
			VerifyLicenseRequest licenseDownloadRequest = new VerifyLicenseRequest(LicenseAction.DEPLOY,
					securityVerificationCdumpNode.getNodeSolutionId(), userId);
			licenseDownloadRequest.addAction(LicenseAction.DEPLOY);
			ILicenseVerification verifyUserRTU = licenseVerifier.verifyRTU(licenseDownloadRequest);
			// returns true or false if rtu exists
			workflow.setWorkflowAllowed(verifyUserRTU.isAllowed(LicenseAction.DEPLOY));
		}

		if (isValidInvokeScan(licenseScan, worflowId)) {
			svResonse = invokesScan(securityVerificationCdumpNode.getNodeSolutionId(),
					mlpCdumpSolutionRevision.getRevisionId(), worflowId);
			workflow.setWorkflowAllowed(false);
			workflow.setReason("license scan incomplete|security scan failure");
		}
		if (isValidInvokeScan(securityScan, worflowId)) {
			svResonse = invokesScan(securityVerificationCdumpNode.getNodeSolutionId(),
					mlpCdumpSolutionRevision.getRevisionId(), worflowId);
			workflow.setWorkflowAllowed(false);
			workflow.setReason("license scan incomplete|security scan failure");
		}
		if (isValidInvokeScan(licenseVerify, worflowId)) {
			svResonse = invokesScan(securityVerificationCdumpNode.getNodeSolutionId(),
					mlpCdumpSolutionRevision.getRevisionId(), worflowId);
			workflow.setWorkflowAllowed(false);
			workflow.setReason("license scan incomplete|security scan failure");
		}
		if (isValidInvokeScan(securityVerify, worflowId)) {
			svResonse = invokesScan(securityVerificationCdumpNode.getNodeSolutionId(),
					mlpCdumpSolutionRevision.getRevisionId(), worflowId);
			workflow.setWorkflowAllowed(false);
			workflow.setReason("license scan incomplete|security scan failure");
		}
	}

	private void verificationSiteConfig() throws Exception {
		logger.debug("Inside verificationSiteConfig method call");
		SecurityVerificationJsonParser parseJSON = new SecurityVerificationJsonParser();
		ICommonDataServiceRestClient client = getClient(cdmsClientUrl, cdmsClientUsername, cdmsClientPwd);
		JSONObject siteConfigDataJsonObj = new JSONObject();
		if (client != null) {
			MLPSiteConfig mlpSiteConfig = client.getSiteConfig(SVConstants.CONFIGKEY);
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
	}

	private boolean isValidInvokeScan(Map<String, String> siteConfigMap, String workFlowId) {
		if (siteConfigMap.containsKey(workFlowId) && siteConfigMap.get(workFlowId).equals("true")) {
			return true;
		}
		return false;
	}

	private ICommonDataServiceRestClient getClient(final String cdmsClientUrl, final String cdmsClientUsername,
			final String cdmsClientPwd) {
		ICommonDataServiceRestClient client = new CommonDataServiceRestClientImpl(cdmsClientUrl, cdmsClientUsername,
				cdmsClientPwd, null);
		return client;
	}

	private NexusArtifactClient getNexusClient(final String nexusClientUrl, final String nexusClientUsername,
			final String nexusClientPwd) {
		RepositoryLocation repositoryLocation = new RepositoryLocation();
		repositoryLocation.setId("1");
		repositoryLocation.setUrl(nexusClientUrl);
		repositoryLocation.setUsername(nexusClientUsername);
		repositoryLocation.setPassword(nexusClientPwd);

		NexusArtifactClient artifactClient = new NexusArtifactClient(repositoryLocation);
		return artifactClient;
	}

	private ResponseEntity<SVResonse> invokesScan(String nodeSolutionId, String revisionId, String worflowId) {
		logger.debug("securityVerificationApiUrl  {}", securityVerificationApiUrl);
		HttpHeaders headers = new HttpHeaders();
		RestTemplate restTemplate = new RestTemplate();
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		String svApiUrl = svApiUrlBuilder(nodeSolutionId, revisionId, worflowId, securityVerificationApiUrl);
		logger.debug("Security verification Api Url {}" + svApiUrl);
		ResponseEntity<SVResonse> svResonse = restTemplate.exchange(svApiUrl, HttpMethod.POST, entity, SVResonse.class);
		logger.debug("Security verification Api result {}", svResonse.getBody());
		return svResonse;
	}

	private String svApiUrlBuilder(String nodeSolutionId, String revisionId, String worflowId,
			String securityVerificationApiUrl) {
		StringBuilder url = new StringBuilder();
		url.append(securityVerificationApiUrl);
		url.append(SVConstants.BACKSLASH);
		url.append(SVConstants.SOLUTIONID);
		url.append(SVConstants.BACKSLASH);
		url.append(nodeSolutionId);
		url.append(SVConstants.BACKSLASH);
		url.append(SVConstants.REVISIONID);
		url.append(SVConstants.BACKSLASH);
		url.append(revisionId);
		url.append(SVConstants.BACKSLASH);
		url.append(SVConstants.WORKFLOWID);
		url.append(SVConstants.BACKSLASH);
		url.append(worflowId);
		return url.toString();
	}

}