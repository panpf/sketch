#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e

./gradlew clean

./package_android.sh --skipClean
./package_ios.sh --skipClean
./package_desktop.sh --skipClean
./package_js.sh --skipClean
./package_wasmJs.sh --skipClean

echo "Android Output: sample/build/outputs/apk/release/"
echo "Desktop Output: sample/build/compose/binaries/main/"
echo "JS Output: sample/build/dist/js/productionExecutable/"
echo "WasmJs Output: sample/build/dist/wasmJs/productionExecutable/"
echo "✅  All packages are created successfully."