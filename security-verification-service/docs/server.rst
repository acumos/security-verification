.. ===============LICENSE_START=======================================================
.. Acumos CC-BY-4.0
.. ===================================================================================
.. Copyright (C) 2017 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
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

=====================================================
Developer Guide for the Security Verification Service
=====================================================

This microservice provides security verification services to components in the
Acumos machine-learning platform. It is built using the Spring-Boot platform.
This document primarily offers guidance for server developers.

Supported Methods and Objects
-----------------------------

The microservice endpoints and objects are documented using Swagger. A running
server documents itself at a URL like the following, but consult the server's
configuration for the exact port number (e.g., "9183") and context root
(e.g., "scan") to use::

    http://localhost:9183/scan/swagger-ui.html

Building and Packaging
----------------------

As of this writing the build (continuous integration) process is fully automated
in the Linux Foundation system using Gerrit and Jenkins. This section describes
how to perform local builds for development and testing.

Prerequisites
~~~~~~~~~~~~~

The build machine needs the following:

1. Java version 1.8
2. Maven version 3
3. Connectivity to Maven Central to download required jars

Use maven to build and package the service into a single "fat" jar using this
command::

    mvn clean install

Development and Local Testing
-----------------------------

This section provides information for running the server in a
production/development environment, assuming that the application is packaged
into a docker container for deployment.

Prerequisites
~~~~~~~~~~~~~

    1. Java version 1.8 in the runtime environment; i.e., installed in the
       docker container
    2. The username/password combination to access the database
    3. The Nexus username/password combination to access.

Configuring the system
~~~~~~~~~~~~~~~~~~~~~~

At runtime in production deployments, in addition to using a configuration file,
environment-specific configuration properties should be supplied using a block of
JSON in an environment variable called SPRING\_APPLICATION\_JSON. This can easily
be done using the deployment templates. The default SV Scanning templates
for use with docker-compose or kubernetes are provided by the AIO (All-In-One)
Acumos deployment toolset in the system-integration repository.

.. code:: bash

   # Get the system-integration repository
   git clone --depth 1 "https://gerrit.acumos.org/r/system-integration"
   # Select the Boreas branch
   cd system-integration
   git checkout boreas
   # See what environment configuration options are supported
   cat AIO/acumos_env.sh
   # See the docker-compose deployment template with references to options
   cat AIO/docker/acumos/sv-scanning.yml
   # See the kubernetes deployment template with references to options
   cat AIO/kubernetes/deployment/sv-scanning-deployment.yaml
   # Edit the options in acumos_env.sh and/or in the template directly

Launch Instructions
~~~~~~~~~~~~~~~~~~~

To run the SV Scanning in a local docker environment:

1. Build an image locally or use an image in the Acumos Nexus repositories.

2. Update environment variables as referenced by the template, either
   directly or in acumos_env.sh:

   * ACUMOS_CDS_HOST: hostname or IP address of the CDS service
   * ACUMOS_CDS_PASSWORD: CDS user password
   * ACUMOS_CDS_PORT: CDS service port
   * ACUMOS_CDS_USER: CDS service user
   * ACUMOS_LOG_LEVEL: log level to use
   * ACUMOS_NAMESPACE: kubernetes namespace and logs parent folder under '/mnt'
   * ACUMOS_NEXUS_API_PORT: Nexus API port
   * ACUMOS_NEXUS_GROUP: Nexus artifact group ID
   * ACUMOS_NEXUS_HOST: Nexus hostname or IP address
   * ACUMOS_NEXUS_MAVEN_REPO: Nexus Maven repo name
   * ACUMOS_NEXUS_MAVEN_REPO_PATH: path prefix for Nexus Maven repo
   * ACUMOS_NEXUS_RW_USER: Nexus user with R/W permission
   * ACUMOS_NEXUS_RW_USER_PASSWORD: Nexus R/W user password
   * ACUMOS_SECURITY_VERIFICATION_PORT: port on which to expose the SV service
   * SECURITY_VERIFICATION_IMAGE: image version to use

2. Use the docker-compose process that applies to your environment, e.g.

   * for a standalone docker container::

    docker-compose -f sv-scanning up -d
