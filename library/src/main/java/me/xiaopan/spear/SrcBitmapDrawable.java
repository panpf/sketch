package me.xiaopan.spear;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class SrcBitmapDrawable extends Drawable {
    private static final int DEFAULT_PAINT_FLAGS = Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG;
    private Bitmap mBitmap;
    private Paint mPaint;
    private Rect srcRect;
    private Point fixedSize;
    private int bitmapWidth;
    private int bitmapHeight;

    public SrcBitmapDrawable(Bitmap bitmap) {
        this.mBitmap = bitmap;
        this.bitmapWidth = bitmap!=null?bitmap.getWidth():0;
        this.bitmapHeight = bitmap!=null?bitmap.getHeight():0;
        if(mBitmap != null){
            this.mPaint = new Paint(DEFAULT_PAINT_FLAGS);
        }
        this.srcRect = new Rect(0, 0, bitmapWidth, bitmapHeight);
    }

    @Override
    public void draw(Canvas canvas) {
        final Bitmap bitmap = this.mBitmap;
        if (bitmap == null || bitmap.isRecycled()) {
            return;
        }

        canvas.drawBitmap(bitmap, srcRect, getBounds(), mPaint);
    }

    @Override
    public void setAlpha(int alpha) {
        if(mPaint != null){
            final int oldAlpha = mPaint.getAlpha();
            if (alpha != oldAlpha) {
                mPaint.setAlpha(alpha);
                invalidateSelf();
            }
        }
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        if(mPaint != null){
            mPaint.setColorFilter(cf);
            invalidateSelf();
        }
    }

    @Override
    public int getOpacity() {
        final Bitmap bitmap = mBitmap;
        return (bitmap == null || mPaint == null || bitmap.hasAlpha() || mPaint.getAlpha() < 255) ? PixelFormat.TRANSLUCENT : PixelFormat.OPAQUE;
    }

    public Point getFixedSize() {
        return fixedSize;
    }

    public void setFixedSize(Point fixedSize) {
        this.fixedSize = fixedSize;
        if(fixedSize == null){
            srcRect.set(0, 0, bitmapWidth, bitmapHeight);
            super.setBounds(0, 0, bitmapWidth, bitmapHeight);
        }else{
            onUpdateFixedSize(fixedSize.x, fixedSize.y);
        }
    }

    public void setFixedSize(int width, int height) {
        if(this.fixedSize == null){
            this.fixedSize = new Point(width, height);
        }else{
            this.fixedSize.set(width, height);
        }
        onUpdateFixedSize(width, height);
    }

    protected void onUpdateFixedSize(int fixedWidth, int fixedHeight){
        if(bitmapWidth == 0 || bitmapHeight == 0){
            srcRect.set(0, 0, 0, 0);
        }else if((float)bitmapWidth/(float)bitmapHeight == (float)fixedWidth/(float)fixedHeight){
            srcRect.set(0, 0, bitmapWidth, bitmapHeight);
        }else{
            mapping(bitmapWidth, bitmapHeight, fixedWidth, fixedHeight, srcRect);
        }
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    @Override
    public int getIntrinsicWidth() {
        return srcRect.width();
    }

    @Override
    public int getIntrinsicHeight() {
        return srcRect.height();
    }

    private void mapping(int sourceWidth, int sourceHeight, int targetWidth, int targetHeight, Rect rect){
        float widthScale = (float)sourceWidth/targetWidth;
        float heightScale = (float)sourceHeight/targetHeight;
        float finalScale = widthScale<heightScale?widthScale:heightScale;
        int srcWidth = (int)(targetWidth*finalScale);
        int srcHeight = (int)(targetHeight*finalScale);
        int srcLeft = (sourceWidth - srcWidth)/2;
        int srcTop = (sourceHeight - srcHeight)/2;
        rect.set(srcLeft, srcTop, srcLeft+srcWidth, srcTop+srcHeight);
    }
}