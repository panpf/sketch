# AsyncImage

翻译：[English](async_image.md)




> [!CAUTION]
> 在 Compose 中你不需要也不能配置 target、listener、以及 progressListener，因为它们容易破坏 ImageRequest 的 equals 结果，造成意外重组，降低性能

要想监听加载状态和进度请使用 AsyncImageState 的 loadState 和 progress 属性，详情请参考文档 [《AsyncImage 详解》](async_image_zh.md)