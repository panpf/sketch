package com.github.panpf.sketch.util.internal

// https://youtrack.jetbrains.com/issue/KT-52344
// When a module does not have source code, the generateMetadataFileForIosArm64Publication task will throw an exception as follows
// java.io.FileNotFoundException: /Users/panpf/Workspace/sketch/sketch-http/build/classes/kotlin/iosArm64/main/klib/sketch-http.klib (No such file or directory)
// Create an empty file for this to avoid triggering this error