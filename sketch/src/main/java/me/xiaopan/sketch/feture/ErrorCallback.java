package me.xiaopan.sketch.feture;

import android.graphics.BitmapFactory;

import java.io.File;
import java.io.IOException;

import me.xiaopan.sketch.Identifier;
import me.xiaopan.sketch.request.LoadRequest;

public interface ErrorCallback extends Identifier {
    void onInstallDiskCacheFailed(IOException e, File cacheDir, int count);
    void onDecodeGifImageFailed(Throwable error, LoadRequest request, BitmapFactory.Options options);
    void onDecodeNormalImageFailed(Throwable error, LoadRequest request, BitmapFactory.Options options);
}
