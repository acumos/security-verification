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

package org.acumos.securityverification.scantool;

import org.springframework.stereotype.Service;

/**
 * https://help.sonatype.com/iqserver/rest-apis/component-evaluation-rest-apis---v2
 */
@Service
public class NexusIQWrapper implements LicenseScanTool, VulerabilityScanTool {

	@Override
	public Object scanForVulnerability() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object scanForLicense() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	/*
	 * call POST /api/v2/evaluation/applications/{applicationInternalId}
	 */
	private void submitComponentForEvaluation() throws Exception {
		
	}

	/**
	 * Calls NexusIQ API: GET /api/v2/evaluation/applications/{applicationInternalId}/results/{resultId}
	 */
	@Override
	public Object retrieveScanResult() throws Exception {
		/* 
		 * if result isn't in CDS, retrieve from NexusIQ, store, and return data
		 * If the report is not ready, you will receive a (404) error.
		 */
		return null;
		
	}
	/*
	 * Webhooks can be configured in NexusIQ server https://help.sonatype.com/iqserver/iq-server-webhooks
	 * Webhooks are HTTP callbacks that POST data to defined URLs. They let you build integrations registered to certain events on IQ Server. When a registered event is triggered, an HTTP POST payload is sent to the webhook’s defined URL.

Use webhooks to receive notifications about events that happen in IQ Server. When an event occurs - for example, when an application evaluation is completed - IQ Server creates an event object. This object has relevant information about what just happened, such as the type of event and any data associated with the event. IQ Server then sends the event object to defined URLs using HTTP POST requests.

IQ Server lets you use webhooks for policy management, application evaluation, security vulnerability override management, and license override management events.

Each webhook is defined by the following:

The URL to POST the payload.
An optional secret key that ensures authenticity of the source.
Event types subscribed by the webhook.
	 */
	
	
}
