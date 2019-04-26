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
package org.acumos.securityverification.domain;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class SecurityVerificationCdumpTest {

	@Test
	public void testSecurityVerificationCdump() {
		List<SecurityVerificationCdumpNode> nodes = new ArrayList<>();
		SecurityVerificationCdump securityVerificationCdump = new SecurityVerificationCdump();
		securityVerificationCdump.setCid("cid");
		securityVerificationCdump.setCname("cname");
		securityVerificationCdump.setNodes(nodes);
		securityVerificationCdump.setSolutionId("solutionId");
		securityVerificationCdump.setVersion("version");
		assertNotNull(securityVerificationCdump);
		assertNotNull(securityVerificationCdump.getCid());
		assertNotNull(securityVerificationCdump.getCname());
		assertNotNull(securityVerificationCdump.getNodes());
		assertNotNull(securityVerificationCdump.getSolutionId());
		assertNotNull(securityVerificationCdump.getVersion());
		assertNotNull(securityVerificationCdump.hashCode());
		assertNotNull(securityVerificationCdump.equals(securityVerificationCdump));
		assertNotNull(securityVerificationCdump.equals(null));
	}
}