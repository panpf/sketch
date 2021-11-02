package com.github.panpf.sketch.process;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Locale;

import com.github.panpf.sketch.Sketch;
import com.github.panpf.sketch.cache.BitmapPool;
import com.github.panpf.sketch.request.Resize;

/**
 * 在图片上面个盖上一层颜色，可兼容形状不规则的透明图片
 */
public class MaskImageProcessor extends WrappedImageProcessor {

    @Nullable
    private Paint paint;
    private int maskColor;

    /**
     * 创建一个遮罩图片处理器
     *
     * @param maskColor        遮罩颜色
     * @param wrappedProcessor 嵌套一个图片处理器
     */
    public MaskImageProcessor(int maskColor, @Nullable WrappedImageProcessor wrappedProcessor) {
        super(wrappedProcessor);
        this.maskColor = maskColor;
    }

    /**
     * 创建一个遮罩图片处理器
     *
     * @param maskColor 遮罩颜色
     */
    public MaskImageProcessor(int maskColor) {
        this(maskColor, null);
    }

    @NonNull
    @Override
    public Bitmap onProcess(@NonNull Sketch sketch, @NonNull Bitmap bitmap, Resize resize, boolean lowQualityImage) {
        if (bitmap.isRecycled()) {
            return bitmap;
        }

        BitmapPool bitmapPool = sketch.getConfiguration().getBitmapPool();

        Bitmap.Config newBitmapConfig = bitmap.getConfig();
        if (newBitmapConfig == null) {
            newBitmapConfig = lowQualityImage ? Bitmap.Config.ARGB_4444 : Bitmap.Config.ARGB_8888;
        }

        Bitmap maskBitmap;
        boolean isNewBitmap = false;
        if (bitmap.isMutable()) {
            maskBitmap = bitmap;
        } else {
            maskBitmap = bitmapPool.getOrMake(bitmap.getWidth(), bitmap.getHeight(), newBitmapConfig);
            isNewBitmap = true;
        }

        Canvas canvas = new Canvas(maskBitmap);

        if (isNewBitmap) {
            canvas.drawBitmap(bitmap, 0, 0, null);
        }

        if (paint == null) {
            paint = new Paint();
            paint.setColor(maskColor);
        }
        paint.setXfermode(null);

        int src = canvas.saveLayer(0, 0, bitmap.getWidth(), bitmap.getHeight(), paint, Canvas.ALL_SAVE_FLAG);

        canvas.drawBitmap(bitmap, 0, 0, null);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawRect(0, 0, bitmap.getWidth(), bitmap.getHeight(), paint);

        canvas.restoreToCount(src);

        return maskBitmap;
    }

    /**
     * 获取遮罩颜色
     */
    public int getMaskColor() {
        return maskColor;
    }

    @NonNull
    @Override
    public String onToString() {
        return String.format(Locale.US, "%s(%d)", "MaskImageProcessor", maskColor);
    }

    @Override
    public String onGetKey() {
        return String.format(Locale.US, "%s(%d)", "Mask", maskColor);
    }
}
