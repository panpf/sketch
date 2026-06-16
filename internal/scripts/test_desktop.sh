#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e

cd ../../

./gradlew desktopTest

echo "✅  Desktop tests are passed successfully."