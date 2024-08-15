#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e

./gradlew clean :sketch-compose:assembleRelease -PcomposeCompilerReports=true