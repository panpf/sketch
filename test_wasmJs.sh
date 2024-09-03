#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e

./gradlew wasmJsBrowserTest

echo "✅  WasmJs tests are passed successfully."