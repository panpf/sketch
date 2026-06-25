#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e

rootDir="${PWD%/internal/*}"
cd $rootDir

./gradlew clean
./gradlew samples:androidApp:assembleRelease

echo "✅  Android package is created successfully. The distribution is written to $(pwd)/samples/androidApp/build/outputs/apk/release/"