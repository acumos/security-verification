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
package org.acumos.securityverification.transport;

import static org.junit.Assert.assertNotNull;

import org.acumos.securityverification.transport.ErrorTransport;
import org.junit.Test;

public class ErrorTransportTest {

	private ErrorTransport errorTransport;
	private int statusCode = 400;
	private String errMsg = "Error occured";
	private Exception exception = new Exception();

	@Test
	public void testErrorTransport() {
		errorTransport = new ErrorTransport(statusCode, errMsg);
		assertNotNull(errorTransport);
	}

	@Test
	public void testErrorTransportIntStringException() {
		errorTransport = new ErrorTransport(statusCode, errMsg, exception);
		assertNotNull(errorTransport);
	}

	@Test
	public void testGetStatus() {
		errorTransport = new ErrorTransport(statusCode, errMsg, exception);
		assertNotNull(errorTransport.getStatus());
	}

	@Test
	public void testSetStatus() {
		errorTransport = new ErrorTransport();
		errorTransport.setStatus(200);
		assertNotNull(errorTransport.getStatus());
	}

	@Test
	public void testGetError() {
		errorTransport = new ErrorTransport(statusCode, errMsg, exception);
		assertNotNull(errorTransport.getError());
	}

	@Test
	public void testSetError() {
		errorTransport = new ErrorTransport();
		errorTransport.setError("Error occured");
		assertNotNull(errorTransport.getError());
	}

	@Test
	public void testGetException() {
		errorTransport = new ErrorTransport();
		errorTransport.setException("Exception occured");
		assertNotNull(errorTransport.getException());
	}

	@Test
	public void testSetException() {
		errorTransport = new ErrorTransport();
		errorTransport.setException("Exception occured");
		assertNotNull(errorTransport.getException());
	}

}
