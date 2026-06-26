#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e

rootDir="${PWD%/internal*}"
cd "$rootDir"

./gradlew connectedAndroidTest --continue

echo "✅  Android tests are passed successfully."