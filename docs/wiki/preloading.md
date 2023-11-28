# Preload images into memory

Translations: [简体中文](preloading_zh.md)

To preload images into memory, you only need to not set target, as follows:

```kotlin
DisplayImage(context, "https://www.sample.com/image.jpg") {
    // more ...
}.enqueue()
```

In order to ensure that the cache is accurately hit when used later, the configuration during
preloading needs to be exactly the same as when used, especially resizeSize