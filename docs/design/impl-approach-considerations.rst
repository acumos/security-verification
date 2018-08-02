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

======================================
Implementation Approach Considerations
======================================

Given the importance of managing risks such as above, the key question is how
that capability needs to, or can be, implemented. This design assumes that the
goal of the S-V component of the Acumos platform is to provide a comprehensive,
as-much-automated-as-possible, platform-integrated service that fulfills the
goals. This
  may not be achievable in a single release or in full, since:

* project resource may be insufficient
* technical solutions to some of the goals may be unavailable, e.g.

  * machine-learning technologies are fairly new and ability
    to detect malicious design in compiled models (e.g. in pickle files) may be
    limited technically
  * when compared to signatures of well-known or new threats to host systems or
    consumer devices (e.g. PCs) as supported by open source virus/malware scan
    tools, there may be limited experience thus limited libraries of threat
    signatures for compiled ML models

Thus alternate/fallback implementation approaches are described below, so that
as many of the goals as possible can be delivered. These alternate approaches are based upon the
following assessments of how the bylaws goals related to potential implementation
approaches, such as:

* a comprehensive, as-much-automated-as-possible, platform-integrated service

  * this is the stated default approach, given that resources and technical
    solutions are available

* a hybrid approach of some manual processes, supplementing the automated
  platform capabilities, e.g.

  * manual admin of the platform capabilities through configuration files that
    are provisioned on the platform hosts and used by the components in the
    absence of portal-marketplace admin UI support for the same configuration
  * exporting solution packages (artifacts and metadata) for offline scanning,
    in the absence of integrated, automated scanning tools
  * maintaining the status of scans (e.g. unrequested, in-progress, successful,
    failed) as a key input to enabling/blocking workflows for solutions, through
    a manual but API-supported process, in the absence of automated updates of
    status based upon integrated scanning

* a fully manual, open source toolset-supported process that is ensured by
  establishment of community policies and related practices

  * in this case there may be no specific platform-integrated support for
    scanning, verification status management, policy definition or control of
    workflows per those policies, etc
  * open source toolsets and user guides however could be provided to help
    operators/admins to fulfill the requirements of their company and of the
    Acumos ecosystem
  * beyond the above, a priority would be placed on a "trust but verify"
    approach to policy adherence and modeler behaviors that support best
    practices and policies

Depending on how the Acumos community prioritizes the goals of S-V, the
various approaches above, and how successful the S-V team is in resourcing and
addressing technical challenges of the design below, one  to many of these
hybrid/manual approach elements may be implemented.