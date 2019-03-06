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

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.domain.MLPArtifact;
import org.acumos.cds.domain.MLPSiteConfig;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.nexus.client.NexusArtifactClient;
import org.acumos.nexus.client.RepositoryLocation;
import org.acumos.securityverification.domain.LicenseVerify;
import org.acumos.securityverification.domain.SecurityVerificationCdump;
import org.acumos.securityverification.domain.SecurityVerificationCdumpNode;
import org.acumos.securityverification.domain.SecurityVerify;
import org.acumos.securityverification.domain.Verification;
import org.acumos.securityverification.domain.Workflow;
import org.acumos.securityverification.utils.Configurations;
import org.acumos.securityverification.utils.SVConstants;
import org.acumos.securityverification.utils.SecurityVerificationJsonParser;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecurityVerificationServiceImpl implements ISecurityVerificationService {
	 
	 Logger logger = LoggerFactory.getLogger(SecurityVerificationServiceImpl.class);

	public Workflow securityVerificationScan(String solutionId)throws Exception {
 
		Workflow workflow = new Workflow();
		
		String datasource = Configurations.getConfig("cdms.client.url");
		String userName = Configurations.getConfig("cdms.client.username");
		String dataPd = Configurations.getConfig("cdms.client.pwd");
		String nexusUrl = Configurations.getConfig("nexus.client.url");
		String nexusUserName = Configurations.getConfig("nexus.client.username");
		String nexusPd = Configurations.getConfig("nexus.client.pwd");
		logger.debug("datasource"+datasource);
		logger.debug("nexusUrl"+nexusUrl);
		
		CommonDataServiceRestClientImpl client = new CommonDataServiceRestClientImpl(datasource, userName, dataPd,null);
		if (client != null) {
			logger.debug("CCDS CALL GET /solution/{solutionId} ");
			MLPSolution mlpSolution = client.getSolution(solutionId);
			if (mlpSolution != null) {
				
				logger.debug("CCDS CALL GET /solution/{solutionId}/revision::--");
				List<MLPSolutionRevision> mlpSolutionRevisions = client.getSolutionRevisions(solutionId);
				
				String revisionId = null;
				for (MLPSolutionRevision mlpSolutionRevision : mlpSolutionRevisions) {
					logger.debug("mlpSolutionRevision.getRevisionId()::-->" + mlpSolutionRevision.getRevisionId());
					if(mlpSolutionRevision.getRevisionId() != null) {
						
						Verification siteConfigVerificationObj = verificationSiteConfig();
						
						revisionId = mlpSolutionRevision.getRevisionId();	
						logger.debug("mlpSolutionRevision.getVersion():::-->"+mlpSolutionRevision.getVersion());
						//modelTypeCode is PR (predictor) and the toolkitTypeCode is CP (composite solution) then retrieves the CD (CDUMP) artifact for the revisionId
						//GET /revision/{revisionId}/artifact
						if (mlpSolution.getModelTypeCode().equalsIgnoreCase(SVConstants.MODEL_TYPE_CODE)
								&& mlpSolution.getToolkitTypeCode().equalsIgnoreCase(SVConstants.TOOL_KIT_TYPE_CODE)) {

							logger.debug("Call CDS to get CDUMP artifact URL..");
							List<MLPArtifact> mlpArtifactList = client.getSolutionRevisionArtifacts(null,revisionId);
							String nexusURI = "";
								nexusURI = mlpArtifactList.stream()
										.filter(mlpArt -> mlpArt.getArtifactTypeCode()
												.equalsIgnoreCase(SVConstants.ARTIFACT_TYPE_CDUMP))
										.findFirst().get().getUri();
								logger.debug("mlpArtifact nexusURI::--" + nexusURI);

								ByteArrayOutputStream byteArrayOutputStream = null;
								NexusArtifactClient nexusArtifactClient = nexusArtifactClient(nexusUrl, nexusUserName,
										nexusPd);
								byteArrayOutputStream = nexusArtifactClient.getArtifact(nexusURI);
								logger.debug("getBluePrintNexus: byteArrayOutputStream length:--"+ byteArrayOutputStream.size());
								logger.debug("byteArrayOutputStream.toString():::---> " + byteArrayOutputStream.toString());

								// TODO need to be removed CDUMP json file
								SecurityVerificationJsonParser parseJSON = new SecurityVerificationJsonParser();
								// using the member nodeSolutionId, retrieves the list of revisions for the solutionId
								SecurityVerificationCdump securityVerificationCdump = parseJSON.parseCdumpJsonFile(byteArrayOutputStream.toString());
								List<SecurityVerificationCdumpNode> securityVerificationCdumpNodes = securityVerificationCdump.getNodes();
								if (securityVerificationCdumpNodes != null) {
									for (SecurityVerificationCdumpNode securityVerificationCdumpNode : securityVerificationCdumpNodes) {
										logger.debug("------CDUMP CHILD NODE -------------------");
										List<MLPSolutionRevision> mlpCdumpSolutionRevisions = client.getSolutionRevisions(securityVerificationCdumpNode.getNodeSolutionId());
										for (MLPSolutionRevision mlpCdumpSolutionRevision : mlpCdumpSolutionRevisions) {
											
											if (securityVerificationCdumpNode.getNodeVersion()
													.equalsIgnoreCase(mlpCdumpSolutionRevision.getVersion())) {
												logger.debug(" ############################################ \nCdumpNode NodeSolutionId getName: {} "
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
												//TODO Call to scan invocation logic
												//ACUMOS-2558: TBD verifiedVulnerability or verifiedLicense are null and unable to find validationStatusCode in MLPSolutionRevision. 
												mlpCdumpSolutionRevision.getVerifiedLicense();
												mlpCdumpSolutionRevision.getVerifiedVulnerability();
												workflow.setWorkflowAllowed(true); 
												
												LicenseVerify licenseVerify = siteConfigVerificationObj.getLicenseVerify();
												if (licenseVerify.isDeploy() && licenseVerify.isDownload()
														&& licenseVerify.isPublishCompany()
														&& licenseVerify.isPublishPublic() && licenseVerify.isShare()) {
													//TBD not found "validationStatusCode" is "IP"
													workflow.setWorkflowAllowed(false); 
													workflow.setReason(SVConstants.LICENSE_SCAN_INCOMPLETE);
												}
												SecurityVerify securityVerify = siteConfigVerificationObj.getSecurityVerify();
												if (securityVerify.isDeploy() && securityVerify.isDownload()
														&& securityVerify.isPublishCompany()
														&& securityVerify.isPublishPublic() && securityVerify.isShare()) {
													//TBD not found "validationStatusCode" is "IP"
													workflow.setWorkflowAllowed(false); 
													workflow.setReason(SVConstants.LICENSE_SCAN_INCOMPLETE);
												}
												
											}
										}
									}
								}
						}else {
							logger.debug(" executes the Scan: TRUE it is a simple model and the S-V library executes the Scan Invocation Logic");
							//TODO scan invocation logic
							workflow.setWorkflowAllowed(true); 
						}
						
					} else {
					//ACUMOS-2555 return “workflow allowed”=”false” and reason=”solution/revision not found”
					workflow.setWorkflowAllowed(false); 
					workflow.setReason(SVConstants.REASON_NOT_FOUND);
					}
						
				}
				
			}
			
		}
		
		return workflow;
	}
	
	
	public Verification verificationSiteConfig() {

		logger.debug("verificationSiteConfig ");
		String datasource = Configurations.getConfig("cdms.client.url");
		String userName = Configurations.getConfig("cdms.client.username");
		String dataPd = Configurations.getConfig("cdms.client.pwd");
		
		SecurityVerificationJsonParser parseJSON = new SecurityVerificationJsonParser();
		//CDS call
		CommonDataServiceRestClientImpl client = new CommonDataServiceRestClientImpl(datasource, userName, dataPd,null);
		JSONObject siteConfigDataJsonObj = new JSONObject();
		if(client != null) {
			MLPSiteConfig mlpSiteConfig =client.getSiteConfig(SVConstants.CONFIGKEY);
			if (mlpSiteConfig != null) {
				String siteConfigTemp = mlpSiteConfig.getConfigValue().toString().replaceAll("\\\\\"", "\"");
				String siteConfigData = siteConfigTemp.substring(1, siteConfigTemp.length() - 1);
				siteConfigDataJsonObj = parseJSON.stringToJsonObject(siteConfigData);
			} else {
				//ACUMOS-2555 TODO Need to be discussed "note until the Scanning Service is implemented, the site-config key will need to be provisioned via swagger"
				//No site config infomation found. Default site_config is picked from configuration file and inserted in database
				String siteConfigJsonFromConfiguration="";//env.getProperty("siteConfig.verification");  
				MLPSiteConfig config = new MLPSiteConfig();
				config.setConfigKey(SVConstants.CONFIGKEY);
				config.setConfigValue(siteConfigJsonFromConfiguration);
//				config.setUserId("26fcd4bf-8819-41c1-b46c-87ec2f7a39f8");
				Object obj = client.createSiteConfig(config);
				siteConfigDataJsonObj = parseJSON.stringToJsonObject(siteConfigJsonFromConfiguration);
			}
		
		}
		
		return parseJSON.parseSiteConfigJson(siteConfigDataJsonObj);
	}


	private NexusArtifactClient nexusArtifactClient(String nexusUrl, String nexusUserName, String nexusPd) {
		logger.debug("nexusArtifactClient start");
		RepositoryLocation repositoryLocation = new RepositoryLocation();
		repositoryLocation.setId("1");
		repositoryLocation.setUrl(nexusUrl);
		repositoryLocation.setUsername(nexusUserName);
		repositoryLocation.setPassword(nexusPd);
		NexusArtifactClient nexusArtifactClient = new NexusArtifactClient(repositoryLocation);
		logger.debug("nexusArtifactClient End");
		return nexusArtifactClient;
	}
	
	
	
}
