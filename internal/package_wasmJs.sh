#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e

rootDir="${PWD%/internal*}"
cd "$rootDir"

./gradlew clean
./gradlew samples:wasmJsApp:wasmJsBrowserDistribution

echo "✅  WasmJs package is created successfully. The distribution is written to $(pwd)/samples/wasmJsApp/build/dist/wasmJs/productionExecutable/"