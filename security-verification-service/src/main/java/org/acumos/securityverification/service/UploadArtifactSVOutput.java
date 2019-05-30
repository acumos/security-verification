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

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.acumos.cds.CodeNameType;
import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.domain.MLPArtifact;
import org.acumos.cds.domain.MLPCodeNamePair;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.nexus.client.NexusArtifactClient;
import org.acumos.nexus.client.RepositoryLocation;
import org.acumos.nexus.client.data.UploadArtifactInfo;
import org.acumos.securityverification.exception.AcumosServiceException;
import org.acumos.securityverification.utils.SVServiceConstants;
import org.acumos.securityverification.utils.SecurityVerificationServiceUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.maven.wagon.ConnectionException;
import org.apache.maven.wagon.ResourceDoesNotExistException;
import org.apache.maven.wagon.TransferFailedException;
import org.apache.maven.wagon.authentication.AuthenticationException;
import org.apache.maven.wagon.authorization.AuthorizationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpStatusCodeException;

public class UploadArtifactSVOutput {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private Environment env;

	Map<String, String> artifactsDetails = new HashMap<>();

	public UploadArtifactSVOutput(Environment env2){
		this.env = env2;
	}

	public void addCreateArtifact(String solutionId, String revisionId, String version, String userId,
			File file) throws AcumosServiceException, FileNotFoundException {

		logger.debug("Inside the addCreateArtifact, solutionId: {} revisionId: {} version: {} userId: {}",solutionId,revisionId,version,userId);

		long size = file.length();
		String name = FilenameUtils.getBaseName(file.getName());
		String extension = FilenameUtils.getExtension(file.getName());
		logger.debug("addCreateArtifact filename: {} size: {}, extension: {}, name: {} ", name, size, extension, name);

		ICommonDataServiceRestClient dataServiceRestClient = getCcdsClient();
		if (SecurityVerificationServiceUtils.isEmptyOrNullString(extension))
			throw new IllegalArgumentException("Incorrect file extension.");

		try {
			// first try to upload the file to nexus.
			NexusArtifactClient nexusClient = getNexusClient();
			UploadArtifactInfo uploadInfo = null;
			InputStream stream = null;
			try {
				stream = new DataInputStream(new FileInputStream(file));
				logger.debug("Before nexusClient.uploadArtifact call");
				uploadInfo = nexusClient.uploadArtifact(getNexusGroupId(solutionId, revisionId), name, version,
						extension, size, stream);
				logger.debug("After nexusClient.uploadArtifact call");
			} catch (IOException | ConnectionException | AuthenticationException | AuthorizationException
					| TransferFailedException | ResourceDoesNotExistException e) {
				logger.error("Failed to upload the artifact", e);
				throw new AcumosServiceException(AcumosServiceException.ErrorCode.IO_EXCEPTION, e.getMessage());
			} finally {
				if (null != stream) {
					stream.close();
				}
			}

			if (uploadInfo != null) {
				logger.debug(
						"{} uploadToArtifact response Info: getArtifactId: {}, getGroupId: {}, getPackaging: {}, getVersion: {}, getArtifactMvnPath: {} ",
						file.getName(), uploadInfo.getArtifactId(), uploadInfo.getGroupId(), uploadInfo.getPackaging(),
						uploadInfo.getVersion(), uploadInfo.getArtifactMvnPath());

				List<MLPArtifact> mlpArtifactList = dataServiceRestClient.getSolutionRevisionArtifacts(null,revisionId);// solutionIdIgnored
				MLPSolutionRevision mlpSolutionRevision = dataServiceRestClient.getSolutionRevision(solutionId,revisionId);
				boolean found = false;
				MLPArtifact modelArtifact = new MLPArtifact();
				modelArtifact.setName(file.getName());
				modelArtifact.setDescription(file.getName());
				modelArtifact.setVersion(uploadInfo.getVersion());
				modelArtifact.setUserId(mlpSolutionRevision.getUserId());
				modelArtifact.setUri(uploadInfo.getArtifactMvnPath());
				modelArtifact.setSize((int) size);
				modelArtifact.setArtifactTypeCode(SVServiceConstants.SCAN_RESULT_CODE);
				for (MLPArtifact mlpArtifact : mlpArtifactList) {
					if (mlpArtifact.getArtifactTypeCode().equalsIgnoreCase(SVServiceConstants.SCAN_RESULT_CODE)
							&& mlpArtifact.getName().equalsIgnoreCase(file.getName())) {
						modelArtifact.setArtifactId(mlpArtifact.getArtifactId());
						dataServiceRestClient.updateArtifact(modelArtifact);
						found = true;
						logger.debug("updateArtifact done");
					}
				}
				if (!found) {
					modelArtifact = dataServiceRestClient.createArtifact(modelArtifact);
					logger.debug("createArtifact done");
					try {
							logger.debug("addSolutionRevisionArtifact for " + file.getName() + " called");
							dataServiceRestClient.addSolutionRevisionArtifact(solutionId, revisionId,
									modelArtifact.getArtifactId());
							logger.debug("addSolutionRevisionArtifact for " + file.getName() + " successful");
						} catch (HttpStatusCodeException e) {
							logger.error("Fail to call addSolutionRevisionArtifact: " + e);
							throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR,
									"Fail to call addSolutionRevisionArtifact for " + file.getName() + " - "
											+ e.getResponseBodyAsString(),
									e);
						}
				}

			} else {
				logger.error("Cannot upload the Artifact to the specified path");
				throw new AcumosServiceException(AcumosServiceException.ErrorCode.IO_EXCEPTION,
						"Cannot upload the Artifact to the specified path");
			}
		} catch (Exception e) {
			logger.error("Exception during upload to Artifact: ", e);
			throw new AcumosServiceException(e.getMessage());
		}
	}

	private String getArtifactTypeCode(String artifactTypeName) {
		String typeCode = artifactsDetails.get(artifactTypeName);
		return typeCode;
	}

	private Map<String, String> getArtifactsDetails(ICommonDataServiceRestClient dataServiceRestClient) {
		List<MLPCodeNamePair> typeCodeList = dataServiceRestClient.getCodeNamePairs(CodeNameType.ARTIFACT_TYPE);
		Map<String, String> artifactsDetails = new HashMap<>();
		if (!typeCodeList.isEmpty()) {
			for (MLPCodeNamePair codeNamePair : typeCodeList) {
				artifactsDetails.put(codeNamePair.getName(), codeNamePair.getCode());
			}
		}
		return artifactsDetails;
	}

	private String getNexusGroupId(String solutionId, String revisionId) {
		logger.debug("Inside getNexusGroupId");
		String group = env.getProperty(SVServiceConstants.NEXUS_GROUPID);
		logger.debug("nexus.groupId: {}",group);
		if (SecurityVerificationServiceUtils.isEmptyOrNullString(group))
			throw new IllegalArgumentException("Missing property value for nexus groupId.");
		// This will created the nexus file upload path as groupId/solutionId/revisionId. Ex.. "org/acumos/solutionId/revisionId".
		return String.join(".", group, solutionId, revisionId);
	}

	private ICommonDataServiceRestClient getCcdsClient() {
		logger.debug("Inside getCcdsClient");
		ICommonDataServiceRestClient client = new CommonDataServiceRestClientImpl(
				env.getProperty(SVServiceConstants.CDMS_CLIENT_URL),
				env.getProperty(SVServiceConstants.CDMS_CLIENT_USER),
				env.getProperty(SVServiceConstants.CDMS_CLIENT_PWD), null);
		return client;
	}

	private NexusArtifactClient getNexusClient() {
		logger.debug("Inside getNexusClient");
		RepositoryLocation repositoryLocation = new RepositoryLocation();
		repositoryLocation.setId("1");
		repositoryLocation.setUrl(env.getProperty(SVServiceConstants.NEXUS_CLIENT_URL));
		repositoryLocation.setUsername(env.getProperty(SVServiceConstants.NEXUS_CLIENT_USER));
		repositoryLocation.setPassword(env.getProperty(SVServiceConstants.NEXUS_CLIENT_PWD));

		if (!StringUtils.isEmpty(env.getProperty(SVServiceConstants.NEXUS_CLIENT_PROXY))) {
			repositoryLocation.setProxy(env.getProperty(SVServiceConstants.NEXUS_CLIENT_PROXY));
		}
		NexusArtifactClient artifactClient = new NexusArtifactClient(repositoryLocation);
		return artifactClient;
	}

}
