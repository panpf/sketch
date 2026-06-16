#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e

cd ../../

./gradlew connectedAndroidTest
./gradlew iosSimulatorArm64Test
./gradlew desktopTest
./gradlew jsBrowserTest
./gradlew wasmJsBrowserTest

echo "✅  All tests are passed successfully."