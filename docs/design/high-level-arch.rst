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


* Security-Verification Service:

  * provides APIs

    * to serve requests to perform scanning jobs as required (per site admin)

  * uses CDS site-config data to determine when to invoke scanning
  * uses CDS site-config data, solution data, and scan result to determine the verification statuses for a solution/revision and updates CDS database
  * allows Acumos operators to use a default set of scan tool or to integrate other tools via a plugin-style interface
  * runs as an always-on service under docker or kubernetes



