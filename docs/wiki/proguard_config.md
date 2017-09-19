# 配置混淆（Proguard）

### 方法 1

如果你是通过 gradle 远程依赖或是下载了 aar 后本地依赖，那么你无需手动配置任何混淆，因为aar包中已经包含有混淆配置文件，Android 打包工具会自动应用

### 方法 2

如果你是将源码集成到了自己的项目中，那么你需要 将 [sketch] 的 [proguard-rules.pro][sketch-proguard-rules] 文件以及 [sketch-gif] 的 [proguard-rules.pro][sketch-gif-proguard-rules] 文件的内容拷贝到你的 app 的混淆配置文件中

[sketch]: ../../sketch/
[sketch-gif]: ../../sketch-gif/
[sketch-proguard-rules]: ../../sketch/proguard-rules.pro
[sketch-gif-proguard-rules]: ../../sketch-gif/proguard-rules.pro
