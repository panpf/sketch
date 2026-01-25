#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e

cd ../../

"$(pwd)/internal/scripts/test_desktop.sh" --skipFindRootDir
"$(pwd)/internal/scripts/test_js.sh" --skipFindRootDir
"$(pwd)/internal/scripts/test_wasmJs.sh" --skipFindRootDir
"$(pwd)/internal/scripts/test_android.sh" --skipFindRootDir
"$(pwd)/internal/scripts/test_ios.sh" --skipFindRootDir

echo "✅  All tests are passed successfully."