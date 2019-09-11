# ===============LICENSE_START=======================================================
# Acumos Apache-2.0
# ===================================================================================
# Copyright (C) 2019 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
# ===================================================================================
# This Acumos software file is distributed by AT&T and Tech Mahindra
# under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# This file is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
# ===============LICENSE_END=========================================================
FROM adoptopenjdk/openjdk11-openj9:jre
RUN apt-get update && apt-get install -y wget curl git jq zip python-dev xz-utils zlib1g libxml2-dev libxslt1-dev bzip2
COPY maven/ /maven/
RUN chmod -R 777 /maven
RUN chmod -R 777 /maven/*.jar
ENV JAVA_OPTS="-Xms128m -Xmx512m"
ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /maven/security-verification-service-*.jar
RUN cd /maven/scan;wget https://github.com/nexB/scancode-toolkit/releases/download/v3.0.2/scancode-toolkit-3.0.2.zip;
RUN cd /maven/scan;unzip scancode-toolkit-3.0.2.zip