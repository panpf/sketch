#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e

cd ../../

./gradlew wasmJsBrowserTest --continue

echo "✅  WasmJs tests are passed successfully."