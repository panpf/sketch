# Register component

Translations: [简体中文](register_component.zh.md)

Sketch supports extending the functions of Sketch through the [Fetcher], [Decoder] and [Interceptor]
interfaces. The built-in `sketch-http-*` and `sketch-animated-*` as well as `sketch-svg`,
`sketch-video` and other extension components are implemented in this way.

Extended components need to be registered with Sketch or ImageRequest before use. Components
registered with Sketch can be used by all ImageRequests, while components registered with
ImageRequest can only be used by the current ImageRequest.

> [!TIP]
> When the sortWeight is the same, components registered in ImageRequest have a higher priority than
> components registered in Sketch.

## Register component

Sketch supports automatic registration and manual registration. Automatic registration is enabled by
default and can only be registered in global Sketch. All modules that come with it have been adapted
to automatic registration.

### Automatic registration

For modules that are adapted to automatic registration, you only need to configure their
dependencies. When initializing Sketch, it will be automatically searched and registered
through [ComponentLoader].

> [!TIP]
> The automatic registration function is implemented on the jvm platform through `ServiceLoader`,
> and on non-jvm platforms through the `@EagerInitialization` annotation.

### Manual registration

If you want to have more fine-grained control over the registration of components, you can disable
the automatic registration function first, and then manually register the components through the
components() or addComponents() methods of Sketch.Builder, as follows:

```kotlin
Sketch.Builder(context).apply {
    // Disable automatic registration for all components
    componentLoaderEnabled(false)
    components {
        add(MyFetcher.Factory())
        add(MyDecoder.Factory())
        add(SvgDecoder.Factory())
        add(MyInterceptor())
        // ...
    }
}.build()
```

You can also disable the automatic registration function of only the specified component through the
addIgnoredComponentProvider() method, as follows:

```kotlin
Sketch.Builder(context).apply {
    // Disable automatic registration of the MyComponentProvider component
    addIgnoredComponentProvider(SvgComponentProvider::class)
    components {
        add(SvgDecoder.Factory())
        // ...
    }
}.build()
```

You can also register components in ImageRequest. Components registered in ImageRequest are only
used for the current request, as follows:

```kotlin
ImageRequest(context, "http://sample.com/sample.jpeg") {
    components {
        addFetcher(MyFetcher.Factory())
        addDecoder(MyDecoder.Factory())
        // ...
    }
}
```

> [!IMPORTANT]

* The components() method will clear all added components. If you only want to add components, you
  can use the addComponents() method.

### Adapting Automatic Registration

To adapt to the automatic registration function, newly developed components need to follow the
following steps (taking full-platform components as an example):

1. Create the implementation class of the expect version [ComponentProvider] in the commonMain
   directory
2. jvm platform:
    1. Create the actual version of the [ComponentProvider] implementation class in the
       jvmCommonMain directory. Be careful to add the @Keep annotation to the implementation class,
       because ServiceLoader creates its instance through reflection.
    2. Create 'resources/META-INF/services' directory under androidMain and desktopMain directories
    3. Create a file named 'com.github.panpf.sketch.util.ComponentProvider' in the services
       directory
    4. Fill in the full name of your [ComponentProvider] implementation class one line at a time in
       the file
3. Non-jvm platforms:
    1. Create the actual version of the [ComponentProvider] implementation class in the
       nonJvmCommonMain directory
    2. Create any file anywhere in the iosMain and wasmJsMain platform directories and fill in the
       following content:
        ```kotlin
        @Suppress("DEPRECATION")
        @OptIn(ExperimentalStdlibApi::class)
        @EagerInitialization
        @Deprecated("", level = DeprecationLevel.HIDDEN)
        val ktorHttpComponentProviderInitHook: Any = ComponentLoader.register(KtorHttpComponentProvider())
        ```
    3. Create any file anywhere in the jsMain platform directory and fill in the following content:
       ```kotlin
       @JsExport
       @Suppress("DEPRECATION")
       @OptIn(ExperimentalStdlibApi::class, ExperimentalJsExport::class)
       @EagerInitialization
       @Deprecated("", level = DeprecationLevel.HIDDEN)
       val ktorHttpComponentProviderInitHook: Any = ComponentLoader.register(KtorHttpComponentProvider())
       ```
    4. `ktorHttpComponentProviderInitHook` Need to be replaced with the name of
       your [ComponentProvider] implementation class

> [!TIP]
> For a complete example, please refer to the `sketch-http-ktor3` module

## Component sorting

When Sketch creates [Fetcher], [Decoder] and executes [Interceptor] for a request, it is executed in
list order, so it is important that the component supports sorting.

Sketch sorts [Fetcher], [Decoder] and [Interceptor] components based on their sortWight property.
The smaller the sortWight value, the higher the priority.

So when you customize a component, you can arrange the component where you want by overriding the
sortWight property.

## Disable component

Sketch not only supports registering new components, but also supports disabling default components
or components that will be added in the future. You only need to add its KClass to Sketch or
ImageRequest.

Sketch supports automatic disabling and manual disabling.

### Automatic disabled

Automatic disabling and automatic registration are implemented by relying on [ComponentProvider].
Implement its disabledFetchers(), disabledDecoder(), disabledInterceptors() methods to return the
KClass of the component that needs to be disabled, as follows:

```kotlin
class MyComponentProvider : ComponentProvider {

    override fun addFetchers(context: PlatformContext): List<Fetcher.Factory>? {
        return null
    }

    override fun addDecoders(context: PlatformContext): List<Decoder.Factory>? {
        return listOf(MyDecoder.Factory())
    }

    override fun addInterceptors(context: PlatformContext): List<Interceptor>? {
        return null
    }

    override fun disabledFetchers(context: PlatformContext): List<KClass<out Fetcher.Factory>>? {
        return null
    }

    override fun disabledDecoders(context: PlatformContext): List<KClass<out Decoder.Factory>>? {
        return listOf(SkiaDecoder::class)
    }

    override fun disabledInterceptors(context: PlatformContext): List<KClass<out Interceptor>>? {
        return null
    }

    override fun toString(): String = "MyComponentProvider"
}
```

For specific details, please refer to the previous chapter "Adapting Automatic Registration"

### Manually disable

Disable it globally in Sketch as follows:

```kotlin
Sketch.Builder(context).apply {
    components {
        disabledFetcher(FileUriFetcher.Factory::class)
        disabledDecoder(SvgDecoder.Factory::class)
        disabledInterceptor(ResultCacheInterceptor::class)
        // ...
    }
}.build()
```

It is also possible to disable it in ImageRequest, as follows:

```kotlin
ImageRequest(context, "http://sample.com/sample.jpeg") {
    components {
        disabledFetcher(FileUriFetcher.Factory::class)
        disabledDecoder(SvgDecoder.Factory::class)
        disabledInterceptor(ResultCacheInterceptor::class)
        // ...
    }
}.build()
```

[Decoder]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/decode/Decoder.kt

[Fetcher]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/fetch/Fetcher.kt

[Interceptor]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/Interceptor.kt

[ComponentLoader]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/util/ComponentLoader.kt

[ComponentProvider]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/util/ComponentLoader.kt

[ComponentProvider]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/util/ComponentLoader.kt