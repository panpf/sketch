#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e

rootDir="${PWD%/internal*}"
cd "$rootDir"

./gradlew macosArm64Test --continue

echo "✅  macOS Native tests are passed successfully."
