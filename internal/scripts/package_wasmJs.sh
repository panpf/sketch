#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e

if [ "$1" != "--skipFindRootDir" ]; then
  cd ../../
fi

if [ "$1" != "--skipClean" ]; then
  ./gradlew clean
fi

./gradlew samples:wasmJsApp:wasmJsBrowserDistribution

echo "✅  WasmJs package is created successfully. The distribution is written to $(pwd)/samples/wasmJsApp/build/dist/wasmJs/productionExecutable/"