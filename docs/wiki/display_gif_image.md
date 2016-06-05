Sketch集成了android-gif-drawable 1.1.7支持gif图片

Sketch默认不解码gif图，只会通过BitmapFactory读取其第一帧作为一个普通的图片。需要你通过各种Options的setDecodeGifImage(true)或各种Helper的decodeGifImage()明确指明可以播放gif才会使用GifDrawable播放gif，例如：

```
sketImageView.getOptions()
    .setDecodeGifImage(true);
```
详情请参考[配置各种属性.md](options.md)

注意：
>* Sketch会根据mimeType判断是否是gif图，因此不用担心识别不了伪装成jpg的gif图
>* gif图不能使用maxSize、resize、TransitionImageDisplayer
>* gif图还不能使用内存缓存，因为GifDrawable需要依赖Callback才能播放，
如果缓存的话就会出现一个GifDrawable被显示在多个ImageView上的情况，这时候就只有最后一个能正常播放

SketchImageView还支持当显示的图片是gif图时在右下角显示一个图标，告诉用户这是一张gif图，详情请参考[SketchImageView详细使用说明.md](sketch_image_view.md)