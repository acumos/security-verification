.. ===============LICENSE_START=======================================================
.. Acumos CC-BY-4.0
.. ===================================================================================
.. Copyright (C) 2019 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
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

=========================================================
Developer Guide for the Security Verification (SV) Client
=========================================================

This library provides a client for using the Security Verification Service in the Acumos machine-learning platform.
It depends on many Spring libraries. This document offers guidance for both client developers and client 
users (developers who want to use the client in their Java projects).

Maven Dependency
----------------

The client jar is deployed to these Nexus repositories at the Linux Foundation:: 

	<repository>
		<id>snapshots</id>
		<url>https://nexus.acumos.org/content/repositories/snapshots</url> 
	</repository>
	<repository>
		<id>releases</id>
		<url>https://nexus.acumos.org/content/repositories/releases</url> 
	</repository>

Use this dependency information, ideally with the latest version number shown in the release notes::

	<dependency> 
		 <groupId>org.acumos.security-verification</groupId>
		 <artifactId>security-verification-client</artifactId>
		 <version>x.x.x</version>
	</dependency>

Building and Packaging
----------------------

As of this writing the build (continuous integration) process is fully automated in the Linux Foundation system
using Gerrit and Jenkins.  This section describes how to perform local builds for development and testing.

Prerequisites
~~~~~~~~~~~~~

The build and test machine needs the following:

1. Java version 1.8
2. Maven version 3
3. Connectivity to Maven Central to download required jars

Use maven to build and package the client jar using this command::

    mvn package
