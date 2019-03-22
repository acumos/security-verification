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

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.domain.MLPDocument;
import org.acumos.nexus.client.NexusArtifactClient;
import org.acumos.nexus.client.data.UploadArtifactInfo;
import org.acumos.securityverification.exception.AcumosServiceException;
import org.acumos.securityverification.utils.Configurations;
import org.acumos.securityverification.utils.SVUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.maven.wagon.ConnectionException;
import org.apache.maven.wagon.ResourceDoesNotExistException;
import org.apache.maven.wagon.TransferFailedException;
import org.apache.maven.wagon.authorization.AuthorizationException;
import org.apache.maven.wagon.authentication.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UploadArtifactSVOutput extends AbstractServiceImpl{

	Logger logger = LoggerFactory.getLogger(UploadArtifactSVOutput.class);
	
	public MLPDocument addRevisionDocument(String solutionId, String revisionId, String accessType, String userId, File file) throws AcumosServiceException, FileNotFoundException {

		logger.debug("Inside the addRevisionDocument");
		
		long size = file.length();
		String name = FilenameUtils.getBaseName(file.getName());
		String extension = FilenameUtils.getExtension(SVUtils.getFileExtension(file));
		
		logger.debug("Inside the addRevisionDocument" +name);
		logger.debug("size  "+size);
		logger.debug("extension "+extension);
		logger.debug("name "+name);
		
		
		ICommonDataServiceRestClient dataServiceRestClient = getCcdsClient();

		if(SVUtils.isEmptyOrNullString(extension))
			throw new IllegalArgumentException("Incorrect file extension.");

		//Check if docuemtn already exists with the same name
		List<MLPDocument> documents = dataServiceRestClient.getSolutionRevisionDocuments(revisionId, accessType);
		logger.debug("CCDS call sucess..");
		for (MLPDocument doc : documents) {
			if (doc.getName().equalsIgnoreCase(name)) {
				logger.error("Document Already exists with the same name.");
				throw new AcumosServiceException(AcumosServiceException.ErrorCode.IO_EXCEPTION, "Document Already exists with the same name.");
			}
		}

		//first try to upload the file to nexus. If successful then only create the c_document record in db
		NexusArtifactClient nexusClient = getNexusClient();
		UploadArtifactInfo uploadInfo = null;
		MLPDocument document = null;
		InputStream stream = null;
		try {
			try {		
				stream = new DataInputStream(new FileInputStream(file));
				logger.debug("Before nexusClient call sucess..");
				uploadInfo = nexusClient.uploadArtifact(getNexusGroupId(solutionId, revisionId), name, accessType, extension, size, stream);
				logger.debug("After nexusClient call sucess..");
			} catch (IOException | ConnectionException | AuthenticationException | AuthorizationException | TransferFailedException | ResourceDoesNotExistException e) {
				logger.error("Failed to upload the document", e);
				throw new AcumosServiceException(AcumosServiceException.ErrorCode.IO_EXCEPTION, e.getMessage());
			}finally {
				if (null != stream) {
						stream.close();
				}
			}
			
			if (uploadInfo != null) {
				logger.debug("Inside uploadInfo..");
				/*document = new MLPDocument(null, name,uploadInfo.getArtifactMvnPath(), (int) size, "1628acd3-37d6-4c53-a722-0396d0590235");*/
				document = new MLPDocument();
				document.setName(name);
				document.setUri(uploadInfo.getArtifactMvnPath());
				document.setSize((int) size);
				document.setUserId(userId);
				document = dataServiceRestClient.createDocument(document);
				logger.debug("After uploadInfo..");
				dataServiceRestClient.addSolutionRevisionDocument(revisionId, accessType, document.getDocumentId());
			} else {
				logger.error("Cannot upload the Document to the specified path");
				throw new AcumosServiceException(AcumosServiceException.ErrorCode.IO_EXCEPTION, "Cannot upload the Document to the specified path");
			}
		} catch (Exception e) {
			logger.error( "Exception during addRevisionDocument ={}", e);
			throw new AcumosServiceException(e.getMessage());
		}
		return document;
	}

	private String getNexusGroupId(String solutionId, String revisionId) {
		String group = Configurations.getConfig("nexus.groupId");
		
		if(SVUtils.isEmptyOrNullString(group))
			throw new IllegalArgumentException("Missing property value for nexus groupId.");
		//This will created the nexus file upload path as groupId/solutionId/revisionId. Ex.. "org/acumos/solutionId/revisionId".
		return String.join(".", group, solutionId, revisionId);
	}
}
