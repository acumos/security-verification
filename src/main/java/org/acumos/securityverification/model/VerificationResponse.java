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

package org.acumos.securityverification.model;

import java.util.ArrayList;
import java.util.List;

public class VerificationResponse extends SVResponse {
	
	private List<String> messages;
	
	public VerificationResponse(String status) {
		super(status);
		messages = new ArrayList<String>();
	}
	
	

	public List<String> getMessages() {
		return this.messages;
	}



	public void setMessages(List<String> messages) {
		this.messages = messages;
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((this.messages == null) ? 0 : this.messages.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		VerificationResponse other = (VerificationResponse) obj;
		if (this.messages == null) {
			if (other.messages != null)
				return false;
		} else if (!this.messages.equals(other.messages))
			return false;
		return true;
	}

	
}
