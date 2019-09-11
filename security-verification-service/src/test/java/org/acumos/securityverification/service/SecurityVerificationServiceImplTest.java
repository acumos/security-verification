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

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.invoke.MethodHandles;

import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.client.CommonDataServiceRestClientMockImpl;
import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.domain.MLPSiteConfig;
import org.acumos.securityverification.utils.SVServiceConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

// @RunWith(MockitoJUnitRunner.class)
@RunWith(MockitoJUnitRunner.Silent.class)
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "javax.management.*"})
@PrepareForTest({ SecurityVerificationServiceImpl.class })
public class SecurityVerificationServiceImplTest extends CommonDataServiceRestClientMockImpl {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Mock
	private Environment env;

	@Mock
	private RestTemplate restTemplate;

	@InjectMocks
	private SecurityVerificationServiceImpl securityVerificationServiceImpl;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		securityVerificationServiceImpl = new SecurityVerificationServiceImpl();
		securityVerificationServiceImpl.setEnvironment(this.env);
		when(env.getProperty(SVServiceConstants.CDMS_CLIENT_URL)).thenReturn("http://cds.com:8001/ccds");
		when(env.getProperty(SVServiceConstants.CDMS_CLIENT_USER)).thenReturn("MockedSpringEnvString");
		when(env.getProperty(SVServiceConstants.CDMS_CLIENT_PWD)).thenReturn("MockedSpringEnvString");
	}

	@Test
	public void testCreateSiteConfig() throws Exception {
		when(env.getProperty("siteConfig.verification")).thenReturn("siteConfigJsonFromConfiguration");
		ICommonDataServiceRestClient client = mock(CommonDataServiceRestClientImpl.class);
		PowerMockito.whenNew(CommonDataServiceRestClientImpl.class)
				.withArguments(env.getProperty(SVServiceConstants.CDMS_CLIENT_URL),
						env.getProperty(SVServiceConstants.CDMS_CLIENT_USER),
						env.getProperty(SVServiceConstants.CDMS_CLIENT_PWD), null)
				.thenReturn((CommonDataServiceRestClientImpl) client);
		ResponseEntity responseEntity = mock(ResponseEntity.class);
		when(restTemplate.getForEntity(Mockito.anyString(), any(Class.class))).thenReturn(responseEntity);
		MLPSiteConfig mlpSiteConfig = mock(MLPSiteConfig.class);
		when(client.getSiteConfig(SVServiceConstants.SITE_VERIFICATION_KEY)).thenReturn(null);
		MLPSiteConfig mlpSiteConfigFromDB = mock(MLPSiteConfig.class);
		MLPSiteConfig config = mock(MLPSiteConfig.class);
		mlpSiteConfigFromDB = (MLPSiteConfig) client.createSiteConfig(config);
		when((MLPSiteConfig) client.getSiteConfig(SVServiceConstants.SITE_VERIFICATION_KEY))
				.thenReturn(mlpSiteConfigFromDB);

		String getConfigValue = securityVerificationServiceImpl.createSiteConfig(client);
		assertNotNull(getConfigValue);
	}

	@Test
	public void testSecurityVerification() throws Exception {

		String solutionId = "bf0478bc-0d3f-4433-80b6-d7a0f6db2df2";
		String revisionId = "ba742c3b-039c-4a8e-b0fc-1aee7d5a9d67";
		ICommonDataServiceRestClient client = mock(CommonDataServiceRestClientImpl.class);
		SecurityVerificationScan securityVerificationRunner = mock(SecurityVerificationScan.class);
		
		PowerMockito.whenNew(SecurityVerificationScan.class).withAnyArguments().thenReturn(securityVerificationRunner);
		Thread t = mock(Thread.class);
		PowerMockito.whenNew(Thread.class).withAnyArguments().thenReturn(t);
		securityVerificationServiceImpl.securityVerification(solutionId, revisionId, client);
		assertNotNull(t);
	}

	@Test
	public void testSetEnvironment() {
		securityVerificationServiceImpl.setEnvironment(env);
		assertNotNull(env);
	}
	
}