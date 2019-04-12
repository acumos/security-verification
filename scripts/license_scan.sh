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
# $ bash license_scan.sh <folder>
#   solutionId: ID of Acumos model
#   revisionId: ID of Acumos model revision
#   folder: folder where the model data was dumped via dump_model.sh
#
# per https://github.com/nexB/scancode-toolkit

function initialize() {
  if [[ ! -e scanresult/config ]]; then mkdir -p scanresult/config; fi
  cat <<EOF >scanresult/config/allowed_licenses.json
{ "allowedLicense": [
  { "type":"SPDX", "name":"Apache-2.0" },
  { "type":"SPDX", "name":"CC-BY-4.0" },
  { "type":"SPDX", "name":"BSD-3-Clause" },
  { "type":"SPDX", "name":"MIT-License" },
  { "type":"VendorA", "name":"VendorA-OSS" },
  { "type":"CompanyB", "name":"CompanyB-Proprietary" }
]}
EOF

  cat <<EOF >scanresult/config/compatible_licenses.json
{ "compatibleLicenses": [
  { "name":"Apache-2.0", "compatible": [
      { "name": "CC-BY-4.0" },
      { "name": "Apache-2.0" },
      { "name": "BSD-3-Clause" },
      { "name": "MIT-License" }
    ]
  },
  { "name":"BSD-3-Clause", "compatible": [
      { "name": "CC-BY-4.0" },
      { "name": "Apache-2.0" },
      { "name": "BSD-3-Clause" },
      { "name": "MIT-License" }
    ]
  },
  { "name":"MIT", "compatible": [
      { "name": "CC-BY-4.0" },
      { "name": "Apache-2.0" },
      { "name": "BSD-3-Clause" },
      { "name": "MIT-License" }
    ]
  }
]}
EOF
}

function extract_licenses() {
  local root_license=""
  echo "" >all_licenses
  local files=$(jq '.files | length' $1)
  json='{"root_license":{"name":""},"files":['
  local i=0
  # check each file reference for licenses and build a json object for those that do
  while [[ $i -lt $files ]]; do
    local file_path=$(jq ".files[$i].path" $1 | sed 's/""/"/g')
    local lics=$(jq ".files[$i].licenses | length" $1)
    if [[ $lics -gt 0 ]]; then
      local result="$file_path: "
      echo "" >file_licenses
      local file_licenses=""
      local j=0
      while [[ $j -lt $lics ]]; do
        name=$(jq ".files[$i].licenses[$j].short_name" $1 | sed 's/ /-/g' | tr -d '"')
        # Ignore licenses for bugs
        # https://github.com/nexB/scancode-toolkit/issues/1408
        # https://github.com/nexB/scancode-toolkit/issues/1409
        if [[ "$name" != "NPL-1.1" && "$name" != "NOKOS-License-1.0a" ]]; then
          if [[ $(grep -c $name file_licenses) -eq 0 ]]; then
            file_licenses="$file_licenses,{\"name\":\"$name\"}"
            echo "$name" >file_licenses
            echo "$name " >>all_licenses
            result="$result $name"
            if [[ "$root_license" == "" && $(echo $file_path | cut -d '/' -f 2 | grep -c -i -E '^license.txt') -gt 0 ]]; then
              root_license=$name
              echo "Root license $root_license found"
            fi
          fi
        fi
        ((j++))
      done
      echo $result
      file_licenses=$(echo $file_licenses | sed 's/^,//')
      # add the file entry to the json structure
      json="${json}{\"path\":$file_path,\"licenses\":[$file_licenses]},"
    fi
  ((i++))
  done
  json="$(echo ${json}]} | sed 's/,]}/]}/g')"
  echo $json >$OUT/scanresult.json
  if [[ "$root_license" != "" ]]; then
    sed -i -- "s/\"name\":\"\"/\"name\":\"$root_license\"/" $OUT/scanresult.json
  fi
  # Count number of references to each license
  licenses=$(sort all_licenses | uniq)
  local i=0
  for license in $licenses; do
    count=$(grep -c $license all_licenses)
    echo "$license: $count"
    ((i++))
  done
  local license_count=$i
  echo "license_count: $license_count"
  echo "root license: $root_license"
}

function update_reason() {
  if [[ "$reason" == "" ]]; then
    reason="$1"
  else
    reason="$reason, $1"
  fi
}

