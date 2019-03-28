/*-
 * ===============LICENSE_START=======================================================
 * Acumos Apache-2.0
 * ===================================================================================
 * Copyright (C) 2019 Ericsson Software Technology AB. All rights reserved.
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

/**
 * Construct a request to create a RTU
 *
 *CreatedRtu
 *
 * @author est.tech
 * @version 0.0.2
 */
public interface ICreateRTURequest extends ILicenseRequest {

	/**
	 * <p>getRTURefs.</p>
	 *
	 * @return list of UUIs used for creating a MLPRtuReference
	 * @see org.acumos.cds.domain.MLPRtuReference
	 */
	String[] getRTURefs();

	/**
	 * <p>isSiteWide.</p>
	 *
	 * @return Is the right to use specific to a user or applicable site wide
	 * @see org.acumos.cds.domain.MLPRightToUse#site
	 */
	boolean isSiteWide();

	/**
	 * When creating the RTU if applicable to entire site
	 *
	 * @param siteWide create rtu for solution for entire site
	 * @see org.acumos.cds.domain.MLPRightToUse#site
	 */
	void setSiteWide(boolean siteWide);

	/**
	 * <p>getRTUId.</p>
	 *
	 * @return Right to use ID
	 * @see org.acumos.cds.domain.MLPRightToUse#rtuId
	 */
	Long getRTUId();

	/**
	 * <p>setRtuId.</p>
	 *
	 * @param rtuId id for the right to use
	 * @see org.acumos.cds.domain.MLPRightToUse#rtuId
	 */
	void setRtuId(long rtuId);

	/**
	 * In Boreas only supporting one generated UUID for the
	 * right to use. If you want to add additional rtuRefs
	 * you can use this api to update and all refs will be created.
	 * This is not a required property when creating a right to use.
	 *
	 * @param rtuRefs UUID for each right to use
	 * @see org.acumos.cds.domain.MLPRightToUse#rtuReferences
	 */
	void setRtuRefs(String[] rtuRefs);





}
