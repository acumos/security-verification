#!/bin/bash
# Copyright 2019 AT&T Intellectual Property, Inc. All rights reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# See the License for the specific language governing permissions and
# limitations under the License.
#
# What this is: Script to scan Acumos model artifacts and documents as dumped
# from an Acumos platform by dump_model.sh
#
# Usage:
# $ bash license_scan.sh <folder> <requestId>
#   folder: folder where the model data was dumped via dump_model.sh
#   requestId: identifier of the scan job, and uniquely-named folder for transient data
#
# per https://github.com/nexB/scancode-toolkit
# Implementation below minimizes use of echo due to potential hangs, per
# https://stackoverflow.com/questions/3425754/calling-a-shell-script-from-java-hangs

function fail() {
  fname=$(caller 0 | awk '{print $2}')
  fline=$(caller 0 | awk '{print $1}')
  if [[ "$1" == "" ]]; then
    logit $fname $fline ERROR "Unknown failure"
  else
    logit $fname $fline ERROR "$1"
  fi
  exit 1
}

function logit() {
  cat <<EOF >>$WORK_DIR/$requestId/license_scan.log
$(date +%Y-%m-%d:%H:%M:%SZ), $3, license_scan.sh($1:$2), requestId($requestId), $4
EOF
}

function log() {
  fname=$(caller 0 | awk '{print $2}')
  fline=$(caller 0 | awk '{print $1}')
  if [[ "$1" == "ERROR" ]]; then logit $fname $fline $1 "$2"
  elif [[ "$1" == "INFO" ]]; then logit $fname $fline $1 "$2"
  elif [[ "$1" == "DEBUG" && "$LOG_LEVEL" == "DEBUG" ]]; then logit $fname $fline $1 "$2"
  fi
}

function get_allowed_licenses() {
  trap 'fail' ERR
  local nal=$(jq '. | length' $folder/cds/allowed_licenses.json)
  local al=0
  allowed_licenses=""
  while [[ $al -lt $nal ]]; do
    local alname=$(jq -r ".[$al].name" $folder/cds/allowed_licenses.json)
    allowed_licenses="$allowed_licenses $alname"
    al=$((al+1))
  done
  log DEBUG "allowed_licenses ($allowed_licenses)"
}

function initialize() {
  trap 'fail' ERR
  log DEBUG "initializing allowed_licenses.json and compatible_licenses.json"
  jq '.allowedLicense' $folder/cds/siteconfig.json >$folder/cds/allowed_licenses.json
  get_allowed_licenses
  jq '.compatibleLicenses' $folder/cds/siteconfig.json >$folder/cds/compatible_licenses.json
}

function get_allowed_license_type() {
  trap 'fail' ERR
  log DEBUG "get_allowed_license_type $1"
  local license_name=$1
  local n=$(jq '. | length' $folder/cds/allowed_licenses.json)
  local i=0
  local allowed_name
  allowed_license_type=""
  while [[ $i -lt $n ]]; do
    allowed_name=$(jq -r ".[$i].name" $folder/cds/allowed_licenses.json)
    if [[ "$allowed_name" == "$license_name" ]]; then
      allowed_license_type=$(jq -r ".[$i].type" $folder/cds/allowed_licenses.json)
    fi
    i=$((i+1))
  done
}

