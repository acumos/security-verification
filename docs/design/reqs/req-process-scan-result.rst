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

.. _process-scan-results:

====================
Process Scan Results
====================


Each tool provides scan result data in different formats.

When a scan result is available, a MLPSolutionVerification object shall be created and stored in CDS to store scan data. Also, at this is the point the verification status values should be calculated and stored with the solution/revision object. Tools with Web Hooks can call the :ref:`.. _scan-result` API, passing the scan results in the message body. The ScanResultController would then locate the appropriate tool wrapper class to process the results.


This should send a notification to the portal so it knows to pull the updated revision/solution verification status. Otherwise how will the portal know that a requested scan has been completed? it's not practical for the portal to keep pulling objects from the database (or in the previous design, keep calling /verify)


----------------------------------------------------------------------------------------------------------

** CONCERN: THIS APPROACH IS PROBLEMATIC AND MOST LIKELY NOT POSSIBLE GIVEN THE THIRD PARTY TOOLS AVAILABLE. **

Details of the scan process for license and vulnerabilities is TBD.

During the scan process, as scanning is completed for each artifact or metadata
item in the scanresult.json file, the service:

* updates the corresponding scanresult.json licenseScan or vulnerabilityScan
  attribute per the result of the scan, i.e. as "success" or "failure" ** again, why is this not being updated in the database? ***
* if the result of the scan is "failure", updates the corresponding CDS
  licenseScan or vulnerabilityScan attribute for the solution/revision to
  "failure"
* updates the scanresult.json artifact in the nexus-service, and update the CDS
  (if required) for the new artifact version

When scanning is completed for the last item in the scanresult.json file:

* if the current value of the corresponding CDS attribute for licenseScan
  or vulnerabilityScan is "in-progress", the service sets the attribute
  to "success"



**ALTERNATIVE IMPLEMENTATION**

Assuming the alternate (offline) implementation of "Perform Scan" above is
chosen, when the offline scan execution is complete, the admin places a
scanresult.json file in the host-shared solution/revision folder. The S-V
service, upon detecting the presence of a new or updated scanresult.json file:

* updates the scanresult.json artifact in the nexus-service and CDS as above
* if any of the licenseScan and vulnerabilityScan attributes for the artifacts
  or metadata items in the scanresult.json file are "failure", updates the
  corresponding CDS licenseScan or vulnerabilityScan attribute for the
  solution/revision to "failure"
* when all licenseScan and vulnerabilityScan attributes for the artifacts
  or metadata items in the scanresult.json file have been updated, if the
  current value of either the licenseScan and vulnerabilityScan CDS attributes
  is "in-progress", the Scanning Service sets the attribute to "success"



** CONCERN: instead of creating a scanresults.json file at the beginning of the scan, why not just update the database? then when scan results are processed, create/update a scanresults file, create artifact in CDS for solution/revision, store scanresults in nexus under solution/revision. **