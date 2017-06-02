此版本主要是修复了几个bug然后升级了示例App

:fire::fire::fire: 如果你有与ImageViewInterface相关的自定义就不能无痛升级 :fire::fire::fire:

### ImageDisplayer
* :art: 现在TransitionDisplayer遇到两张一模一样的图片时不再执行过渡动画

### Download
* :bug: 修复下载进度不回调的bug
* :art: 下载进度回调间隔由1000毫秒减少到100毫秒，这样进度的变化更明显

### Other:
* :hammer: ImageViewInterface改名为SketchView并移到me.xiaopan.sketch目录下，`所有与此相关的自定义都需要修改`

### ImagePreprocessor
* :bug: 修复所有内置Preprocessor在获取锁后发现有缓存的情况下没有解锁的bug
* :bug: 修复ApkPreprocessor没有使用正确的磁盘缓存key的bug

### Sample App：
* :sparkles: 新增Unsplash页面，可浏览来自Unsplash的高质量图片
* :fire: 去掉了明星图片页面