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

=======================
High-Level Architecture
=======================

The following diagram illustrates the integration of S-V into an Acumos platform:

.. image:: ../images/security-verification-arch.png


Functional Components
.....................

The S-V service shall include two component microservices:

* Verification Service: this is the fontend to the S-V service

  * provides all S-V APIs to other Acumos components

    * to serve requests to perform scanning jobs as required (per site admin)
    * to check the status of verification for workflow gates

  * uses CDS site-config data to determine when to invoke scanning
  * uses CDS site-config data and solution data to determine how to respond to
    requests for the status of verification
  * runs as a always-on service under docker-ce or kubernetes


* Scanning Service: this is the backend to the S-V service

  * provides a scanning API to the S-V Verification Service, to execute scan
    operations as needed using scanning tools for license and vulnerabilities
  * allows Acumos operators to use a default set of scan tools, or to integrate
    other tools via a plugin-style interface
  * runs as an always-on service under docker, or an on-demand job under
    kubernetes

** QUESTION: WHY TWO SEPARATE SERVICES? I DON'T SEE THE NEED. **