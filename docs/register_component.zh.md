# 注册组件

翻译：[English](register_component.md)

Sketch 支持通过 [Fetcher] 和 [Decoder] 接口扩展 Sketch 的功能，自带的 `sketch-http-*` 和
`sketch-animated-*` 以及 `sketch-svg`、`sketch-video` 等扩展组件就是这样实现的

扩展组件在使用前需要先注册到 Sketch 或 ImageRequest，注册到 Sketch 的组件所有 ImageRequest 都可以使用，而注册到
ImageRequest 的组件则只有当前 ImageRequest 可以使用

> [!TIP]
> 在 ImageRequest 中注册的组件的优先级高于在 Sketch 中注册的组件

## 注册到 Sketch

注册到 Sketch 有两种方式：自动注册和手动注册，自动注册默认开启，并且自带的所有模块都已适配了自动注册

### 自动注册

Sketch 支持自动发现并注册 [Fetcher] 和 [Decoder] 组件，在 jvm 平台通过 `ServiceLoader` 实现，在非 jvm
平台通过 `@EagerInitialization` 注解实现。

适配了自动注册的组件你只需要配置其依赖即可，不需要在初始化 Sketch 时手动注册组件。

### 手动注册

如果你想更精细的控制组件的注册就可以禁用自动注册组件功能，然后采用手动注册组件的方式，如下：

```kotlin
Sketch.Builder(context).apply {
    componentLoaderEnabled(false)    // 禁用所有组件的自动注册
    components {
        addFetcher(MyFetcher.Factory())
        addDecoder(MyDecoder.Factory())
        // ...
    }
}.build()
```

你还可以只禁用部分组件的自动注册功能，如下：

```kotlin
Sketch.Builder(context).apply {
    addIgnoreFetcherProvider(MyFetcherProvider::class)    // 禁用 MyFetcherProvider 组件的自动注册
    addIgnoreDecoderProvider(MyDecoderProvider::class)    // 禁用 MyDecoderProvider 组件的自动注册
    components {
        addFetcher(MyFetcher.Factory())
        addDecoder(MyDecoder.Factory())
        // ...
    }
}.build()
```

### 适配自动注册

新开发的组件要适配自动注册功能，需要按如下步骤操作（以全平台组件为例）：

1. 在 commonMain 目录下创建 expect 版本的 [FetcherProvider] 或 [DecoderProvider] 的实现类
2. jvm 平台：
    1. 在 jvmCommonMain 目录下创建 actual 版本的 [FetcherProvider] 或 [DecoderProvider] 的实现类，注意要为
       实现类添加 @Keep 注解，因为 ServiceLoader 是通过反射来创建它的实例的
    2. 在 androidMain 和 desktopMain 目录下创建 'resources/META-INF/services' 目录
    3. 在 services 目录下创建 一个名为 'com.github.panpf.sketch.util.FetcherProvider' 或 '
       com.github.panpf.sketch.util.DecoderProvider' 的文件
    4. 在文件中一行一个填写你的 [FetcherProvider] 或 [DecoderProvider] 实现类的全名
3. 非 jvm 平台：
    1. 在 nonJvmCommonMain 目录下创建 actual 版本的 [FetcherProvider] 或 [DecoderProvider] 的实现类
   2. 在 iosMain、wasmJsMain 平台目录下任意位置创建任意文件填写以下内容：
       ```kotlin
       @Suppress("DEPRECATION")
       @OptIn(ExperimentalStdlibApi::class)
       @EagerInitialization
       @Deprecated("", level = DeprecationLevel.HIDDEN)
       val ktorHttpUriFetcherProviderInitHook: Any = ComponentLoader.register(KtorHttpUriFetcherProvider())
       ```
   3. 在 jsMain 平台目录下任意位置创建任意文件填写以下内容：
      ```kotlin
      @JsExport
      @Suppress("DEPRECATION")
      @OptIn(ExperimentalStdlibApi::class, ExperimentalJsExport::class)
      @EagerInitialization
      @Deprecated("", level = DeprecationLevel.HIDDEN)
      val ktorHttpUriFetcherProviderInitHook: Any = ComponentLoader.register(KtorHttpUriFetcherProvider())
      ```
   4. `ktorHttpUriFetcherProviderInitHook` 和 `KtorHttpUriFetcherProvider` 需要替换成你的
       [FetcherProvider] 或 [DecoderProvider] 实现类的名字

> [!TIP]
> 完整示例请参考 `sketch-http-ktor3` 模块

## 注册到 ImageRequest

注册到 ImageRequest 则和手动注册到 Sketch 一样，如下：

```kotlin
ImageRequest(context, "http://sample.com/sample.jpeg") {
    components {
        addFetcher(MyFetcher.Factory())
        addDecoder(MyDecoder.Factory())
        // ...
    }
}
```

[Decoder]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/decode/Decoder.kt

[Fetcher]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/fetch/Fetcher.kt

[FetcherProvider]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/util/ComponentLoader.kt

[DecoderProvider]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/util/ComponentLoader.kt