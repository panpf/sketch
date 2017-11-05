package me.panpf.sketch.drawable;

import android.graphics.Bitmap;

import me.panpf.sketch.decode.ImageAttrs;
import me.panpf.sketch.util.SketchUtils;

public abstract class SketchBitmap {

    private String key;
    private String uri;
    protected Bitmap bitmap;
    private ImageAttrs attrs;

    protected SketchBitmap(Bitmap bitmap, String key, String uri, ImageAttrs attrs) {
        if (bitmap == null || bitmap.isRecycled()) {
            throw new IllegalArgumentException("bitmap is null or recycled");
        }

        this.bitmap = bitmap;
        this.key = key;
        this.uri = uri;
        this.attrs = attrs;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public String getKey() {
        return key;
    }

    public String getUri() {
        return uri;
    }

    public ImageAttrs getAttrs() {
        return attrs;
    }

    public abstract String getInfo();

    public int getByteCount() {
        return SketchUtils.getByteCount(getBitmap());
    }

    public Bitmap.Config getBitmapConfig() {
        return bitmap != null ? bitmap.getConfig() : null;
    }
}
