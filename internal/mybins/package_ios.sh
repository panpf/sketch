#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e

cd ../../

if [ "$1" != "--skipClean" ]; then
  ./gradlew clean
fi

xcodebuild -project sample/iosApp/iosApp.xcodeproj -scheme iosApp -destination 'generic/platform=iOS Simulator' CODE_SIGN_IDENTITY="" CODE_SIGNING_REQUIRED=NO CODE_SIGNING_ALLOWED=NO -derivedDataPath sample/build/ios/outputs/

echo "✅  iOS package is created successfully. $(pwd)/sample/build/ios/outputs/Build/Products/Debug-iphonesimulator/"