/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
 * Modifications Copyright (C) 2019 Nordix Foundation.
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

import org.junit.Test;

public class WorkflowTest {

  @Test
  public void testIsWorkflowAllowed() {
    boolean workflowAllowed = true;
    Workflow workflow = new Workflow();
    workflow.setReason("reason");
    workflow.setSvException("svException");
    workflow.setWorkflowAllowed(workflowAllowed);
    assertNotNull(workflow);
    assertNotNull(workflow.getReason());
    assertNotNull(workflow.getSvException());
  }
}
