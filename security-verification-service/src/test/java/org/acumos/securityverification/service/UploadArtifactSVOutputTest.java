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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.lang.invoke.MethodHandles;

import org.acumos.cds.AccessTypeCode;
import org.acumos.nexus.client.NexusArtifactClient;
import org.acumos.nexus.client.RepositoryLocation;
import org.acumos.nexus.client.data.UploadArtifactInfo;
import org.acumos.securityverification.utils.SVServiceConstants;
import org.acumos.securityverification.utils.SecurityVerificationServiceUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

public class UploadArtifactSVOutputTest {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Mock
	private Environment env;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		when(env.getProperty(SVServiceConstants.CDMS_CLIENT_URL)).thenReturn("http://cds.com:8001/ccds");
		when(env.getProperty(SVServiceConstants.CDMS_CLIENT_USER)).thenReturn("MockedSpringEnvString");
		when(env.getProperty(SVServiceConstants.CDMS_CLIENT_PWD)).thenReturn("MockedSpringEnvString");
		when(env.getProperty(SVServiceConstants.NEXUS_CLIENT_URL)).thenReturn("http://acumosnexus.com:8282/nexus");
		when(env.getProperty(SVServiceConstants.NEXUS_CLIENT_USER)).thenReturn("MockedSpringEnvString");
		when(env.getProperty(SVServiceConstants.NEXUS_CLIENT_PWD)).thenReturn("MockedSpringEnvString");
		when(env.getProperty(SVServiceConstants.NEXUS_CLIENT_PROXY)).thenReturn("MockedSpringEnvString");
	}

	@Test(expected=IllegalArgumentException.class)
	public void testIncorectFile() throws Exception {
		String solutionId = "bf0478bc-0d3f-4433-80b6-d7a0f6db2df2";
		String revisionId = "ba742c3b-039c-4a8e-b0fc-1aee7d5a9d67";
		String accessType = AccessTypeCode.PR.toString();
		String userId = "ba742c3b-039c-4a8e-b0fc-1aee7d5a9d64";
		
			File file = mock(File.class);
			UploadArtifactSVOutput uploadArtifactSVOutput = new UploadArtifactSVOutput(env);
			uploadArtifactSVOutput.addCreateArtifact(solutionId,revisionId,accessType,userId,file);
	}
	
	@Test(expected=Exception.class)
	public void testAddCreateArtifact() throws Exception {
		String solutionId = "bf0478bc-0d3f-4433-80b6-d7a0f6db2df2";
		String revisionId = "ba742c3b-039c-4a8e-b0fc-1aee7d5a9d67";
		String accessType = AccessTypeCode.PR.toString();
		String userId = "ba742c3b-039c-4a8e-b0fc-1aee7d5a9d64";
		String folder = "fb8f31cc-69d0-46b1-8b68-593a3b55a595";
		String scanResultJsonFilePath = scanOutJsonLocation(folder, SVServiceConstants.SCAN_RESULT_JSON);
			String name="scanresult.json";
			String scanResultJson = "{\"schema\": \"1.0\",\"verifiedLicense\": \"false\",\"reason\": \"no license.txt document found\",\"solutionId\": \"0357ff93-46f4-45df-a6f7-7f2cf5ea779f\",\"revisionId\": \"0ce6b633-0604-4a59-9ab2-bad78952c54a\",\"scanTime\": \"190416-143640\",\"root_license\": {\"name\": \"\",\"type\": \"SPDX\"}}";
			
			File file = File.createTempFile("scanresult", ".json");
			FileWriter writer = new FileWriter(file);
			writer.write(scanResultJson);
			writer.close();

			FileInputStream fileInputStreamMock = PowerMockito.mock(FileInputStream.class);
			PowerMockito.whenNew(FileInputStream.class).withArguments(scanResultJsonFilePath).thenReturn(fileInputStreamMock);
			DataInputStream stream = PowerMockito.mock(DataInputStream.class);
			PowerMockito.whenNew(DataInputStream.class).withArguments(fileInputStreamMock).thenReturn(stream);
			
			RepositoryLocation repositoryLocation = mock(RepositoryLocation.class);
			NexusArtifactClient nexusClient = mock(NexusArtifactClient.class);
			PowerMockito.whenNew(NexusArtifactClient.class).withArguments(repositoryLocation).thenReturn(nexusClient);
		
			UploadArtifactInfo uploadInfo = mock(UploadArtifactInfo.class);
			when(env.getProperty("nexus.groupId")).thenReturn("group");
			when(nexusClient.uploadArtifact(getNexusGroupId(solutionId, revisionId), name, accessType,
					"json", 1245, stream)).thenReturn(uploadInfo);
			
			UploadArtifactSVOutput uploadArtifactSVOutput = new UploadArtifactSVOutput(env);
			uploadArtifactSVOutput.addCreateArtifact(solutionId,revisionId,accessType,userId,file);
	}

	private String getNexusGroupId(String solutionId, String revisionId) {
		String group = env.getProperty("nexus.groupId");

		if (SecurityVerificationServiceUtils.isEmptyOrNullString(group))
			throw new IllegalArgumentException("Missing property value for nexus groupId.");
		return String.join(".", group, solutionId, revisionId);
	}
	
	
	private String scanOutJsonLocation(String folder,String jsonFlieName) {
		StringBuilder scanJsonOutFliePath = new StringBuilder();
		scanJsonOutFliePath.append(SVServiceConstants.SECURITY_SCAN);
		scanJsonOutFliePath.append(SVServiceConstants.FORWARD_SLASH);
		scanJsonOutFliePath.append(folder);
		scanJsonOutFliePath.append(jsonFlieName);
		return scanJsonOutFliePath.toString();
	}
}
