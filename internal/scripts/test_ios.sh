#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e

rootDir="${PWD%/internal/*}"
cd $rootDir

./gradlew iosSimulatorArm64Test --continue

echo "✅  iOS tests are passed successfully."