#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e

rootDir="${PWD%/internal/*}"
cd $rootDir

./gradlew clean :sketch-compose:assembleRelease -PcomposeCompilerReports=true