package me.xiaopan.sketchsample.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View

import me.xiaopan.sketch.util.SketchUtils

class FindEmptyView : View {
    private var fullRectList: List<Rect>? = null
    private var emptyRectList: List<Rect>? = null
    private var boundsRect: Rect? = null

    private val boundsRectPaint: Paint = Paint()
    private val fullRectPaint: Paint = Paint()
    private val emptyRectPaint: Paint = Paint()

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        boundsRectPaint.style = Paint.Style.STROKE
        boundsRectPaint.color = Color.parseColor("#8800CD00")
        boundsRectPaint.strokeWidth = SketchUtils.dp2px(context, 1).toFloat()

        fullRectPaint.color = Color.parseColor("#88FF0000")
        fullRectPaint.strokeWidth = SketchUtils.dp2px(context, 1).toFloat()
        fullRectPaint.style = Paint.Style.STROKE

        emptyRectPaint.color = Color.parseColor("#880000CD")
        emptyRectPaint.strokeWidth = SketchUtils.dp2px(context, 1).toFloat()
        emptyRectPaint.style = Paint.Style.STROKE
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (fullRectList != null) {
            for (fullRect in fullRectList!!) {
                if (!fullRect.isEmpty) {
                    canvas.drawRect(
                            (fullRect.left * 3 + 1).toFloat(),
                            (fullRect.top * 3 + 1).toFloat(),
                            (fullRect.right * 3 - 1).toFloat(),
                            (fullRect.bottom * 3 - 1).toFloat(),
                            fullRectPaint)
                }
            }
        }

        if (emptyRectList != null) {
            for (emptyRect in emptyRectList!!) {
                if (!emptyRect.isEmpty) {
                    canvas.drawRect(
                            (emptyRect.left * 3 + 1).toFloat(),
                            (emptyRect.top * 3 + 1).toFloat(),
                            (emptyRect.right * 3 - 1).toFloat(),
                            (emptyRect.bottom * 3 - 1).toFloat(),
                            emptyRectPaint)
                }
            }
        }

        if (boundsRect != null && !boundsRect!!.isEmpty) {
            canvas.drawRect((boundsRect!!.left * 3).toFloat(), (boundsRect!!.top * 3).toFloat(), (boundsRect!!.right * 3).toFloat(), (boundsRect!!.bottom * 3).toFloat(), boundsRectPaint)
        }
    }

    fun setBoundsRect(boundsRect: Rect) {
        this.boundsRect = boundsRect
    }

    fun setEmptyRectList(emptyRectList: List<Rect>?) {
        this.emptyRectList = emptyRectList
    }

    fun setFullRectList(fullRectList: List<Rect>) {
        this.fullRectList = fullRectList
    }
}
