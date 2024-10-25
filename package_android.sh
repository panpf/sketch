#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e

if [ "$1" != "--skipClean" ]; then
  ./gradlew clean
fi

./gradlew sample:assembleRelease

echo "✅  Android package is created successfully. The distribution is written to $(pwd)/sample/build/outputs/apk/release/"