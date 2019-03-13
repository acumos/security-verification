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

import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.nexus.client.NexusArtifactClient;
import org.acumos.nexus.client.RepositoryLocation;
import org.acumos.securityverification.utils.Configurations;
import org.acumos.securityverification.utils.SVUtils;

public abstract class AbstractServiceImpl {

	
	public ICommonDataServiceRestClient getClient() {
		ICommonDataServiceRestClient client = new CommonDataServiceRestClientImpl(Configurations.getConfig("cdms.client.url"),
				 Configurations.getConfig("cdms.client.username"), Configurations.getConfig("cdms.client.pwd"), null);
		return client;
	}
	
	public NexusArtifactClient getNexusClient() {
		RepositoryLocation repositoryLocation = new RepositoryLocation();
		repositoryLocation.setId("1");
		repositoryLocation.setUrl(Configurations.getConfig("nexus.client.url"));
		repositoryLocation.setUsername(Configurations.getConfig("nexus.client.username"));
		repositoryLocation.setPassword(Configurations.getConfig("nexus.client.pwd"));

		if (!SVUtils.isEmptyOrNullString(Configurations.getConfig("nexus.proxy"))) {
				repositoryLocation.setProxy(Configurations.getConfig("nexus.proxy"));
		}

		NexusArtifactClient artifactClient = new NexusArtifactClient(repositoryLocation);
		return artifactClient;
	}
}
