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
# What this is: script to setup the "verifiication" site-config key for the
# security-verification service
#
# Prerequisites:
# - Acumos platform installed
# - jq installed on the host where this script is being run
#
# Usage:
# $ bash setup_verification_site_config.sh <cds_base> <cds_creds> <admin> [json]
#   cds_base: CDS base url, e.g. http://localhost:8000
#   cds_creds: CDS credentials, e.g. username:password
#   admin: username of Acumos Admin (user with role "Admin")
#   json: (optional) verification JSON file to use (default is included here)

function fail() {
  log "$1"
  exit 1
}

function log() {
  set +x
  fname=$(caller 0 | awk '{print $2}')
  fline=$(caller 0 | awk '{print $1}')
  echo; echo "$fname:$fline ($(date)) $1"
  set -x
}

function find_admin() {
  trap 'fail' ERR
  log "Finding user name $admin"
  local jsonout="/tmp/$(date +%H%M%S%N)"
  curl -s -o $jsonout -u $cds_creds -H 'Accept: application/json' $cds_base/user
  cat $jsonout
  users=$(jq -r '.content | length' $jsonout)
  i=0; userId=""
  while [[ $i -lt $users && "$userId" == "" ]] ; do
    loginName=$(jq -r ".content[$i].loginName" $jsonout)
    if [[ "$loginName" == "$admin" ]]; then
      userId=$(jq -r ".content[$i].userId" $jsonout)
    fi
    ((++i))
  done
  rm $jsonout
  if [[ "$userId" == "" ]]; then
    fail "User $admin not found at Acumos platform $ACUMOS_DOMAIN"
  fi
}

function setup_verification_site_config() {
  local jsonout="/tmp/$(date +%H%M%S%N)"
  local jsoninp="/tmp/$(date +%H%M%S%N)"
  if [[ $(curl -I -u $cds_creds $cds_base/site/config/verification | grep -ci 'content-length: 0') -eq 1 ]]; then
    cds_api="-X POST $cds_base/site/config"
  else
    cds_api="-X PUT $cds_base/site/config/verification"
  fi
  sed -i -- 's/"/\\"/g' $json
  cat <<EOF >$jsoninp
{
  "configKey": "verification",
  "configValue": "$(cat $json)",
  "userId": "$userId"
}
EOF

  log "Sending request to CDS API $cds_api"
  cat $jsoninp

  curl -s -o $jsonout -u $cds_creds \
    -H 'Accept: application/json' \
    -H "Content-Type: application/json" \
    $cds_api -d @$jsoninp

  cat $jsonout
  rm $jsonout
  rm $jsoninp
}


if [[ $# -lt 3 ]]; then
  echo <<'EOF'
Usage:
  $ bash setup_verification_site_config.sh <cds_base> <cds_creds> <admin> [json]
    cds_base: CDS base url, e.g. http://localhost:8000
    cds_creds: CDS credentials, e.g. username:password
    admin: username of Acumos Admin (user with role "Admin")
    json: (optional) verification JSON file to use (default is included here)
EOF
  echo "All parameters not provided"
  exit 1
fi

set -x
export WORK_DIR=$(pwd)
cds_base=$1/ccds
cds_creds=$2
admin=$3
json=$4
log "Create or update site-config key 'verificaton'"
if [[ "$json" != "" ]]; then
  log "Using provided JSON file $json"
  cat $json
else
  json="/tmp/$(date +%H%M%S%N)"
  log "Using script-default JSON"
  cat <<EOF >$json
{
 "verification" : {
 "externalScan":"false",
 "allowedLicense": [
 { "type":"SPDX", "value":"Apache-2.0" },
 { "type":"SPDX", "value":"CC-BY-4.0" },
 { "type":"SPDX", "value":"BSD-3-Clause" },
 { "type":"VendorA", "value":"VendorA-OSS" },
 { "type":"CompanyB", "value":"CompanyB-Proprietary" }
 ],
 "licenseScan": {
 "created":"true",
 "updated":"true",
 "deploy":"false",
 "download":"false",
 "share":"false",
 "publishCompany":"false",
 "publishPublic":"false"
 },
 "securityScan": {
 "created":"true",
 "updated":"true",
 "deploy":"false",
 "download":"false",
 "share":"false",
 "publishCompany":"false",
 "publishPublic":"false"
 },
 "licenseVerify": {
 "deploy":"true",
 "download":"true",
 "share":"false",
 "publishCompany":"true",
 "publishPublic":"true"
 },
 "securityVerify": {
 "deploy":"true",
 "download":"true",
 "share":"false",
 "publishCompany":"true",
 "publishPublic":"true"
 }
 }
}
EOF
fi
if [[ ! $(jq . $json) ]]; then
  fail "JSON did not parse successfully"
fi
find_admin
setup_verification_site_config $json
