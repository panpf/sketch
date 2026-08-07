#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e

rootDir="${PWD%/internal*}"
cd "$rootDir"

./gradlew clean
./gradlew samples:macosApp:packageDmgNativeReleaseMacosArm64

echo "✅  macOS Native package is created successfully. The distribution is written to $(pwd)/samples/macosApp/build/compose/binaries/main/native-macosArm64-release-dmg/"
