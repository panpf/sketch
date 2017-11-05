# 统一修改 Options

有时候我们需要对所有请求的 Options 进行检查并修改，例如列表滑动中暂停加载功能就需要检查所有的 [DisplayOptions] 并修改 requestLevel 属性

Sketch 提供了 [OptionsFilter] 接口来实现此功能

### 使用

1.首先你需要实现 [OptionsFilter] 接口，定义你的过滤器，如下：

```java
public class TestOptionsFilter implements OptionsFilter {

    @Override
    public void filter(DownloadOptions options) {
        // 在这里检查并修改 options
    }
}
```

2.然后通过 [OptionsFilterManager] 注册 TestOptionsFilter 即可，如下：

```java
OptinsFilterManager optinsFilterManager = Sketch.with(context).getConfiguration().getOptinsFilterManager();
optinsFilterManager.add(new TestOptionsFilter());
```

### 内置的 OptionsFilter

Sketch 内置了四种 [OptionsFilter]，如下：
* [LowQualityOptionsFilter]：用来控制全局低质量模式
* [InPreferQualityOverSpeedOptionsFilter]：用来控制全局质量优先模式
* [PauseDownloadOptionsFilter]：用来控制暂停下载，配合 [MobileDataPauseDownloadController] 可实现移动数据下暂停下载，另参考 [移动数据或有流量限制的 WIFI 下暂停下载图片，节省流量][pause_download]
* [PauseLoadOptionsFilter]：用来控制暂停加载，配合 [ScrollingPauseLoadManager] 可实现列表滑动中暂停加载，另参考 [列表滑动时暂停加载图片，提升列表滑动流畅度][pause_load]

上述四个 [OptionsFilter], [Configuration] 和 [OptionsFilterManager] 都提供了开关控制，详情请参考源码

[OptionsFilter]: ../../sketch/src/main/java/me/panpf/sketch/optionsfilter/OptionsFilter.java
[OptionsFilterManager]: ../../sketch/src/main/java/me/panpf/sketch/optionsfilter/OptionsFilterManager.java
[LowQualityOptionsFilter]: ../../sketch/src/main/java/me/panpf/sketch/optionsfilter/LowQualityOptionsFilter.java
[InPreferQualityOverSpeedOptionsFilter]: ../../sketch/src/main/java/me/panpf/sketch/optionsfilter/InPreferQualityOverSpeedOptionsFilter.java
[PauseDownloadOptionsFilter]: ../../sketch/src/main/java/me/panpf/sketch/optionsfilter/PauseDownloadOptionsFilter.java
[PauseLoadOptionsFilter]: ../../sketch/src/main/java/me/panpf/sketch/optionsfilter/PauseLoadOptionsFilter.java
[MobileDataPauseDownloadController]: ../../sketch/src/main/java/me/panpf/sketch/optionsfilter/MobileDataPauseDownloadController.java
[ScrollingPauseLoadManager]: ../../sample/src/main/java/me/panpf/sketchsample/util/ScrollingPauseLoadManager.java
[pause_download]: pause_download.md
[pause_load]: pause_load.md
[Configuration]: ../../sketch/src/main/java/me/panpf/sketch/Configuration.java
[DisplayOptions]: ../../sketch/src/main/java/me/panpf/sketch/request/DisplayOptions.java
