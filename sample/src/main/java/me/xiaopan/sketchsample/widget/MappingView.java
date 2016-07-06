package me.xiaopan.sketchsample.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

import me.xiaopan.sketch.SketchImageView;
import me.xiaopan.sketchsample.util.DeviceUtils;

public class MappingView extends SketchImageView {
    private RectF srcRect;
    private Paint paint;

    public MappingView(Context context) {
        super(context);
        init(context);
    }

    public MappingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MappingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        srcRect = new RectF();
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(DeviceUtils.dp2px(context, 1));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (srcRect.isEmpty()) {
            return;
        }

//        srcRect.set(0, 0, 100, 100);
        canvas.drawRect(srcRect, paint);
    }

    public void update(int sourceWidth, RectF sourceSrcRect) {
        if (sourceWidth == 0) {
            return;
        }
        int selfWidth = getWidth();
        float scale = (float) selfWidth / sourceWidth;
        this.srcRect.set(sourceSrcRect.left * scale, sourceSrcRect.top * scale, sourceSrcRect.right * scale, sourceSrcRect.bottom * scale);
//        Log.d("test", "sourceSrcRect: " + sourceSrcRect.toString() + ", scale: " + scale + ", mapping: " + srcRect.toString());
        invalidate();
    }
}
