package me.xiaopan.sketchsample.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import me.xiaopan.prl.PullRefreshLayout;
import me.xiaopan.sketchsample.R;

public class MyPullRefreshHeader extends LinearLayout implements PullRefreshLayout.PullRefreshHeader {
    private ImageView arrowImageView;
    private ProgressBar progressBar;
    private TextView hintTextView;

    private Matrix matrix;  // 用来旋转箭头
    private int maxDegrees; // 最大旋转角度
    private int triggerHeight = -1;    // 触发高度
    private float px = -1, py = -1; // 旋转中心的坐标
    private Status status; // 状态

    public MyPullRefreshHeader(Context context) {
        this(context, null);
    }

    public MyPullRefreshHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(getContext()).inflate(R.layout.refresh_header, this);
        arrowImageView = (ImageView) findViewWithTag("arrowImage");
        progressBar = (ProgressBar) findViewWithTag("progressBar");
        hintTextView = (TextView) findViewWithTag("hintText");

        adjustArrowImageViewPadding(arrowImageView);
        px = arrowImageView.getDrawable().getIntrinsicWidth() / 2;
        py = arrowImageView.getDrawable().getIntrinsicHeight() / 2;
        arrowImageView.setScaleType(ImageView.ScaleType.MATRIX);
        matrix = new Matrix();
        maxDegrees = 180;
        status = Status.NORMAL;

        onToNormal();

        Drawable arrayDrawable = context.getResources().getDrawable(R.drawable.ic_pull_down);
        arrayDrawable = arrayDrawable.mutate();
        arrayDrawable.setColorFilter(makeResetColorFilter(context.getResources().getColor(R.color.default_text_color_normal)));
        arrowImageView.setImageDrawable(arrayDrawable);
    }

    @Override
    public void onScroll(int distance) {
        // 如果当前正在刷新，就什么也不做
        if (status == Status.REFRESHING) {
            return;
        }

        // 计算旋转角度并旋转箭头
        float degrees;
        if (distance >= getTriggerHeight()) {
            degrees = maxDegrees;
        } else {
            degrees = ((float) distance / getTriggerHeight()) * maxDegrees;
        }
        matrix.setRotate(degrees, px, py);
        arrowImageView.setImageMatrix(matrix);
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public void onToWaitRefresh() {
        arrowImageView.setVisibility(VISIBLE);
        progressBar.setVisibility(INVISIBLE);
        hintTextView.setText("松手立即刷新");
    }

    @Override
    public void onToRefreshing() {
        arrowImageView.setVisibility(INVISIBLE);
        progressBar.setVisibility(VISIBLE);
        hintTextView.setText("正在刷新...");
    }

    @Override
    public void onToNormal() {
        arrowImageView.setVisibility(VISIBLE);
        progressBar.setVisibility(INVISIBLE);
        hintTextView.setText("下拉刷新");
    }

    @Override
    public int getTriggerHeight() {
        // 初始化触发高度
        if (triggerHeight == -1) {
            triggerHeight = getMeasuredHeight();
        }
        return triggerHeight;
    }

    /**
     * 调整箭头图片的内边距，保证在旋转箭头的时候不会被遮盖
     */
    private void adjustArrowImageViewPadding(ImageView arrowImageView) {
        if (arrowImageView.getDrawable() == null) {
            return;
        }

        int width = arrowImageView.getDrawable().getIntrinsicWidth();
        int height = arrowImageView.getDrawable().getIntrinsicHeight();
        int paddingLeft = arrowImageView.getPaddingLeft();
        int paddingTop = arrowImageView.getPaddingTop();
        int paddingRight = arrowImageView.getPaddingRight();
        int paddingBottom = arrowImageView.getPaddingBottom();
        if (width > height) {
            int offset = (width - height) / 2;
            if (paddingTop < offset) {
                paddingTop = offset;
            }
            if (paddingBottom < offset) {
                paddingBottom = offset;
            }
        } else if (width < height) {
            int offset = (height - width) / 2;
            if (paddingLeft < offset) {
                paddingLeft = offset;
            }
            if (paddingRight < offset) {
                paddingRight = offset;
            }
        }
        arrowImageView.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
    }

    /**
     * 创建一个可以改变颜色的ColorFilter
     *
     * @param color 透明度是没有用的
     * @return ColorMatrixColorFilter
     */
    public static ColorMatrixColorFilter makeResetColorFilter(int color) {
        float mRed = Color.red(color);
        float mGreen = Color.green(color);
        float mBlue = Color.blue(color);
        float[] src = new float[]{
                0, 0, 0, 0, mRed,
                0, 0, 0, 0, mGreen,
                0, 0, 0, 0, mBlue,
                0, 0, 0, 1, 0};
        ColorMatrix matrix = new ColorMatrix();
        matrix.set(src);
        return new ColorMatrixColorFilter(src);
    }
}