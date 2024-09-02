#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e

./package_android.sh
./package_ios.sh
./package_desktop.sh
./package_js.sh
./package_wasmJs.sh