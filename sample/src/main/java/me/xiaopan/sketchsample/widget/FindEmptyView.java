package me.xiaopan.sketchsample.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

import me.xiaopan.sketch.util.SketchUtils;

public class FindEmptyView extends View {
    private List<Rect> fullRectList;
    private List<Rect> emptyRectList;
    private Rect boundsRect;

    private Paint boundsRectPaint;
    private Paint fullRectPaint;
    private Paint emptyRectPaint;

    public FindEmptyView(Context context) {
        super(context);
        init(context);
    }

    public FindEmptyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FindEmptyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        boundsRectPaint = new Paint();
        boundsRectPaint.setStyle(Paint.Style.STROKE);
        boundsRectPaint.setColor(Color.parseColor("#8800CD00"));
        boundsRectPaint.setStrokeWidth(SketchUtils.dp2px(context, 1));

        fullRectPaint = new Paint();
        fullRectPaint.setColor(Color.parseColor("#88FF0000"));
        fullRectPaint.setStrokeWidth(SketchUtils.dp2px(context, 1));
        fullRectPaint.setStyle(Paint.Style.STROKE);

        emptyRectPaint = new Paint();
        emptyRectPaint.setColor(Color.parseColor("#880000CD"));
        emptyRectPaint.setStrokeWidth(SketchUtils.dp2px(context, 1));
        emptyRectPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (fullRectList != null) {
            for (Rect fullRect : fullRectList) {
                if (!fullRect.isEmpty()) {
                    canvas.drawRect(fullRect.left * 3 + 1, fullRect.top * 3 + 1, fullRect.right * 3 - 1, fullRect.bottom * 3 - 1, fullRectPaint);
                }
            }
        }

        if (emptyRectList != null) {
            for (Rect emptyRect : emptyRectList) {
                if (!emptyRect.isEmpty()) {
                    canvas.drawRect(emptyRect.left * 3 + 1, emptyRect.top * 3 + 1, emptyRect.right * 3 - 1, emptyRect.bottom * 3 - 1, emptyRectPaint);
                }
            }
        }

        if (boundsRect != null && !boundsRect.isEmpty()) {
            canvas.drawRect(boundsRect.left * 3, boundsRect.top * 3, boundsRect.right * 3, boundsRect.bottom * 3, boundsRectPaint);
        }
    }

    public void setBoundsRect(Rect boundsRect) {
        this.boundsRect = boundsRect;
    }

    public void setEmptyRectList(List<Rect> emptyRectList) {
        this.emptyRectList = emptyRectList;
    }

    public void setFullRectList(List<Rect> fullRectList) {
        this.fullRectList = fullRectList;
    }
}
