.. ===============LICENSE_START=======================================================
.. Acumos CC-BY-4.0
.. ===================================================================================
.. Copyright (C) 2017-2018 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
.. ===================================================================================
.. This Acumos documentation file is distributed by AT&T and Tech Mahindra
.. under the Creative Commons Attribution 4.0 International License (the "License");
.. you may not use this file except in compliance with the License.
.. You may obtain a copy of the License at
..
.. http://creativecommons.org/licenses/by/4.0
..
.. This file is distributed on an "AS IS" BASIS,
.. WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
.. See the License for the specific language governing permissions and
.. limitations under the License.
.. ===============LICENSE_END=========================================================

=========================
Fetch Verification Status
=========================

** this isn't necessary if the scan status and result is stored in the database; the s-v code that processes a scan result can determine the scan status and update the solution/revision accordingly **

API definition :ref:`api-verification-status`

Acumos components shall call the Verification Status API when they need to check
if a workflow should proceed based upon the admin requirements for verification
related to that workflow and the status of verification for a solution/revision, which are stored in the security-verification site config.


based upon site-config settings that require specific
verification criteria, as well as the actual record of scanning as recorded in a
scan-results solution artifact.

The Verification Service will use the following process to determine the API
result:

* If the requested workflowId is invalid return 400 BAD REQUEST with status
  "invalid workflowId", and exit.
* If the requested solutionId or revisionId are not found in the CDS return
  404 NOT FOUND with status as appropriate to the error found, and exit.
* If the CDS config license-verify attribute array member for the request
  workflowId value is "false" and the CDS config vulnerability-verify attribute
  array member for the request workflowId value is "false", return 200 OK with
  status "workflow permitted" message "workflow not gated", and exit.
* If the CDS config vulnerability-verify attribute array member for the request
  workflowId value is "true" and the CDS
  solution/{solutionId}/revision/{revisionId} attribute verified-vulnerability
  value is not requested, in progress, or failed, add status
  "workflow not permitted" and message "vulnerability scan <value>" per the
  verified-vulnerability value to the response.
* If the CDS config license-verify attribute array member for workflowId
  value is "true" and  and the CDS
  solution/{solutionId}/revision/{revisionId} attribute verified-license
  value is "unrequested, "in-progress", or "failure", add status
  "workflow not permitted" and message "license scan <value>" per the
  verified-license value to the response.
* If after the above steps, the response body status attribute is unset, return
  200 OK with status "workflow permitted" and message
  "all workflow gates cleared".
