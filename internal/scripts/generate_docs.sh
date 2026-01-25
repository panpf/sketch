#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e

cd ../../

./gradlew dokkaGenerate

echo "✅  Dokka docs generate successfully. The distribution is written to $(pwd)/build/html/"