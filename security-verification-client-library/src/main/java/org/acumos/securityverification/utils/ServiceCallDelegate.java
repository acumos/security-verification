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
package org.acumos.securityverification.utils;

import org.springframework.web.client.RestTemplate;

/**
 * ServiceCallDelegate class used to call the Rest API. 
 */
public class ServiceCallDelegate {

	/**
	 * This method is used to make post method call. 
	 * @param serviceUrl
	 * @param jObject
	 * @param clazz
	 * @return response, of type T
	 */
	public <T> T callDelegate(String serviceUrl,Object jObject, Class<T> clazz) {
		try {
			RestTemplate restTemplate = new RestTemplate();
			return (T) restTemplate.postForObject(serviceUrl, jObject, clazz);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

}