#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e

rootDir="${PWD%/internal*}"
cd "$rootDir"

./gradlew clean
./gradlew samples:jvmApp:packageReleaseDistributionForCurrentOS

echo "✅  JVM package is created successfully. The distribution is written to $(pwd)/samples/jvmApp/build/compose/binaries/main-release/"