/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
 * Modifications Copyright (C) 2019 Nordix Foundation.
 * ===================================================================================* This Acumos software file is distributed by AT&T and Tech Mahindra
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

import org.junit.Test;

public class SecurityVerificationCdumpNodeTest {

  @Test
  public void testSecurityVerificationCdumpNode() {
    SecurityVerificationCdumpNode securityVerificationCdumpNode =
        new SecurityVerificationCdumpNode();
    securityVerificationCdumpNode.setName("name");
    securityVerificationCdumpNode.setNodeId("nodeId");
    securityVerificationCdumpNode.setNodeSolutionId("nodeSolutionId");
    securityVerificationCdumpNode.setNodeVersion("nodeVersion");
    assertNotNull(securityVerificationCdumpNode);
    assertNotNull(securityVerificationCdumpNode.getName());
    assertNotNull(securityVerificationCdumpNode.getNodeId());
    assertNotNull(securityVerificationCdumpNode.getNodeSolutionId());
    assertNotNull(securityVerificationCdumpNode.getNodeVersion());
  }
}
