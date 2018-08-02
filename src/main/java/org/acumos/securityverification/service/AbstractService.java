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

import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.securityverification.utils.EELFLoggerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

public abstract class AbstractService {
	protected final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(getClass().getName());
	protected ICommonDataServiceRestClient cdsClient;
	
	
	@Value("${nexus.nexusEndPointURL}")
	protected String nexusEndPointURL;

	@Value("${nexus.nexusUserName}")
	protected String nexusUserName;

	@Value("${nexus.nexusPassword}")
	protected String nexusPassword;

	@Value("${nexus.nexusGroupId}")
	protected String nexusGroupId;

	@Value("${cmndatasvc.cmnDataSvcEndPoinURL}")
	protected String cmnDataSvcEndPoinURL;

	@Value("${cmndatasvc.cmnDataSvcUser}")
	protected String cmnDataSvcUser;

	@Value("${cmndatasvc.cmnDataSvcPwd}")
	protected String cmnDataSvcPwd;

	public AbstractService() {
	/*	cdsUrl = 
		cdsClient = CommonDataServiceRestClientImpl.getInstance(url.toString(), userName,
					password);*/
	}
}
