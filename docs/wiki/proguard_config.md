只需在你的`proguard-rules.pro`文件中加入以下配置即可：
```proguard
-keep public class pl.droidsonroids.gif.GifIOException{<init>(int);}

-keep class pl.droidsonroids.gif.GifInfoHandle{<init>(long,int,int,int);}
```
