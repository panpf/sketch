#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e

rootDir="${PWD%/internal/*}"
cd $rootDir

./gradlew clean
./gradlew samples:desktopApp:packageReleaseDistributionForCurrentOS

echo "✅  Desktop package is created successfully. The distribution is written to $(pwd)/samples/desktopApp/build/compose/binaries/main-release/"