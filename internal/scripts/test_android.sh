#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e

cd ../../

./gradlew connectedAndroidTest

echo "✅  Android tests are passed successfully."