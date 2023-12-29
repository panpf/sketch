#!/bin/bash

# Build and upload the artifacts to 'mavenCentral'.
./gradlew clean publish
if [[ $? -eq 0 ]]; then
  echo "publish success"
else
  echo "publish failed"
  exit 1
fi