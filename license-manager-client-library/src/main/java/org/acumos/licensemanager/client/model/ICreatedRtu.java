/*-
 * ===============LICENSE_START=======================================================
 * Acumos Apache-2.0
 * ===================================================================================
 * Copyright (C) 2019 Nordix Foundation.
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

package org.acumos.licensemanager.client.model;

import java.util.List;

import org.acumos.cds.domain.MLPRightToUse;

/**
 * <p>ICreatedRtu interface.</p>
 *
 *CreatedRtu
 *
 * @author est.tech
 * @version 0.0.2
 */
public interface ICreatedRtu extends IErrorResponse {

	/**
	 * <p>isCreated.</p>
	 *
	 * @return if the RTU was created
	 */
	boolean isCreated();

	/**
	 * <p>isUpdated.</p>
	 *
	 * @return if instead of creating we updated the rtu This will indicate that the
	 *         rtu references will be overriden
	 */
	boolean isUpdated();

	/**
	 * <p>getRequest.</p>
	 *
	 * @return original request
	 */
	ICreateRTURequest getRequest();

	/**
	 * <p>setRequest.</p>
	 *
	 * @param request the original create request
	 */
	void setRequest(ICreateRTURequest request);

	/**
	 * <p>getRtus.</p>
	 *
	 * @return list of Rtus created in CDS
	 */
	List<MLPRightToUse> getRtus();

}
