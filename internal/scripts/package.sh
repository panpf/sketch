#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e

cd ../../

./gradlew clean

"$(pwd)/internal/scripts/package_android.sh" --skipFindRootDir --skipClean
"$(pwd)/internal/scripts/package_ios.sh" --skipFindRootDir --skipClean
"$(pwd)/internal/scripts/package_desktop.sh" --skipFindRootDir --skipClean
"$(pwd)/internal/scripts/package_js.sh" --skipFindRootDir --skipClean
"$(pwd)/internal/scripts/package_wasmJs.sh" --skipFindRootDir --skipClean

echo "Android distribution is written to $(pwd)/sample/build/outputs/apk/release/"
echo "Desktop distribution is written to $(pwd)sample/build/compose/binaries/main/"
echo "JS distribution is written to $(pwd)sample/build/dist/js/productionExecutable/"
echo "WasmJs distribution is written to $(pwd)sample/build/dist/wasmJs/productionExecutable/"
echo "✅  All packages are created successfully."