#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e

if [ "$1" != "--skipClean" ]; then
  ./gradlew clean
fi

./gradlew sample:jsBrowserDistribution

echo "✅  JS package is created successfully. Output: sample/build/dist/js/productionExecutable/"