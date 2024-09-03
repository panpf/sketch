#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e

./gradlew clean

./package_android.sh --skipClean
./package_ios.sh --skipClean
./package_desktop.sh --skipClean
./package_js.sh --skipClean
./package_wasmJs.sh --skipClean

echo "✅  All packages are created successfully."