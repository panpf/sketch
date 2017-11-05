# 使用 MemoryCacheStateImage 先显示已缓存的较模糊的图片，然后再显示清晰的图片

### 背景

显示一个图片列表的时候通常我们会在列表页显示较小的图片，点击进入图片详情页后再显示更清晰原图

在进入图片详情页后通常的做法是先显示一个转转转的进度条，然后下载原始图片并显示，如果这时候网络较慢的话，用户会长时间在看一个空白的页面，体验不太好

要优化这个问题，最容易想到的办法就是先显示在列表页已经显示的较小的图片，等原始大图加载完后再显示原始大图

### MemoryCacheStateImage

现在通过 [MemoryCacheStateImage] 就可以轻松实现这样的效果，首先 [MemoryCacheStateImage] 需要一个内存缓存 key 才能从内存缓存中取出图片，然后用这张图片作为 loading 占位图显示，所以这里的关键就是怎么才能知道上一个页面中那些图片的内存缓存 key 呢？

首先我们需要知道内存缓存 key 的构成，其实内存缓存 key 就是请求 key，请求 key 是由 uri 和选项构成的

```
public class DisplayHelper {
    protected void preProcess() {
        ....

        // 根据URI和显示选项生成请求Key
        key = SketchUtils.makeRequestKey(uri, uriModel, displayOptions);
    }
}
```

实际上是由 [SketchUtils].makeRequestKey() 方法生成的请求 key

```java
@NonNull
public static String makeRequestKey(@NonNull String imageUri, @NonNull UriModel uriModel, @NonNull String optionsKey) {
    StringBuilder builder = new StringBuilder();
    if (uriModel.isConvertShortUriForKey()) {
        builder.append(SketchMD5Utils.md5(imageUri));
    } else {
        builder.append(imageUri);
    }
    if (!TextUtils.isEmpty(optionsKey)) {
        builder.append(optionsKey);
    }
    return builder.toString();
}
```

从上面我们可以看到内存缓存 key 就是由 uri 和选项 key 构成，因此我们可以拿到上一个页面 ImageView的选项 key，然后在图片详情页将 uri 和选项 key 拼接一下就能得到内存缓存 key 了

第一步，在图片列表页点击图片跳转的时候取出选项 key 并传到图片详情页

```java
public void onClick(View v) {
    SketchImageView sketchImageView = (SketchImageView) v;
    String optionsKey = sketchImageView.getOptionsKey();

    Intent intent = new Intent(context, ImageDetailActivity.class);
    intent.put("optionsKey", optionsKey);
    intent.put("imageUrls", ....);
    startActivity(intent);
}
```
`一定要通过 SketchImageView 的 getOptionsKey() 方法获取选项 key，因为在 commit 方法里会对 Options 进行补充处理，所以通过 getOptionsKey() 得到的才是才是最终有效的选项 key`

第二步，假如 ImageDetailActivity 里面是一个 ViewPager，然后又把选项 key 和图片 uri，传到了最终的 ImageFragment 中，那么就在 ImageFragment 中拼装内存缓存 key
```java
public class ImageFragment extends Fragment {
    String optionsKey;
    String imageUri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);  

        optionsKey = getArguments().getString("optionsKey");
        imageUri = getArguments().getString("imageUri");
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        SketchImageView sketchImageView = view.findViewById(R.id.iamge);
        DisplayOptions options = sketchImageView.getOptions();

        Configuration configuration = Sketch.with(getActivity()).getConfiguration();
        UriModel uriModel = configuration.getUriModelManager().match(imageUri);
        String loadingImageMemoryCacheKey = SketchUtils.makeRequestKey(imageUri, uriModel, optionsKey);       
        options.setLoadingImage(new MemoryCacheStateImage(loadingImageMemoryCacheKey, null));

        options.set....;
        sketchImageView.displayImage(imageUri);
    }
}
```

更详细的实现细节请参考示例 app

[MemoryCacheStateImage]: ../../sketch/src/main/java/me/panpf/sketch/state/MemoryCacheStateImage.java
[SketchUtils]: ../../sketch/src/main/java/me/panpf/sketch/util/SketchUtils.java
