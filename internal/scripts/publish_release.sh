#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e

cd ../../

# Build and upload the artifacts to 'mavenCentral'.
./gradlew clean publishAndReleaseToMavenCentral --no-configuration-cache