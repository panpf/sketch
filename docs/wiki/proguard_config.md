### 方法1
如果你是通过 gradle 远程依赖或是下载了 aar 后本地依赖，那么你无需手动配置任何混淆，因为aar包中已经包含有混淆配置文件，Android打包工具会自动应用


### 方法2

如果你是将源码集成到了自己的项目中，那么你需要配置以下混淆

sketch混淆配置：

```proguard
# 只有SketchGifDrawableImpl类与sketch-gif有联系，因此当缺失sketch-gif时SketchGifDrawableImpl类在混淆时会发出警告
-dontwarn me.xiaopan.sketch.drawable.SketchGifDrawableImpl
-dontwarn me.xiaopan.sketch.drawable.SketchGifDrawableImpl$1

# 实现了Initializer接口的类需要在AndroidManifest中配置，然后再运行时实例化，因此不能混淆
-keep public class * implements me.xiaopan.sketch.Initializer
```

sketch-gif混淆配置：

```proguard
-keep public class pl.droidsonroids.gif.GifIOException{<init>(int, java.lang.String);}

# sketch就以这两个类是否存在判断是否可以使用gif
-keep class me.xiaopan.sketch.gif.BuildConfig
-keep class pl.droidsonroids.gif.GifDrawable
```

`具体的混淆配置请以sketch.aar和sketch-gif.aar包中包含的混淆配置文件为准，因为后续可能会更新混淆配置，而这个文档未必会及时更新`
