#!/bin/bash

# Build and upload the artifacts to 'mavenCentral'.
./gradlew clean assembleRelease publish
if [[ $? -eq 0 ]]; then
  echo "publish success"
else
  echo "publish failed"
  exit 1
fi

# Close and release the repository.
if [ "$1" = "release" ]; then
  ./gradlew closeAndReleaseRepository
  if [[ $? -eq 0 ]]; then
    echo "closeAndReleaseRepository success"
  else
    echo "closeAndReleaseRepository failed"
    exit 1
  fi
else
    echo "Skip execute 'closeAndReleaseRepository', add parameter 'release' if needed"
fi