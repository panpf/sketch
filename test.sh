#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e

./test_desktop.sh
./test_js.sh
./test_wasmJs.sh
./test_android.sh
./test_ios.sh

echo "✅  All tests are passed successfully."