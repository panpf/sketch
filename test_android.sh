#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e

./gradlew connectedAndroidTest

echo "✅  Android tests are passed successfully."