function verify_compatibility() {
  root_license=$(jq -r '.root_license.name' $OUT/scanresult.json)
  compatible_licenses=$(jq '.compatibleLicenses | length' scanresult/config/compatible_licenses.json)
  local i=0
  local root_name=$(jq -r ".compatibleLicenses[$i].name" scanresult/config/compatible_licenses.json)
  while [[ "$root_license" != "$root_name" && $i -lt $compatible_licenses ]]; do
    ((i++))
    root_name=$(jq -r ".compatibleLicenses[$i].name" scanresult/config/compatible_licenses.json)
  done
  if [[ $i -le $compatible_licenses ]]; then
    echo "Checking all licenses found for compatibility with Root license $root_name"
    compatibles=$(jq ".compatibleLicenses[$i].compatible | length" scanresult/config/compatible_licenses.json)
    local license
    files=$(jq '.files | length' $OUT/scanresult.json)
    local j=0
    while [[ $j -lt $files ]]; do
      path=$(jq -r ".files[$j].path" $OUT/scanresult.json)
      licenses=$(jq ".files[$j].licenses | length" $OUT/scanresult.json)
      local k=0
      while [[ $k -lt $licenses ]]; do
        name=$(jq -r ".files[$j].licenses[$k].name" $OUT/scanresult.json)
        local l=0
        while [[ "$name" != "$(jq -r ".compatibleLicenses[$i].compatible[$l].name" scanresult/config/compatible_licenses.json)" && $l -lt $compatibles ]]; do
          ((l++))
        done
        if [[ $l -eq $compatibles ]]; then
          verifiedLicense=false
          update_reason "$path license ($name) is incompatible with root license $root_name"
        fi
        ((k++))
      done
      ((j++))
    done
  else
    verifiedLicense=false
    reason="Internal error: $root_name not found in compatible license list"
    echo $reason
  fi
}

function verify_allowed() {
  local file=$1
  local name=$2
  local allowed_licenses=$(jq '.allowedLicense | length' scanresult/config/allowed_licenses.json)
  local i=0
  while [[ $i -lt $allowed_licenses && "$name" != "$(jq ".allowedLicense[$i].name" scanresult/config/allowed_licenses.json)" ]]; do
    ((i++))
  done
  if [[ $i -eq allowed_licenses ]]; then
    verifiedLicense=false
    update_reason "$file license ($name) is not allowed"
  fi
}

function verify_root_license() {
  if [[ "$(jq -r '.root_license.name' $OUT/scanresult.json)" == "" ]]; then
    verifiedLicense=false
    update_reason "no license.txt document found"
    echo "No license.txt found in root folder"
  else
    echo "Verified presence of license.txt in root folder"
  fi
}

function verify_license() {
  extract_licenses $OUT/scancode.json
  verifiedLicense=true
  reason=""
  verify_root_license
  files=$(jq '.files | length' $OUT/scanresult.json)
  local i=0
  while [[ $i -lt $files ]]; do
    local file=$(jq ".files[$i].path" $OUT/scanresult.json)
    licenses=$(jq ".files[$i].licenses | length" $OUT/scanresult.json)
    local j=0
    while [[ $j -lt $licenses ]]; do
      verify_allowed $file $(jq ".files[$i].licenses[$j].name" $OUT/scanresult.json)
      ((j++))
    done
    ((i++))
  done
  if [[ "$(jq -r '.root_license.name' $OUT/scanresult.json)" != "" ]]; then
    verify_compatibility
  fi
  sed -i -- "s/^{/{\"scanTime\":\"$(date +%y%m%d-%H%M%S)\",/" $OUT/scanresult.json
  sed -i -- "s/^{/{\"revisionId\":\"$revisionId\",/" $OUT/scanresult.json
  sed -i -- "s/^{/{\"solutionId\":\"$solutionId\",/" $OUT/scanresult.json
  sed -i -- "s/^{/{\"reason\":\"$reason\",/" $OUT/scanresult.json
  sed -i -- "s/^{/{\"verifiedLicense\":\"$verifiedLicense\",/" $OUT/scanresult.json
  sed -i -- "s/^{/{\"schema\":\"1.0\",/" $OUT/scanresult.json
}

WORK_DIR=$(pwd)

if [[ ! -e scancode-toolkit-3.0.2 ]]; then
  wget https://github.com/nexB/scancode-toolkit/releases/download/v3.0.2/scancode-toolkit-3.0.2.zip
  unzip scancode-toolkit-3.0.2.zip
fi

solutionId=$1
revisionId=$2
folder=$3
OUT=$(date +%H%M%S%N)
mkdir $OUT
scancode-toolkit-3.0.2/scancode --license --copyright \
  --ignore "cds/*" --ignore "scancode.json" --ignore "scanresult.json" \
  --ignore "metadata.json" --ignore "*.h5" \
  --json=$OUT/scancode.json $folder
initialize
verify_license
echo "verifiedLicense: $verifiedLicense"
if [[ "$verifiedLicense" == "false" ]]; then echo "reason: $reason"; fi
cat $OUT/scanresult.json
mv $OUT/* $folder/.
rmdir $OUT
cd $WORK_DIR
