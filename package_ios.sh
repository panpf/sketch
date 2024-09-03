#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e

if [ "$1" != "--skipClean" ]; then
  ./gradlew clean
fi

#xcodebuild -project sample/iosApp/iosApp.xcodeproj -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 14,OS=latest'

#echo "✅  iOS package is created successfully."