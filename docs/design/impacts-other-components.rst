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

==================================
Impacts to other Acumos Components
==================================

Portal-Marketplace
==================

Existing calls to the Validation-Security service (deprecated) will be removed
and new calls will be required to the Security-Verification service per the
supported workflow scanning options and workflow verification gates described
in the "Verification Status" and "Scan Invocation" sections. The specific
impacts on the Portal-Marketplace component will be analyzed and described here.

The Portal-Marketplace UI for users and admins will be impacted in various ways.
The impacts will be described here, and are expected to include at a high level:

* removal of existing UI elements related to the Validation-Security component
* UI elements conveying that workflows are blocked due to required/incomplete
  solution verification, e.g. grayed out workflow options with tooltip hints,
  popup dialogs explaining why a workflow can't be completed at this time, or
  additional notification entries.
* admin of the options for S-V service as described under "Current Release
  Features"

**ALTERNATIVE IMPLEMENTATION**

The configuration options are provided to the Verification Service through a
JSON/YAML file that is placed/updated by admins on the host that is running the
Verification Service, in a shared folder. The Verification Service monitors that
folder for updates, and when detecting a new config file, saves the options to
the CDS through the same API that the Portal-BE service would use.

Federation
==========