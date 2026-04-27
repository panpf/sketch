# 注册组件

翻译：[English](register_component.md)

Sketch 支持通过 [Fetcher], [Decoder] 以及 [Interceptor] 接口扩展 Sketch 的功能，自带的
`sketch-http-*` 和 `sketch-animated-*` 以及 `sketch-svg`、`sketch-video` 等扩展组件就是这样实现的

扩展组件在使用前需要先注册到 Sketch 或 ImageRequest，注册到 Sketch 的组件所有 ImageRequest 都可以使用，而注册到
ImageRequest 的组件则只有当前 ImageRequest 可以使用

> [!TIP]
> 相同 sortWeight 时在 ImageRequest 中注册的组件的优先级高于在 Sketch 中注册的组件

## 注册组件

Sketch 支持自动注册和手动注册两种方式，自动注册默认开启，并且只能注册到全局 Sketch 中，自带的所有模块都已适配了自动注册

### 自动注册

适配了自动注册的模块你只需要配置其依赖即可，在初始化 Sketch 时会通过 [ComponentLoader] 自动搜索并注册。

> [!TIP]
> 自动注册功能在 jvm 平台通过 `ServiceLoader` 实现，在非 jvm 平台通过 `@EagerInitialization` 注解实现。

### 手动注册

如果你想更精细的控制组件的注册可以先禁用自动注册功能，然后通过 Sketch.Builder 的 compoents() 或
addComponents() 方法手动注册组件，如下：

```kotlin
Sketch.Builder(context).apply {
    // 禁用所有组件的自动注册
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

你还可以通过 addIgnoredComponentProvider() 方法只禁用指定组件的自动注册功能，如下：

```kotlin
Sketch.Builder(context).apply {
    // 禁用 MyComponentProvider 组件的自动注册
    addIgnoredComponentProvider(SvgComponentProvider::class)
    components {
        add(SvgDecoder.Factory())
        // ...
    }
}.build()
```

你还可以在 ImageRequest 中注册组件，在 ImageRequest 中注册的组件只用于当前请求，如下：

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

* components() 方法会清空所有已添加的组件，如果你只想追加组件可以用 addComponents() 方法

### 适配自动注册

新开发的组件要适配自动注册功能，需要按如下步骤操作（以全平台组件为例）：

1. 在 commonMain 目录下创建 expect 版本的 [ComponentProvider] 的实现类
2. jvm 平台：
    1. 在 jvmCommonMain 目录下创建 actual 版本的 [ComponentProvider] 的实现类，注意要为
       实现类添加 @Keep 注解，因为 ServiceLoader 是通过反射来创建它的实例的
    2. 在 androidMain 和 desktopMain 目录下创建 'resources/META-INF/services' 目录
    3. 在 services 目录下创建 一个名为 'com.github.panpf.sketch.util.ComponentProvider' 的文件
    4. 在文件中一行一个填写你的 [ComponentProvider] 实现类的全名
3. 非 jvm 平台：
    1. 在 nonJvmCommonMain 目录下创建 actual 版本的 [ComponentProvider] 的实现类
    2. 在 iosMain、wasmJsMain 平台目录下任意位置创建任意文件填写以下内容：
        ```kotlin
        @Suppress("DEPRECATION")
        @OptIn(ExperimentalStdlibApi::class)
        @EagerInitialization
        @Deprecated("", level = DeprecationLevel.HIDDEN)
        val ktorHttpComponentProviderInitHook: Any = ComponentLoader.register(KtorHttpComponentProvider())
        ```
    3. 在 jsMain 平台目录下任意位置创建任意文件填写以下内容：
       ```kotlin
       @JsExport
       @Suppress("DEPRECATION")
       @OptIn(ExperimentalStdlibApi::class, ExperimentalJsExport::class)
       @EagerInitialization
       @Deprecated("", level = DeprecationLevel.HIDDEN)
       val ktorHttpComponentProviderInitHook: Any = ComponentLoader.register(KtorHttpComponentProvider())
       ```
    4. `ktorHttpComponentProviderInitHook` 需要替换成你的 [ComponentProvider] 实现类的名字

> [!TIP]
> 完整示例请参考 `sketch-http-ktor3` 模块

## 组件排序

Sketch 在为请求创建 [Fetcher], [Decoder] 以及执行 [Interceptor] 时是以列表顺序先后执行的，那么组件支持排序就很重要

Sketch 就以 [Fetcher], [Decoder] 以及 [Interceptor] 组件的 sortWight 属性对他们进行排序，sortWight
值越小的优先级越高

所以你在自定义组件的时候可以通过重写 sortWight 属性将组件排在你想要的位置

## 禁用组件

Sketch 不仅支持注册新的组件，还支持禁用默认组件或未来即将添加的组件，只需要将它的 KClass 添加到 Sketch
或 ImageRequest 即可

Sketch 支持自动禁用和手动禁用两种方式

### 自动禁用

自动禁用和自动注册都是依赖 [ComponentProvider] 实现的，实现其 disabledFetchers(), disabledDecoder(),
disabledInterceptors() 方法返回需要禁用的组件的 KClass 即可，如下：

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

具体细节请参考前面的《适配自动注册》章节

### 手动禁用

在 Sketch 中全局禁用，如下：

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

在 ImageRequest 禁用也是可以的，如下：

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