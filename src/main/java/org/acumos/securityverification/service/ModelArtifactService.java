/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2018 AT&T Intellectual Property. All rights reserved.
 * ===================================================================================
 * This Acumos software file is distributed by AT&T 
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
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.domain.MLPArtifact;
import org.acumos.nexus.client.NexusArtifactClient;
import org.acumos.nexus.client.RepositoryLocation;
import org.acumos.securityverification.utils.EELFLoggerDelegate;
import org.springframework.stereotype.Service;

/**
 * Service to interact with Nexus
 * @author aimeeu
 *
 */
@Service
public class ModelArtifactService extends AbstractService {
	

	String artifactFileName;
	
	CommonDataServiceRestClientImpl cmnDataService;
	
	public String obtainAndArchiveModelArtifactsAndDocs(String solutionId, String revisionId) throws Exception {
		
		return null;
	}
	
	private String downloadModelArtifactsFromNexus(String solutionId, String revisionId) throws Exception {
		// CODE LIFTED FROM org.acumos.microservice.services.impl.DownloadModelArtifacts
		logger.debug(EELFLoggerDelegate.debugLogger, "------ Start getModelArtifacts-----------------");
		logger.debug(EELFLoggerDelegate.debugLogger, "-------solutionId-----------" + solutionId);
		logger.debug(EELFLoggerDelegate.debugLogger, "-------revisionId-----------" + revisionId);
		
		List<MLPArtifact> mlpArtifactList;
		String nexusURI = "";
		
		ByteArrayOutputStream byteArrayOutputStream = null;
		//final String webapiUrl, final String user, final String pass, final String proxyUrl
		this.cmnDataService = new CommonDataServiceRestClientImpl(super.cmnDataSvcEndPoinURL, super.cmnDataSvcUser, super.cmnDataSvcPwd, null);
		
		File outputFolder = new File("model");
		outputFolder.mkdirs();

		if (revisionId != null) {
			/*Get the list of Artifacts for the SolutionId and revisionId.*/
			mlpArtifactList = cmnDataService.getSolutionRevisionArtifacts(solutionId, revisionId);
			if (mlpArtifactList != null && !mlpArtifactList.isEmpty()) {

				for (int i = 0; i < mlpArtifactList.size(); i++) {

					if (mlpArtifactList.get(i).getArtifactTypeCode().equals("MI")
							|| mlpArtifactList.get(i).getArtifactTypeCode().equals("MD")) {

						nexusURI = mlpArtifactList.get(i).getUri();

						logger.debug(EELFLoggerDelegate.debugLogger, "------ Nexus URI : " + nexusURI + " -------");
						if (nexusURI != null) {
							RepositoryLocation repositoryLocation = new RepositoryLocation();
							repositoryLocation.setId("1");
							repositoryLocation.setUrl(super.nexusEndPointURL);
							repositoryLocation.setUsername(nexusUserName);
							repositoryLocation.setPassword(nexusPassword);
							NexusArtifactClient artifactClient = new NexusArtifactClient(repositoryLocation);

							byteArrayOutputStream = artifactClient.getArtifact(nexusURI);
							if (!nexusURI.isEmpty()) {
								artifactFileName = nexusURI.substring(nexusURI.lastIndexOf("/") + 1, nexusURI.length());

							}
						}
						if (byteArrayOutputStream != null) {
							byteArrayOutputStream.close();
						}
						File file = new File(outputFolder,artifactFileName);
						FileOutputStream fout = new FileOutputStream(file);
						fout.write(byteArrayOutputStream.toByteArray());
						fout.flush();
						fout.close();

					}
				}
			}
		}
		return artifactFileName;
	}
	
	private String downloadModelDocumentsFromCMS(String solutionId, String revisionId) throws Exception {
		
		return null;
	}
}
