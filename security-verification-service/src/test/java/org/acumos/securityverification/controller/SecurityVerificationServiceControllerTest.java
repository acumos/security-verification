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
package org.acumos.securityverification.controller;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import java.lang.invoke.MethodHandles;

import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.securityverification.service.ISecurityVerificationService;
import org.acumos.securityverification.transport.SVResponse;
import org.acumos.securityverification.utils.SVServiceConstants;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.test.web.servlet.MockMvc;

public class SecurityVerificationServiceControllerTest {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired
	private MockMvc mockMvc;

	@Mock
	private Environment env;
	
	@Mock
	ISecurityVerificationService securityVerificationService;

	@InjectMocks
	private SecurityVerificationServiceController securityVerificationServiceController;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		mockMvc = standaloneSetup(securityVerificationServiceController).build();
		when(env.getProperty(SVServiceConstants.CDMS_CLIENT_URL)).thenReturn("http://cds.com:8001/ccds");
		when(env.getProperty(SVServiceConstants.CDMS_CLIENT_USER)).thenReturn("MockedSpringEnvString");
		when(env.getProperty(SVServiceConstants.CDMS_CLIENT_PWD)).thenReturn("MockedSpringEnvString");

	}

	@Test
	public void testSecurityVerification() throws Exception {
		String solutionId = "bf0478bc-0d3f-4433-80b6-d7a0f6db2df2";
		String revisionId = "ba742c3b-039c-4a8e-b0fc-1aee7d5a9d67";
		String workflowId = "deploy";
		ICommonDataServiceRestClient client = mock(CommonDataServiceRestClientImpl.class);
		PowerMockito.whenNew(CommonDataServiceRestClientImpl.class)
				.withArguments(env.getProperty(SVServiceConstants.CDMS_CLIENT_URL),
						env.getProperty(SVServiceConstants.CDMS_CLIENT_USER),
						env.getProperty(SVServiceConstants.CDMS_CLIENT_PWD), null)
				.thenReturn((CommonDataServiceRestClientImpl) client);
		securityVerificationService.securityVerification(solutionId, revisionId, client);
		SVResponse svResponse = securityVerificationServiceController.securityVerification(solutionId, revisionId,
				workflowId);
		assertNotNull(svResponse);
	}

	@Test
	public void testSiteConfigVerification() throws Exception {
		SVResponse svResponse = securityVerificationServiceController.siteConfigVerification();
		assertNotNull(svResponse);
	}

}
