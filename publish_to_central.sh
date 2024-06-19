#!/bin/bash

# Print error information to facilitate troubleshooting
set -e

# Build and upload the artifacts to 'mavenCentral'.
./gradlew clean publishToMavenCentral --no-configuration-cache