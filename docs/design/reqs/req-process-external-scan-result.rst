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

============================
Process External Scan Result
============================

** THIS IS PRETTY MUCH THE SAME AS :ref:`process-scan-results`. THE EXTERNAL TOOLS WILL ALL GENERATE SCAN RESULTS THAT ARE DIFFERENT, SO THE S-V COMPONENT'S TOOL WRAPPER OBJECTS WOULD PROCESS THE RESULTS ACCORDINGLY. **

The service exposes the :ref:`scan-result` API to allow optional external scan
functions/processes to report back on the status of scans. Tools that have Web Hooks configured can call this API with results from a scan.

