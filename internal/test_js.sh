#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e

rootDir="${PWD%/internal*}"
cd "$rootDir"

./gradlew jsBrowserTest --continue

echo "✅  JS tests are passed successfully."