#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e

# Build and upload the artifacts to 'mavenCentral'.
./gradlew clean publishToMavenCentral --no-configuration-cache