function extract_licenses() {
  trap 'fail' ERR
  log DEBUG "extract_licenses"
  local files=$(jq '.files | length' $1)
  cat <<EOF >$requestId/scanresult.json
{"files":[
EOF
  local i=0
  # check each file reference for licenses and build a json object for those that do
  while [[ $i -lt $files ]]; do
    local file_path=$(jq -r ".files[$i].path" $requestId/scancode.json)
    cat <<EOF >$requestId/file_licenses.json
{"path":"$file_path","licenses":[
EOF
    local lics=$(jq ".files[$i].licenses | length" $1)
    if [[ $lics -gt 0 ]]; then
      local j=0
      while [[ $j -lt $lics ]]; do
        name=$(jq -r ".files[$i].licenses[$j].short_name" $1 | sed 's/ /-/g')
        # Ignore licenses for bugs
        # https://github.com/nexB/scancode-toolkit/issues/1408
        # https://github.com/nexB/scancode-toolkit/issues/1409
        if [[ "$name" != "NPL-1.1" && "$name" != "NOKOS-License-1.0a" ]]; then
          if [[ $(grep -c $name $requestId/file_licenses.json) -eq 0 ]]; then
            cat <<EOF >>$requestId/file_licenses.json
,{"name":"$name"}
EOF
            if [[ "$root_license" == "" && "$file_path" == "license.json" ]]; then
              root_license=$name
              get_compatible_licenses
              get_allowed_license_type $name
              log DEBUG "Root license $root_license found"
              if [[ "$allowed_license_type" != "" ]]; then
                root_license_type=$allowed_license_type
                log DEBUG "root license is of allowed type $root_license_type"
              else
                log DEBUG "root license type is unknown (not found in allowedLicense table)"
              fi
            fi
          fi
        fi
        j=$((j+1))
      done
      sed -i -- ':a;N;$!ba;s/\n//g' $requestId/file_licenses.json
      sed -i -- 's/\[,/[/' $requestId/file_licenses.json
      cat <<EOF >>$requestId/scanresult.json
$(cat $requestId/file_licenses.json)]},
EOF
      if [[ -e $requestId/file_licenses.json ]]; then rm $requestId/file_licenses.json; fi
    fi
  i=$((i+1))
  done
  sed -i -- ':a;N;$!ba;s/\n//g' $requestId/scanresult.json
  sed -i -- 's/\},$/}]}/' $requestId/scanresult.json
  if [[ "$root_license" != "" ]]; then
    sed -i -- "s~^{~{\"root_license\":{\"type\":\"$root_license_type\",\"name\":\"$root_license\"},~" $requestId/scanresult.json
  else
    sed -i -- "s~^{~{\"root_license\":{\"type\":\"\",\"name\":\"\"},~" $requestId/scanresult.json
  fi
}

function update_reason() {
  trap 'fail' ERR
  # Remove any quotes in reason
  update=$(echo $1 | sed 's/"//g')
  if [[ "$reason" == "" ]]; then
    reason="$update"
  else
    reason="$reason, $update"
  fi
  log DEBUG "license_scan failure reason($reason)"
}

function get_compatible_licenses() {
  trap 'fail' ERR
  local nl=$(jq '. | length' $folder/cds/compatible_licenses.json)
  local l=0
  while [[ "$root_license" != "$(jq -r ".[$l].name" $folder/cds/compatible_licenses.json)" && $l -lt $nl ]]; do
    l=$((l+1))
  done
  if [[ $l -le $nl ]]; then
    local ncl=$(jq ".[$l].compatible | length" $folder/cds/compatible_licenses.json)
    local cl=0
    compatible_licenses=""
    while [[ $cl -lt $ncl ]]; do
      compatible_licenses="$compatible_licenses $(jq -r ".[$l].compatible[$cl].name" $folder/cds/compatible_licenses.json)"
      cl=$((cl+1))
    done
    log DEBUG "Built root_license $root_license compatible set: $compatible_licenses"
  else
    verifiedLicense=false
    reason="Internal error: $root_license not found in compatible license list"
    fail "$reason"
  fi
}

function verify_compatibility() {
  trap 'fail' ERR
  local license=$1
  log DEBUG "checking $root_license compatibility with $license"
  if [[ "$compatible_licenses" != *"$license"* ]]; then
    log DEBUG "$path license($license) is incompatible with root license $root_license"
    verifiedLicense=false
    update_reason "$path license($license) is incompatible with root license $root_license"
  else
    log DEBUG "$license is compatible with $root_license"
  fi
}

function verify_allowed() {
  trap 'fail' ERR
  local file=$1
  local license=$2
  log DEBUG "verify_allowed($file, $license)"
  if [[ "$allowed_licenses" != *$license* ]]; then
    verifiedLicense=false
    update_reason "$file license($license) is not allowed"
    log DEBUG "$file license($license) is not allowed"
  else
    log DEBUG "$file license($license) is allowed"
    if [[ "$root_license_valid" == "yes" && "$license" != "$root_license" ]]; then
      verify_compatibility $license
    fi
  fi
}

function verify_root_license() {
  trap 'fail' ERR
  log DEBUG "verify_root_license($root_license)"
  if [[ "$root_license" == "" ]]; then
    verifiedLicense=false
    update_reason "no license artifact found, or license is unrecognized"
    root_license_valid="no"
  else
    if [[ "$root_license_type" == "" ]]; then
      root_license_valid="no"
      verifiedLicense=false
      update_reason "root license($root_license) is not allowed"
    else
      root_license_valid="yes"
      log DEBUG "root license($root_license),type($root_license_type) is allowed"
    fi
  fi
}

function verify_license() {
  trap 'fail' ERR
  log DEBUG "verify_license"
  verifiedLicense=true
  reason=""
  root_license=""
  root_license_valid=""
  root_license_type=""
  compatible_licenses=""
  extract_licenses $requestId/scancode.json
  verify_root_license
  local nf=$(jq '.files | length' $requestId/scanresult.json)
  log DEBUG "checking $nf files for allowed/compatible licenses"
  local f=0
  while [[ $f -lt $nf ]]; do
    local file=$(jq -r ".files[$f].path" $requestId/scanresult.json)
    if [[ "$file" != *license.json* ]]; then
      local nl=$(jq ".files[$f].licenses | length" $requestId/scanresult.json)
      local j=0
      while [[ $j -lt $nl ]]; do
        local name=$(jq -r ".files[$f].licenses[$j].name" $requestId/scanresult.json)
        verify_allowed $file $name
        j=$((j+1))
      done
    fi
    f=$((f+1))
  done
  sed -i -- "s~^{~{\"scanTime\":\"$(date +%y%m%d-%H%M%S)\",~" $requestId/scanresult.json
  sed -i -- "s~^{~{\"revisionId\":\"$revisionId\",~" $requestId/scanresult.json
  sed -i -- "s~^{~{\"solutionId\":\"$solutionId\",~" $requestId/scanresult.json
  sed -i -- "s~^{~{\"reason\":\"$reason\",~" $requestId/scanresult.json
  sed -i -- "s~^{~{\"verifiedLicense\":\"$verifiedLicense\",~" $requestId/scanresult.json
  sed -i -- "s~^{~{\"schema\":\"1.0\",~" $requestId/scanresult.json
}

trap 'fail' ERR
cd $(dirname "$0")
WORK_DIR=$(pwd)

if [[ ! -e scancode-toolkit-3.0.2 ]]; then
  wget https://github.com/nexB/scancode-toolkit/releases/download/v3.0.2/scancode-toolkit-3.0.2.zip
  unzip scancode-toolkit-3.0.2.zip
fi

folder=$1
requestId=$2
solutionId=$(jq -r '.solutionId' $folder/cds/revision.json)
revisionId=$(jq -r '.revisionId' $folder/cds/revision.json)
log DEBUG "license_scan.sh solutionId($solutionId) revisionId($revisionId) folder($folder)"
cd $folder
log DEBUG "invoking scancode"
../scancode-toolkit-3.0.2/scancode --license --copyright \
  --ignore "cds" --ignore "scancode.json" \
  --ignore "scanresult.json" --ignore "metadata.json" --ignore "*.h5" \
  --json=$WORK_DIR/$requestId/scancode.json .
cd ..
if [[ ! -e $requestId/scancode.json ]]; then
  log DEBUG "scancode failure"
cat <<EOF >$requestId/scanresult.json
{"files":[]}
EOF
  sed -i -- "s~^{~{\"scanTime\":\"$(date +%y%m%d-%H%M%S)\",~" $requestId/scanresult.json
  sed -i -- "s~^{~{\"revisionId\":\"$revisionId\",~" $requestId/scanresult.json
  sed -i -- "s~^{~{\"solutionId\":\"$solutionId\",~" $requestId/scanresult.json
  sed -i -- "s~^{~{\"reason\":\"unknown failure in scancode utility\",~" $requestId/scanresult.json
  sed -i -- "s~^{~{\"verifiedLicense\":\"false\",~" $requestId/scanresult.json
  sed -i -- "s~^{~{\"schema\":\"1.0\",~" $requestId/scanresult.json
else
  sed -i -- "s~$folder/~~g" $requestId/scancode.json
  initialize
  verify_license
  log DEBUG "result revisionId($revisionId) verifiedLicense($verifiedLicense) reason($reason)"
fi
