#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e

cd ../../

# Build and install the artifacts locally to 'mavenLocal'.
./gradlew publishToMavenLocal --no-configuration-cache --exclude-task=dokkaHtml