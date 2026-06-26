#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e

rootDir="${PWD%/internal*}"
cd "$rootDir"

./gradlew clean
xcodebuild -project samples/iosApp/iosApp.xcodeproj -scheme iosApp -destination 'generic/platform=iOS Simulator' ARCHS=arm64 CODE_SIGN_IDENTITY="" CODE_SIGNING_REQUIRED=NO CODE_SIGNING_ALLOWED=NO -derivedDataPath samples/shared/build/ios/outputs/

echo "✅  iOS package is created successfully. $(pwd)/samples/shared/build/ios/outputs/Build/Products/Debug-iphonesimulator/"