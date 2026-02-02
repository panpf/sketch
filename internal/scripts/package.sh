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

echo "Android distribution is written to $(pwd)/samples/androidApp/build/outputs/apk/release/"
echo "Android distribution is written to $(pwd)/samples/shared/build/ios/outputs/Build/Products/Debug-iphonesimulator/"
echo "Desktop distribution is written to $(pwd)/samples/shared/build/compose/binaries/main/"
echo "JS distribution is written to $(pwd)/samples/shared/build/dist/js/productionExecutable/"
echo "WasmJs distribution is written to $(pwd)/samples/shared/build/dist/wasmJs/productionExecutable/"
echo "✅  All packages are created successfully."