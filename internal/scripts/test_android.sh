#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e

if [ "$1" != "--skipFindRootDir" ]; then
  cd ../../
fi

./gradlew connectedAndroidTest

echo "✅  Android tests are passed successfully."