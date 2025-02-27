# Register component

Translations: [简体中文](register_component.zh.md)

Sketch supports extending the functions of Sketch through the [Fetcher] and [Decoder] interfaces,
and the built-in `sketch-http-*` and this is how `sketch-animated-*` and extension components such
as `sketch-svg` and `sketch-video` are implemented

Extended components need to be registered with Sketch or ImageRequest before use. Components
registered with Sketch can be used by all ImageRequests, while components registered with
ImageRequest can only be used by the current ImageRequest.

> [!TIP]
> Components registered in ImageRequest have higher priority than components registered in Sketch

## Register to Sketch

There are two ways to register to Sketch: automatic registration and manual registration. Automatic
registration is enabled by default, and all modules that come with it have been adapted to automatic
registration.

### Automatic registration

Sketch supports automatic discovery and registration of [Fetcher] and [Decoder] components. It is
implemented through `ServiceLoader` on the jvm platform and on non-jvm platforms.
The platform is implemented through the `@EagerInitialization` annotation.

For components that are adapted to automatic registration, you only need to configure their
dependencies. There is no need to manually register the component when initializing Sketch.

### Manual registration

If you want to have more precise control over component registration, you can disable the automatic
component registration function and then use manual component registration, as follows:

```kotlin
Sketch.Builder(context).apply {
    componentLoaderEnabled(false)    // Disable automatic registration for all components
    components {
        addFetcher(MyFetcher.Factory())
        addDecoder(MyDecoder.Factory())
        // ...
    }
}.build()
```

You can also disable the automatic registration function of only some components, as follows:

```kotlin
Sketch.Builder(context).apply {
    addIgnoreFetcherProvider(MyFetcherProvider::class)    // Disable automatic registration of the MyFetcherProvider component
    addIgnoreDecoderProvider(MyDecoderProvider::class)    // Disable automatic registration of the MyDecoderProvider component
    components {
        addFetcher(MyFetcher.Factory())
        addDecoder(MyDecoder.Factory())
        // ...
    }
}.build()
```

### Adapt to automatic registration

To adapt to the automatic registration function, newly developed components need to follow the
following steps (taking full-platform components as an example):

1. Create an implementation class of the expect version [FetcherProvider] or [DecoderProvider] in
   the commonMain directory
2. jvm platform:
    1. Create an actual version of the [FetcherProvider] or [DecoderProvider] implementation class
       in the jvmCommonMain directory. Note that Add the @Keep annotation to the implementation
       class because ServiceLoader creates its instance through reflection
    2. Create 'resources/META-INF/services' directory under androidMain and desktopMain directories
    3. Create a file named 'com.github.panpf.sketch.util.FetcherProvider' or ' in the services
       directory com.github.panpf.sketch.util.DecoderProvider' files
    4. Fill in the full name of your [FetcherProvider] or [DecoderProvider] implementation class one
       line in the file
3. Non-jvm platforms:
    1. Create an actual version of the [FetcherProvider] or [DecoderProvider] implementation class
       in the nonJvmCommonMain directory
   2. Create any file anywhere in the iosMain and wasmJsMain platform directories and fill in the
      following content:
       ```kotlin
       @Suppress("DEPRECATION")
       @OptIn(ExperimentalStdlibApi::class)
       @EagerInitialization
       @Deprecated("", level = DeprecationLevel.HIDDEN)
       val ktorHttpUriFetcherProviderInitHook: Any = ComponentLoader.register(KtorHttpUriFetcherProvider())
       ```
   3. Create any file anywhere in the jsMain platform directory and fill in the following content:
      ```kotlin
      @JsExport
      @Suppress("DEPRECATION")
      @OptIn(ExperimentalStdlibApi::class, ExperimentalJsExport::class)
      @EagerInitialization
      @Deprecated("", level = DeprecationLevel.HIDDEN)
      val ktorHttpUriFetcherProviderInitHook: Any = ComponentLoader.register(KtorHttpUriFetcherProvider())
      ```
   4. `ktorHttpUriFetcherProviderInitHook` and `KtorHttpUriFetcherProvider` need to be replaced
       with yours
       [FetcherProvider] or [DecoderProvider] implementation class name

> [!TIP]
> For a complete example, please refer to the `sketch-http-ktor3` module

## Register to ImageRequest

Registering to ImageRequest is the same as manually registering to Sketch, as follows:

```kotlin
ImageRequest(context, "http://sample.com/sample.jpeg").apply {
    components {
        addFetcher(MyFetcher.Factory())
        addDecoder(MyDecoder.Factory())
        // ...
    }
}.build()
```

[Decoder]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/decode/Decoder.kt

[Fetcher]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/fetch/Fetcher.kt

[FetcherProvider]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/util/ComponentLoader.kt

[DecoderProvider]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/util/ComponentLoader.kt