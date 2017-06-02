Sketch的缩略图模式专门用于在一个较小的ImageView上清晰的显示一张超大且宽高比也超大的图片的缩略图

假如ImageView宽高为400x400，图片宽高为30000x960，用400x400和30000x960计算得出的inSampleSize为16，这样得到的图片将极度模糊，无法辨别任何内容

但假如我们只读取图片中间映射的部分呢，计算得出映射后的位置是14537,0-15463,926，宽高是926x926，我们再用400x400和926x926计算得出的inSampleSize为4，这样就清晰的多了

最终我们利用BitmapRegionDecoder读取14537,0-15463,926位置的图片并且缩小4倍

>* 不同的ScaleType计算出的映射位置会有所不同

#### 支持的图片类型和系统版本
>* jpeg、png：API 10（2.3.3）及其以上
>* webp：API 14（4.0）及其以上

#### 使用条件

1. 首先必须要配置resize
2. 然后满足上述对图片类型和系统版本的要求并且resize的宽高比和原图的宽高比相差1.5倍即可

```java
public boolean canUseThumbnailMode(int outWidth, int outHeight, int resizeWidth, int resizeHeight){
    if (resizeWidth > outWidth && resizeHeight > outHeight) {
        return false;
    }

    float resizeScale = (float) resizeWidth / resizeHeight;
    float imageScale = (float) outWidth / outHeight;
    return Math.max(resizeScale, imageScale) > Math.min(resizeScale, imageScale) * 1.5f;
}
```

例如上述示例中resize宽高比为400/400=1.0f，原图宽高比为30000/960=32.4，32.4f > 1.0 * 1.5f = true成立

#### 如何开启

```java
SketchImageView sketchImageView = ...;
DisplayOptions options = sketchImageView.getOptions();
LayoutParams params = sketchImageView.getLayoutParams();

if (params.width != 0 && params.height != 0) {  
  // 用params.width和params.height做为resize
  options.setResizeByFixedSize();

  // Sketch会默认取params.width和params.height做为maxSize，因此不用设置
} else {
  options.setResize(400, 400);
  options.setMaxSize(400, 400);
}

// 开启缩略图模式
options.setThumbnailMode(true);

sketchImageView.displayImage("http://b.zol-img.com.cn/desk/bizhi/image/4/1366x768/1387347695254.jpg");
```
