#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e

cd ../../

./gradlew connectedAndroidTest iosSimulatorArm64Test desktopTest jsBrowserTest wasmJsBrowserTest --continue

echo "✅  All tests are passed successfully."