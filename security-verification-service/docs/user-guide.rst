.. ===============LICENSE_START=======================================================
.. Acumos CC-BY-4.0
.. ===================================================================================
.. Copyright (C) 2017-2019 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
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

================================
Security Verification User Guide
================================

............
Introduction
............

This guide focuses on the things that users need to know about the Security
Verification (SV) feature of the Acumos platform. It is intended for use by:

* Acumos platform users, e.g. machine-learning application ("solution")
  developers ("modelers") or consumers ("end-users")
* Acumos platform administrators ("admins"), i.e. those responsible for setting
  up and maintaining an Acumos platform
* Acumos platform users in other supervisory roles, e.g. as a marketplace catalog
  admin ("publishers")

The `Security Verification Design Specification <https://docs.acumos.org/en/boreas/submodules/security-verification/security-verification-service/docs/index.html>`_
outlines the principle rationales for and features provided by the SV feature.
In summary, these include:

* for modelers and end-users: ensuring that solutions they contribute to or
  obtain from an Acumos platform are covered by clear and compatible licenses

  * "clear" means that the license is a clearly recognized license, either a
    well-known open source license or a proprietary license
  * "compatible" means both that:

    * the licenses are in compliance with the policies established by the
      Acumos platform operator
    * all files associated with the solution are compatible with the license
      under which the solution is published (the "root license")

  * ensuring that details of license scans are included with solutions, so that
    any potential issues can be easily identified
  * ensuring for modelers that their solutions, if covered by a proprietary
    license with RTU (right-to-use) requirements, are not usable by anyone that
    does not possess a RTU for the specific solution

* for admins: ensuring policies and processes related to license scanning and
  license verification can be easily defined and maintained, including

  * which types of licenses are allowed for solutions on the Acumos platform
  * which types of licenses are considered compatible with root licenses of
    solutions on the Acumos platform
  * what types of user workflows should be used as triggers for license scans
  * what types of user workflows should gated by the status of license scans
  * implementation of an operator-specific process for license scans

* for publishers: ensuring that details of license scan status are documented,
  and any issues with scans can be easily identified

A key design feature of the SV feature is that is almost completely configurable,
so that it meets these goals:

* admins can tailor policies and processes so that a good balance is created
  between the goals of enabling low barriers to solution development and publication,
  while thoroughly vetting the solution licenses per operator policies
* modelers can work on their models without any potential barriers to workflows
  ("workflow gates"), up to solution publication; and even publication can be
  removed as a workflow gate if chosen by the operator
* within an organization if desired, modelers can freely share, locally publish,
  and use locally published solutions without license verification overhead

The rest of this document introduces key concepts such as the Acumos license
schema, and addresses how the SV feature supports those goals for
each type of user.

..........................................
license.json and the Acumos License Schema
..........................................

Acumos solution licenses are in JSON format, and associated with the solution
as an artifact named 'license.json'. The schema for these artifacts is at an
early stage of development, and defined in detail by the
`License Manager <https://wiki.acumos.org/display/LM>`_ project. However, for
the purposes of the SV feature, the only important item in the license.json
file is the presence of a well-known identifier for a license:

* as defined by the Linux Foundation SPDX project in the
  `SPDX License List <https://spdx.org/licenses/>`_ (the 'Identifier').
* as configured in the Acumos platform by an admin, e.g. for additional types
  of licenses that are allowed for use with solutions in that platform

As currently defined, an example well-known license would be disclosed in the
"keyword" attribute of the "licenses" array in license.json files, e.g.:

