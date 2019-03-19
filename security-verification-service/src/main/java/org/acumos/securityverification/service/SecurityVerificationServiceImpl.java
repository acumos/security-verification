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

import org.springframework.stereotype.Service;

import java.io.File;

import org.acumos.cds.AccessTypeCode;
import org.acumos.cds.domain.MLPDocument;
import org.acumos.securityverification.utils.Configurations;
import org.acumos.securityverification.utils.SVUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class SecurityVerificationServiceImpl extends AbstractServiceImpl implements ISecurityVerificationService {

	Logger logger = LoggerFactory.getLogger(SecurityVerificationServiceImpl.class);
	
	@Override
	public String securityVerification(String solutionId, String revisionId) throws Exception {

		logger.debug("Inside SecurityVerificationServiceImpl");
			//TODO Need add logic
		
			String path=Configurations.getConfig("scan.verification.script.location");
			File file = SVUtils.readScanOutput(path);
			
			path=Configurations.getConfig("sv.licensescan.script  ");
			logger.debug("path sv.licensescan.script"+path);
			path=Configurations.getConfig("sv.dumpmodel.script");
			logger.debug("path sv.dumpmodel.script  "+path);
			
			//TODO Need fianalize once script run from SV library
			path=Configurations.getConfig("sv.licensescan.scancode");
			logger.debug("path sv.licensescan.scancode  "+path);
			//file = SVUtils.readScanOutput(path);
			path=Configurations.getConfig("sv.licensescan.scanresult");
			logger.debug("path sv.licensescan.scanresult  "+path);
			//file = SVUtils.readScanOutput(path);
			
			long fileSizeByKB =  file.length();
			if(fileSizeByKB > 0){
				logger.debug("in side if conditoin fileSizeByKB  "+fileSizeByKB);
//				String userId= mlpSolution.getUserId();
				String userId= "26fcd4bf-8819-41c1-b46c-87ec2f7a39f8";//TODO Need to be discussed, do we need to pass is via client or do we need to call server and get it.
				
				UploadArtifactSVOutput uploadArtifactSVOutput = new UploadArtifactSVOutput();
				MLPDocument document = uploadArtifactSVOutput.addRevisionDocument(solutionId, revisionId, AccessTypeCode.PR.toString(), userId, file);
				
				logger.debug("getDocumentId {}", document.getDocumentId());
				logger.debug("\n getName {}", document.getName());
				logger.debug("\n getUserId {}", document.getUserId());
				
			}
		
		return null;
	}
	
	
	
}
