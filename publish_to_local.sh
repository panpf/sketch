#!/bin/bash

# Print error information to facilitate troubleshooting
set -e

# Build and install the artifacts locally to 'mavenLocal'.
./gradlew publishToMavenLocal --no-configuration-cache