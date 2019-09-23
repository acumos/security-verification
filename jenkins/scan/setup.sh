#!/bin/bash
# ===============LICENSE_START=======================================================
# Acumos Apache-2.0
# ===================================================================================
# Copyright (C) 2019 AT&T Intellectual Property. All rights reserved.
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
#
# What this is: script to copy any deployment-specific customizations to the
# security-verification scanning service configuration files
#

function log() {
  set +x
  fname=$(caller 0 | awk '{print $2}')
  fline=$(caller 0 | awk '{print $1}')
  if [[ ! -d /maven/logs/security-verification/security-verification-server ]]; then
    mkdir -p /maven/logs/security-verification/security-verification-server
  fi
  echo; echo "$(date +%Y-%m-%d:%H:%M:%SZ), setup.sh($fname:$fline), $1" >>/maven/logs/security-verification/security-verification-server/security-verification-server.log
  set -x
}

set -x
cd /maven/scan
if [[ -e /maven/conf ]]; then
  licenses=$(ls /maven/conf/licenses/*)
  log "Copying from /maven/conf/licenses to scancode license folder: $licenses"
  cp /maven/conf/licenses/* scancode-toolkit-3.0.2/src/licensedcode/data/licenses/.
  rules=$(ls /maven/conf/rules/*)
  log "Copying from /maven/conf/rules to scancode rules folder: $rules"
  cp /maven/conf/rules/* scancode-toolkit-3.0.2/src/licensedcode/data/rules/.
  scripts=$(ls /maven/conf/scripts/*)
  log "Copying from /maven/conf/scripts to /maven/scan/: $scripts"
  cp /maven/conf/scripts/* .
fi
if [[ "$licenses" == "" ]]; then
  licenses=$(ls licenses/*)
  log "Copying default licenses to scancode license folder: $licenses"
  cp licenses/* scancode-toolkit-3.0.2/src/licensedcode/data/licenses/.
fi
if [[ "$rules" == "" ]]; then
  rules=$(ls rules/*)
  log "Copying from default rules to scancode rules folder: $rules"
  cp rules/* scancode-toolkit-3.0.2/src/licensedcode/data/rules/.
fi
# Any additional/updated licenses and rules must be copied prior
log "Initializing scancode toolkit"
scancode-toolkit-3.0.2/scancode --license start.sh --json=/tmp/scancode.json
bash start.sh
log "SV Scanning service has exited"
