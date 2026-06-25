#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e

rootDir="${PWD%/internal/*}"
cd $rootDir

./gradlew \
  internal:componentLoaderTest:connectedAndroidTest \
  sketch-animated-core:connectedAndroidTest \
  sketch.sketch-animated-gif:connectedAndroidTest \
  sketch.sketch-animated-gif-koral:connectedAndroidTest \
  sketch.sketch-animated-webp:connectedAndroidTest \
  sketch.sketch-blurhash:connectedAndroidTest \
  sketch.sketch-core:connectedAndroidTest \
  sketch.sketch-extensions-apkicon:connectedAndroidTest \
  sketch.sketch-extensions-appicon:connectedAndroidTest \
  sketch.sketch-extensions-core:connectedAndroidTest \
  sketch.sketch-extensions-view:connectedAndroidTest \
  sketch.sketch-extensions-viewability:connectedAndroidTest \
  sketch.sketch-http:connectedAndroidTest \
  sketch.sketch-http-core:connectedAndroidTest \
  sketch.sketch-http-hurl:connectedAndroidTest \
  sketch.sketch-http-ktor2:connectedAndroidTest \
  sketch.sketch-http-ktor2-core:connectedAndroidTest \
  sketch.sketch-http-ktor3:connectedAndroidTest \
  sketch.sketch-http-ktor3-core:connectedAndroidTest \
  sketch.sketch-http-okhttp:connectedAndroidTest \
  sketch.sketch-koin:connectedAndroidTest \
  sketch.sketch-singleton:connectedAndroidTest \
  sketch.sketch-svg:connectedAndroidTest \
  sketch.sketch-video:connectedAndroidTest \
  sketch.sketch-video-core:connectedAndroidTest \
  sketch.sketch-video-ffmpeg:connectedAndroidTest \
  sketch.sketch-view:connectedAndroidTest \
  sketch.sketch-view-core:connectedAndroidTest \
  sketch.sketch-view-koin:connectedAndroidTest \
  --continue

echo "✅  Android tests are passed successfully."