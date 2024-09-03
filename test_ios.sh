#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e

./gradlew iosSimulatorArm64Test

echo "✅  iOS tests are passed successfully."