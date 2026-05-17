#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e

cd ../../

./gradlew clean

./gradlew samples:androidApp:assembleRelease
xcodebuild -project samples/iosApp/iosApp.xcodeproj -scheme iosApp -destination 'generic/platform=iOS Simulator' ARCHS=arm64 CODE_SIGN_IDENTITY="" CODE_SIGNING_REQUIRED=NO CODE_SIGNING_ALLOWED=NO -derivedDataPath samples/shared/build/ios/outputs/
./gradlew samples:desktopApp:packageReleaseDistributionForCurrentOS
./gradlew samples:jsApp:jsBrowserDistribution
./gradlew samples:wasmJsApp:wasmJsBrowserDistribution

echo ""
echo "Android distribution is written to $(pwd)/samples/androidApp/build/outputs/apk/release/"
echo "iOS distribution is written to $(pwd)/samples/shared/build/ios/outputs/Build/Products/Debug-iphonesimulator/"
echo "Desktop distribution is written to $(pwd)/samples/desktopApp/build/compose/binaries/main/"
echo "JS distribution is written to $(pwd)/samples/jsApp/build/dist/js/productionExecutable/"
echo "WasmJs distribution is written to $(pwd)/samples/wasmJsApp/build/dist/wasmJs/productionExecutable/"
echo "✅  All packages are created successfully."