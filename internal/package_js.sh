#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e

rootDir="${PWD%/internal/*}"
cd $rootDir

./gradlew clean
./gradlew samples:jsApp:jsBrowserDistribution

echo "✅  JS package is created successfully. The distribution is written to $(pwd)/samples/jsApp/build/dist/js/productionExecutable/"