.. ===============LICENSE_START================================================
.. Acumos CC-BY-4.0
.. ============================================================================
.. Copyright (C) 2019 Nordix Foundation
.. ============================================================================
.. This Acumos documentation file is distributed by Nordix Foundation.
.. under the Creative Commons Attribution 4.0 International License
.. (the "License");
.. you may not use this file except in compliance with the License.
.. You may obtain a copy of the License at
..
..      http://creativecommons.org/licenses/by/4.0
..
.. This file is distributed on an "AS IS" BASIS,
.. WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
.. See the License for the specific language governing permissions and
.. limitations under the License.
.. ===============LICENSE_END==================================================
..

==================================
Application Programming Interfaces
==================================

This document defines the APIs that are being produced
or consumed by the license management sub-component in Acumos.


LicenseCreator.createRTU
========================

User specific RTU with generated RTU id in library and
generated RTU reference in library


Examples:
---------

Simple example:

.. code-block:: java

    // where client is instance of ICommonDataServiceRestClient
    ILicenseCreator licenseCreator = new LicenseCreator(client);
    CreateRTURequest createRTURequest = new CreateRTURequest(solutionId, userId);
    ICreatedRtu createdRtuInfo = licenseCreator.createRTU(createRTURequest);

Site specific RTU as well as all options provided:

.. code-block:: java

    CreateRTURequest createSiteWideRTURequest = new CreateRTURequest();

    // solutionId, userId
    createSiteWideRTURequest.setSiteWide(true);
    createSiteWideRTURequest.setSolutionId(solutionId);
    createSiteWideRTURequest.addUserId(userId);
    createSiteWideRTURequest.setRtuId(new Random().nextLong());
    createSiteWideRTURequest.setRtuRefs(new String[] { UUID.randomUUID().toString() });
    ICreatedRtu createdRtuInfo = licenseCreator.createRtu(createSiteWideRTURequest);

More info see `LicenseCreator javadocs <https://javadocs.acumos.org/org.acumos.security-verification/master/org/acumos/licensemanager/client/LicenseCreator.html>`_

LicenseCreator is using `CDS RTU APIs <https://docs.acumos.org/en/latest/submodules/common-dataservice/docs/server-api.html#right-to-use-controller>`_


LicenseVerifier.verifyRTU
=========================

Verification of a rtu for a solutionId and userId provided by CDS.

.. code-block:: java

    // where client is instance of ICommonDataServiceRestClient
    ILicenseVerifier licenseVerifier = new LicenseVerifier(client);
    VerifyLicenseRequest licenseDownloadRequest = new VerifyLicenseRequest(LicenseAction.deploy, "solutionid", "userid");
    licenseDownloadRequest.addAction(LicenseAction.download);
    ILicenseVerification verifyUserRTU = licenseVerifier.verifyRtu(licenseDownloadRequest);
    verifyUserRTU.isAllowed(LicenseAction.download) // returns true / false if rtu exists
    verifyUserRTU.isAllowed(LicenseAction.deploy) // returns true / false if rtu exists

Learn more in `LicenseVerifier java docs <https://javadocs.acumos.org/org.acumos.security-verification/master/org/acumos/licensemanager/client/LicenseVerifier.html>`_

License Verification is using `CDS RTU APIs <https://docs.acumos.org/en/latest/submodules/common-dataservice/docs/server-api.html#right-to-use-controller>`_

LicenseJsonValidationResults.validateLicenseJson
================================================

Example api call:

.. code-block:: java

    LicenseJsonValidator validator = new LicenseJsonValidator();
    LicenseJsonValidationResults results = validator.validateLicenseJson(goodJson);
    boolean isValid = results.getJsonSchemaErrors().isEmpty();

Learn more in `LicenseJsonValidationResults java docs <https://javadocs.acumos.org/org.acumos.security-verification/master/org/acumos/licensemanager/jsonvalidator/LicenseJsonValidator.html>`_

`Json Schema <https://raw.githubusercontent.com/acumos/security-verification/master/license-manager-client-library/src/main/resources/license.schema.json>`_


