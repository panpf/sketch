package me.xiaopan.sketch.process;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.cache.BitmapPool;
import me.xiaopan.sketch.request.Resize;

/**
 * 在图片上面个盖上一层颜色，可兼容形状不规则的透明图片
 */
public class MaskImageProcessor extends WrappedImageProcessor {

    protected String logName = "MaskImageProcessor";
    private Paint paint;

    private int maskColor;

    public MaskImageProcessor(int maskColor, WrappedImageProcessor wrappedProcessor) {
        super(wrappedProcessor);
        this.maskColor = maskColor;
    }

    public MaskImageProcessor(int maskColor) {
        this(maskColor, null);
    }

    /**
     * 获取遮罩颜色
     */
    @SuppressWarnings("unused")
    public int getMaskColor() {
        return maskColor;
    }

    @Override
    public String onGetKey() {
        return String.format("%s(maskColor=%d)", logName, maskColor);
    }

    @Override
    public Bitmap onProcess(Sketch sketch, Bitmap bitmap, Resize resize, boolean forceUseResize, boolean lowQualityImage) {
        if (bitmap == null || bitmap.isRecycled()) {
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

        int saveFlags = Canvas.MATRIX_SAVE_FLAG | Canvas.CLIP_SAVE_FLAG | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG |
                Canvas.FULL_COLOR_LAYER_SAVE_FLAG | Canvas.CLIP_TO_LAYER_SAVE_FLAG;
        int src = canvas.saveLayer(0, 0, bitmap.getWidth(), bitmap.getHeight(), paint, saveFlags);

        canvas.drawBitmap(bitmap, 0, 0, null);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawRect(0, 0, bitmap.getWidth(), bitmap.getHeight(), paint);

        canvas.restoreToCount(src);

        return maskBitmap;
    }
}