.. code-block:: text

  {
    "licenses": [
      {
        "keyword": "Apache-2.0",
  ...
  }
..

................................
Licenses in other Solution Files
................................

One of the key functions of the SV Scanning service is to scan all files
associated with a solution, to determine which files carry a well-known license,
and whether that license is compatible with the root license. For that purpose,
the SV Scanning service will scan:

* the description that is associated with a solution in a particular catalog,
  since that is a convenient place to disclose licenses etc that modelers
  may want to disclose up-front, and that end-users may want to know about
* all files associated with the solution as "documents" published along with
  the solution through a catalog; since such "documents" can be any (or mostly any)
  arbitrary file type, they can carry licenses and need to be scanned, e.g.

  * user guides, archives with documentation, etc
  * source code files or archives
  * training data archives

Most open-source or proprietary source code will for example carry explicit
and well-known licenses in either or both:

* the root folder of a source code repository (e.g. the conventional LICENSE.txt)
* license files (e.g. LICENSE.txt) in subfolders for open source components that
  have been imported into the solution
* each source file

.....................
SV Scanning Artifacts
.....................

The SV Scanning process will result in two artifacts associated with each
solution revision that is scanned, updated each time the revision is scanned:

* scanresult.json: the summary findings of the SV Scanning Service for a
  revision; see the description below

* scancode.json: the detailed output of the default scanning tool used by the
  Acumos project, the `Scancode Toolkit <https://github.com/nexB/scancode-toolkit>`_.
  This file contains a lot of information about each file that is scanned, and
  contains either a license or copyright statement. It is used by the SV Scanning Service
  and summarized in scanresult.json.

scanresult.json example:

.. code-block:: text

  {
    "schema": "1.0",
    "verifiedLicense": "<true|false>",
    "reason": "<reason for scan failure, if any>",
    "solutionId" : "<solutionId scanned>",
    "revisionId" : "<revisionId scanned>",
    "scanTime" : "<epoch time value when the scan was started>",
    "root_license": {
      "type": "<type value from the Acumos platform allowedLicense set>",
      "name": "<name value from the Acumos platform allowedLicense set>"
    },
    "files": [
      {
      "path": "<folder path of the file as scanned>",
      "licenses": [
        {
          "name": "<name of a license detected in the file>"
        }
      ]
      }
    ]
  }
..

Notes on the attributes:

* name: the well-known name for a license, e.g. SPDX "Identifier"
* type: "SPDX" (used in this release to indicate an open source license), or
  a type value configured by the Acumos admin e.g. for a proprietary license
* files: an array referencing files for which a license was detected. The path
  value for each file helps identify the file in the hierarchy of scanned files, e.g.

  * model descriptions as defined for catalogs will be named per the catalog
    name, e.g. "description-My-Public-Models.txt"
  * files that were contained in the "model.zip" artifact (if any) will be
    in a subfolder path "model-zip"
  * documents associated with a particular catalog will be contained in a
    subfolder path named for the catalog, e.g. "My-Public-Models"

    * any archives (.zip extension files) associated with the revision as a
      catalog document (e.g. source code archives) will be contained in a subfolder
      of the catalog folder path, named for the archive. For example, an archive
      model-source.zip will be unpacked into a folder named "model-source-zip"

.....................................
Workflows, Gates, and Scan Triggering
.....................................

Workflows are actions that a user (modeler, end-user) invoke for a solution, and
include:

* update (addition/update of artifacts or documents)
* deploying a model
* downloading model artifacts or documents
* sharing a model with another user
* publishing a model to a marketplace (public, or restricted)

The admin can configure any of the workflows above as triggers for invoking
a license scan. The scan occurs in the background, and by itself does not gate
any workflow. However, note that:

* the workflow itself may be gated by the policy set by the platform admin, if
  no prior scan had been invoked or a prior scan was unsuccessful
* if the workflow is gated as described below, it may be allowed in a very
  short time. Typically, license scans take less than 30 seconds, thus if
  successful, the scan status will be updated quickly.

Workflow gates are workflows that the admin has configured to require a
successful license scan, prior to completion of the workflow (note again that
solution owners are not subject to these gates except for publishing to a public
catalog). Workflow gates can include:

* deploying a model
* downloading model artifacts or documents
* sharing a model with another user
* publishing a model to a marketplace (public, or restricted)

You might wonder why "update" is not considered a workflow gate: the reason is
that:

* only the solution owner can update a solution
* update is the only way the solution owner can correct any earlier issues
  detected by license scans, so should not itself be gated

If a gate is not passed, the user will receive a popup dialog that explains
why the workflow cannot be completed at the current time, including:

* "license scan not yet started"
* "license scan in-progress"
* "license scan failed", with explanation

  * "no right to use": the user has no RTU provisioned for a proprietary model
  * the "reason" attribute of the scanresult.json artifact, e.g.

    * "no license artifact found, or license is unrecognized": a license.json file
      has not been uploaded, or no recognized license was found in license.json
    * "root license($root_license) is not allowed": license.json does not have an
      approved license
    * "$file license($name) is not allowed": a license from any other scanned file
      is not allowed
    * "$path license($name) is incompatible with root license $root_name": a
      license from any scanned file is incompatible with the root license

............
For Modelers
............

The basic things you need to know about SV and licenses for Acumos platform
solutions include:

* a "license.json" artifact can be onboarded with your model via CLI or web
  onboarding, although it is optional at onboarding time
* if you are the model owner, you will not be subject to workflow gates
  (verification of license scan results per the operator's policy) until you
  attempt to publish to a public catalog, and only then if the admin has
  configured "Publish to Public Marketplace" as a workflow gate
* if you are a collaborating modeler (i.e. the model has been "shared" with you),
  your workflow permissions may be more restricted than the model owner
* it will be typical for the admin to require a successful license scan prior
  to publishing to a public catalog, so it will help if you ensure that you
  have uploaded a license.json file prior to attempting publication
* see `Workflows, Gates, and Scan Triggering`_ for examples of messages you may
  receive when attempting to publish to a public catalog

.............
For End-Users
.............

Workflows that relate to end-users (not solution owners) include download and
deploy.

If you are a model user, any workflow you attempt may be gated per the site
policy established by an admin; see `Workflows, Gates, and Scan Triggering`_
for examples of messages you may receive when attempting a gated workflow

It is expected to be typical that platform admins will require successful scans
prior to publication to a public catalog, so you should not expect workflows you
request to be blocked due to license scan status, since in most cases a
successful scan will have been completed, before the solution was made
available to you. However, note that workflows may be blocked for a brief period
(typically less than 30 seconds), when a new scan has been invoked in these cases:

* the solution owner has just updated the solution
* the platform admin has configured "download" or "deploy" as scan triggers,
  and some other user just invoked one of those workflows

To see the scan details for a solution, you can download the "scanresult.json"
and "scancode.json" artifacts.

..........
For Admins
..........

Admins have a key role in ensuring a good balance between the goals of enabling
low barriers to solution development and publication, while thoroughly vetting
the solution licenses per operator policies.

Understanding the purpose and effect of the two main features of the SV
Service (license scan triggers, and workflow gates) is key to creating an
effective set of policies for the platform. Each platform may have a different
modeler/user base, relationship to other platforms, and organizational policies
that govern how the platform needs to be configured overall. Thus flexibility in
the SV feature design was key.

The main controls that platform admins have over the SV feature are:

* whether to enable the SV feature: the component template (docker or kubernetes)
  for the Portal-BE component has an attribute of the SPRING_APPLICATION_JSON
  environment parameter that you can use to disable or enable use of the SV
  Service for the platform:

  .. code-block:: text

    "portal": {
      "feature": {
        "sv": {
          "enabled": "<true|false>",
    ...
  ..

* which scan triggers to activate (if any): although there is little cost
  in system resource terms to scanning, you might want to focus the triggers
  for scan invocation based upon the typical solution use patterns for your
  modeler/user base

  * update (addition/update of artifacts or documents)

    * this will provide the earliest and likely most commonly invoked workflow
      as a trigger; so if your priority is have early and up-to-date scan status,
      and minimizing gate blocks for subsequent workflows (assuming successful
      scan), activate this gate as a scan trigger.

  * deploying a model

    * deploying would be of most value as a trigger if update was not configured
      as a trigger, and the solution owner had recently updated the solution

  * downloading model artifacts or documents

    * similar to deploying; most valuable when update is not a trigger

  * sharing a model with another user

    * similar to deploying; most valuable when update is not a trigger

  * publishing a model to a marketplace (public, or restricted)

    * publishing to a public marketplace may be for many organizations the
      key workflow to gate

* which workflow gates to activate (if any): workflow gates are the primary
  feature impacting the user experience, so select gates that ensure your
  priorities

  * deploying a model

    * the most common use case for deploy as a workflow gate is a published
      solution that the owner has updated; even if scan success was required
      prior to publication, later updates could result in failure, and thus
      use of the solution by others could be blocked in that case, until the
      issue is corrected
    * the primary risk managed by this gate is the deployment of a solution
      with a license that is not allowed by the operator

  * downloading model artifacts or documents

    * similar to deploy as a workflow gate; since downloading a solution may
      often be a precursor to further distribution or re-uploading as a new
      solution, it may be a priority of the operator to prevent the possibility
      of those actions when the license scan was not successful

  * share

    * this workflow would apply as a gate to prevent the re-sharing of solutions;
      the solution owner can always share a solution, but those it was shared
      with may be restricted from re-sharing the solution, unless a scan was
      successful

  * publishing a model to a marketplace (public, or restricted)

    * publishing to a public marketplace may be for many organizations the
      key workflow to gate
    * use cases for avoiding gates for publishing to a restricted catalog include
      for teams within an organization that want to locally publish a solution
      in development

++++++++++++++++++++++++++++++++++++++++++++
Configuring the Site Config Verification Key
++++++++++++++++++++++++++++++++++++++++++++

"Verification" is the name of the key (configured parameter) of the site config
table in the Acumos Common Data Service ("CDS"). It contains a JSON structure
that is used by the SV feature to control the scan triggers and workflow
gates, as above. Use these steps to customize the verification site config for your
platform:

Before your Acumos platform is deployed, or after, update the default
verification site config key:

* the default (demo) verification site config is shown below, and available in the
  `system-integration repository <https://github.com/acumos/system-integration>`_
  folder AIO/kubernetes/deployment/configmap/sv-scanning/scripts/ as
  `siteconfig-verification.json <https://github.com/acumos/system-integration/AIO/kubernetes/deployment/configmap/sv-scanning/scripts/siteconfig-verification.json>`_)

.. code-block:: text

  {
    "externalScan":"false",
    "allowedLicense":[
      {
        "type":"SPDX",
        "name":"Apache-2.0"
      },
      {
        "type":"SPDX",
        "name":"CC-BY-4.0"
      },
      {
        "type":"SPDX",
        "name":"BSD-3-Clause"
      },
      {
        "type":"Vendor-A",
        "name":"Vendor-A-OSS"
      },
      {
        "type":"Company-B",
        "name":"Company-B-Proprietary"
      }
    ],
    "compatibleLicenses":[
      { "name":"Apache-2.0", "compatible":[
          { "name":"CC-BY-4.0" },
          { "name":"Apache-2.0" },
          { "name":"BSD-3-Clause" },
          { "name":"MIT-License" }
        ]
      },
      { "name":"BSD-3-Clause", "compatible":[
          { "name":"CC-BY-4.0" },
          { "name":"Apache-2.0" },
          { "name":"BSD-3-Clause" },
          { "name":"MIT-License" }
        ]
      },
      { "name":"MIT-License", "compatible":[
          { "name":"CC-BY-4.0" },
          { "name":"Apache-2.0" },
          { "name":"BSD-3-Clause" },
          { "name":"MIT-License" }
        ]
      },
      { "name":"Vendor-A-OSS", "compatible":[
          { "name":"Vendor-A-OSS" },
          { "name":"CC-BY-4.0" },
          { "name":"Apache-2.0" },
          { "name":"BSD-3-Clause" },
          { "name":"MIT-License" }
        ]
      },
      { "name":"Company-B-Proprietary", "compatible":[
          { "name":"Company-B-Proprietary" },
          { "name":"CC-BY-4.0" },
          { "name":"Apache-2.0" },
          { "name":"BSD-3-Clause" },
          { "name":"MIT-License" }
        ]
      }
    ],
    "licenseScan":{
      "created":"true",
      "updated":"true",
      "deploy":"true",
      "download":"true",
      "share":"true",
      "publishCompany":"true",
      "publishPublic":"true"
    },
    "securityScan":{
      "created":"true",
      "updated":"true",
      "deploy":"false",
      "download":"false",
      "share":"false",
      "publishCompany":"false",
      "publishPublic":"false"
    },
    "licenseVerify":{
      "deploy":"true",
      "download":"true",
      "share":"true",
      "publishCompany":"true",
      "publishPublic":"true"
    },
    "securityVerify":{
      "deploy":"true",
      "download":"true",
      "share":"false",
      "publishCompany":"true",
      "publishPublic":"true"
    }
  }
..

* NOTE: the "securityScan" and "securityVerify" sections are reserved for future
  use
* If you are using the
  `AIO toolset <https://github.com/acumos/system-integration/tree/master/AIO>`_,
  update siteconfig-verification.json in the folder referenced above
* Deploy or redeploy the SV Scanning service using the tools for your platform,
  e.g. using the "redeploy_component.sh" script in the system-integration repo

You can also update the SV site config key though the CDS Swagger UI.
Note that if you use the CDS Swagger UI, you will need to escape all quotes in
the JSON structure, as shown when you retrieve the current value. See the CDS
user guide for information.

Future releases will include an Acumos platform admin UI screen that allows you
to directly update the SV site config key.

++++++++++++++++++++++++++++++++
Configuring the Scancode Toolkit
++++++++++++++++++++++++++++++++

Two folders in the
`system-integration repository <https://github.com/acumos/system-integration>`_
folder AIO/kubernetes/deployment/configmap/sv-scanning contain examples of how
you can configure the Scancode Toolkit to recognize and categorize additional
license types, e.g. proprietary licenses.

To make changes in these folders, follow the guide below, and then
deploy/redeploy the SV Scanning Service as described in
`Configuring the Site Config Verification Key`_.

The "licenses" and "rules" folders under
AIO/kubernetes/deployment/configmap/sv-scanning can contain extra license and
license-detection rule files that the admin can configure for use with the
SV Scanning Service.

NOTE:

* the description below is based upon initial testing with extending
  the Scancode Tookit configuration, and will be updated as more experience
  allows. For more information, see
  `How to add a new license detection rule? <https://github.com/nexB/scancode-toolkit/wiki/FAQ>`_
  on the `Scancode-toolkit github repo <https://github.com/nexB/scancode-toolkit>`_.
* the files contained in the system-integration repo folders under "licenses"
  and "rules" are examples, for demonstration and test purposes only

---------------
Licenses Folder
---------------

This folder should contain two files for each license to be added. 'selected_base_name'
is a unique name that you can use to differentiate the licenses in this folder.
Ensure that the selected name does not conflict with one of the names in the
`scancode licenses folder <https://github.com/nexB/scancode-toolkit/tree/develop/src/licensedcode/data/licenses>`_ .

* 'selected_base_name'.yml

  * This contains attributes of the license that are needed for the reporting
    functions of the scancode-toolkit. The minimum fields are:

    * key: identifier to be used in the Acumos siteConfig verification key
    * name: full name of the license
    * short_name: short name of the license. This should be aligned with the
      license name as configured in the siteConfig verification key, as
      scancode will report the license name equivalent to this field, with spaces
      replaced by dashes.
    * category: one of

      * Commercial
      * Copyleft
      * Copyleft Limited
      * Free Restricted
      * Patent License
      * Permissive
      * Proprietary Free
      * Public Domain
      * Unstated License

* 'selected_base_name'.LICENSE

  * Unique text from typical text expression of the license. Leave out any
    common phrases that might trigger false detection of other licenses.

------------
Rules Folder
------------

This folder should contain two files for each variant of a rule to be used to
detect licenses. 'selected_base_name' is a unique name that you can use to
differentiate the licenses in this folder. 'variant' is a number from 1 to n.
Ensure that the selected name does not conflict with one of the names in the
`scancode rules folder <https://github.com/nexB/scancode-toolkit/tree/develop/src/licensedcode/data/rules>`_ .

* 'selected_base_name'_'variant'.RULE

  * typically, this should be a text snippet that can uniquely identify the
    license. Scancode supports a variety of rule features that can be used here,
    in addition to plain text.

* 'selected_base_name'_'variant'.yml

  * license_expression: value used as the 'key' in licenses/'selected_base_name'.yml
  * is_license_reference: 'yes', if this is a plain text rule
