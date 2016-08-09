/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package me.xiaopan.sketch.feature.zoom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.feature.zoom.gestures.OnScaleDragGestureListener;
import me.xiaopan.sketch.feature.zoom.gestures.ScaleDragGestureDetector;
import me.xiaopan.sketch.feature.zoom.gestures.ScaleDragGestureDetectorCompat;

public class ImageZoomer implements View.OnTouchListener, OnScaleDragGestureListener, ViewTreeObserver.OnGlobalLayoutListener, FlingTranslateRunner.FlingTranslateListener {
    public static final String NAME = "ImageZoomer";
    public static final float DEFAULT_MAX_SCALE = 15.0f;
    public static final float DEFAULT_MID_SCALE = 1.75f;
    public static final float DEFAULT_MIN_SCALE = 1.0f;
    public static final int DEFAULT_ZOOM_DURATION = 200;
    public static final int EDGE_NONE = -1;
    public static final int EDGE_LEFT = 0;
    public static final int EDGE_RIGHT = 1;
    public static final int EDGE_BOTH = 2;

    // incoming
    private Context context;
    private WeakReference<ImageView> imageViewWeakReference;

    // configurable options
    private int zoomDuration = DEFAULT_ZOOM_DURATION;
    private float baseRotation;
    private float minScale = DEFAULT_MIN_SCALE;
    private float midScale = DEFAULT_MID_SCALE;
    private float maxScale = DEFAULT_MAX_SCALE;
    private boolean zoomEnabled = true;
    private ScaleType scaleType = ScaleType.FIT_CENTER;
    private Interpolator zoomInterpolator = new AccelerateDecelerateInterpolator();

    // listeners
    private OnViewTapListener onViewTapListener;
    private OnSingleFlingListener onSingleFlingListener;
    private OnScaleChangeListener onScaleChangeListener;
    private ArrayList<OnMatrixChangedListener> onMatrixChangedListenerList;

    // run time required
    private int imageViewLeft, imageViewTop, imageViewRight, imageViewBottom;
    private int scrollEdge = EDGE_BOTH;
    private boolean allowParentInterceptOnEdge = true;
    private boolean blockParentIntercept;
    private GestureDetector tapGestureDetector;
    private FlingTranslateRunner currentFlingTranslateRunner;
    private ScaleDragGestureDetector scaleDragGestureDetector;
    private final Matrix baseMatrix = new Matrix();
    private final Matrix drawMatrix = new Matrix();
    private final Matrix suppMatrix = new Matrix();
    private final float[] matrixValues = new float[9];

    public ImageZoomer(ImageView imageView, boolean provideTouchEvent) {
        context = imageView.getContext();
        imageViewWeakReference = new WeakReference<ImageView>(imageView);

        // from ImageView get ScaleType
        scaleType = imageView.getScaleType();
        if (scaleType == ScaleType.MATRIX) {
            scaleType = ScaleType.FIT_CENTER;
        }

        // initialize ImageView
        imageView.setDrawingCacheEnabled(true);
        if (!provideTouchEvent) {
            imageView.setOnTouchListener(this);
        }
        setImageViewScaleTypeMatrix(imageView);

        // initialize
        tapGestureDetector = new GestureDetector(context, new TapListener(this));
        scaleDragGestureDetector = ScaleDragGestureDetectorCompat.newInstance(imageView.getContext(), this);
        baseRotation = 0.0f;

        // listening to the ImageView size changes
        ViewTreeObserver observer = imageView.getViewTreeObserver();
        if (observer != null) {
            observer.addOnGlobalLayoutListener(this);
        }
    }

    @SuppressWarnings("unused")
    public ImageZoomer(ImageView imageView) {
        this(imageView, false);
    }

    // TODO 嵌套ViewPager时滑动还是有问题，比如放大的时候就不能滑动了
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (!zoomEnabled || !hasDrawable()) {
            return false;
        }

        boolean handled = false;

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                // First, disable the Parent from intercepting the touch event
                ViewParent parent = v.getParent();
                if (parent != null) {
                    parent.requestDisallowInterceptTouchEvent(true);
                }

                // cancel fling
                cancelFling();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                // If the user has zoomed less than min scale, zoom back to min scale
                if (getScale() < minScale) {
                    RectF rect = getDisplayRect();
                    if (null != rect) {
                        v.post(new ZoomRunner(this, getScale(), minScale, rect.centerX(), rect.centerY()));
                        handled = true;
                    }
                }
                break;
        }

        if (scaleDragGestureDetector != null) {
            boolean wasScaling = scaleDragGestureDetector.isScaling();
            boolean wasDragging = scaleDragGestureDetector.isDragging();

            handled = scaleDragGestureDetector.onTouchEvent(event);

            boolean didntScale = !wasScaling && !scaleDragGestureDetector.isScaling();
            boolean didntDrag = !wasDragging && !scaleDragGestureDetector.isDragging();

            blockParentIntercept = didntScale && didntDrag;
        }

        // Check to see if the user double tapped
        if (tapGestureDetector != null && tapGestureDetector.onTouchEvent(event)) {
            handled = true;
        }

        return handled;
    }

    @Override
    public void onDrag(float dx, float dy) {
        ImageView imageView = getImageView();
        if (imageView == null) {
            return;
        }

        // Do not drag if we are already scaling
        if (scaleDragGestureDetector != null && scaleDragGestureDetector.isScaling()) {
            return;
        }

        if (Sketch.isDebugMode()) {
            Log.d(Sketch.TAG, NAME + ". " + String.format("onDrag: dx: %.2f. dy: %.2f", dx, dy));
        }

        suppMatrix.postTranslate(dx, dy);
        checkAndDisplayMatrix();

        /**
         * Here we decide whether to let the ImageView's parent to start taking
         * over the touch event.
         *
         * First we check whether this function is enabled. We never want the
         * parent to take over if we're scaling. We then check the edge we're
         * on, and the direction of the scroll (i.e. if we're pulling against
         * the edge, aka 'overscrolling', let the parent take over).
         */
        ViewParent parent = imageView.getParent();
        if (parent != null) {
            if (allowParentInterceptOnEdge && !scaleDragGestureDetector.isScaling() && !blockParentIntercept) {
                if (scrollEdge == EDGE_BOTH || (scrollEdge == EDGE_LEFT && dx >= 1f) || (scrollEdge == EDGE_RIGHT && dx <= -1f)) {
                    parent.requestDisallowInterceptTouchEvent(false);
                }
            } else {
                parent.requestDisallowInterceptTouchEvent(true);
            }
        }
    }

    @Override
    public void onFling(float startX, float startY, float velocityX, float velocityY) {
        currentFlingTranslateRunner = new FlingTranslateRunner(context, this);
        currentFlingTranslateRunner.fling((int) velocityX, (int) velocityY);

        if (onSingleFlingListener != null) {
            onSingleFlingListener.onFling(startX, startY, velocityX, velocityY);
        }
    }

    @Override
    public void onScale(float scaleFactor, float focusX, float focusY) {
        float currentScale = getScale();
        if ((currentScale < maxScale || scaleFactor < 1f) && (currentScale > minScale || scaleFactor > 1f)) {
            if (onScaleChangeListener != null) {
                onScaleChangeListener.onScaleChange(scaleFactor, focusX, focusY);
            }
            suppMatrix.postScale(scaleFactor, scaleFactor, focusX, focusY);
            checkAndDisplayMatrix();
        }
    }

    @Override
    public void onFlingTranslate(float dx, float dy) {
        suppMatrix.postTranslate(dx, dy);
        setImageViewMatrix(getDrawMatrix());
    }

    public void setScale(float scale, boolean animate) {
        ImageView imageView = getImageView();

        if (null != imageView) {
            setScale(scale, (imageView.getRight()) / 2, (imageView.getBottom()) / 2, animate);
        }
    }

    public void setScale(float scale, float focalX, float focalY, boolean animate) {
        ImageView imageView = getImageView();

        if (null != imageView) {
            // Check to see if the scale is within bounds
            if (scale < minScale || scale > maxScale) {
                Log.i(Sketch.TAG, NAME + ". Scale must be within the range of minScale and maxScale");
                return;
            }

            if (animate) {
                imageView.post(new ZoomRunner(this, getScale(), scale, focalX, focalY));
            } else {
                suppMatrix.setScale(scale, scale, focalX, focalY);
                checkAndDisplayMatrix();
            }
        }
    }

    private void setImageViewScaleTypeMatrix(ImageView imageView) {
        if (imageView != null && !ScaleType.MATRIX.equals(imageView.getScaleType())) {
            imageView.setScaleType(ScaleType.MATRIX);
        }
    }

    private boolean hasDrawable() {
        ImageView imageView = getImageView();
        return imageView != null && imageView.getDrawable() != null;
    }

    private void checkZoomLevels(float minZoom, float midZoom, float maxZoom) {
        if (minZoom >= midZoom) {
            throw new IllegalArgumentException(
                    "Minimum zoom has to be less than Medium zoom. Call setMinimumZoom() with a more appropriate value");
        } else if (midZoom >= maxZoom) {
            throw new IllegalArgumentException(
                    "Medium zoom has to be less than Maximum zoom. Call setMaximumZoom() with a more appropriate value");
        }
    }

    public ImageView getImageView() {
        if (imageViewWeakReference == null) {
            return null;
        }

        ImageView imageView = imageViewWeakReference.get();
        if (imageView == null) {
            Log.i(Sketch.TAG, NAME + ". ImageView no longer exists. You should not use this ImageAmplifier any more.");
            cleanup();
        }

        return imageView;
    }

    public float getScale() {
        return (float) Math.sqrt((float) Math.pow(getValue(suppMatrix, Matrix.MSCALE_X), 2) + (float) Math.pow(getValue(suppMatrix, Matrix.MSKEW_Y), 2));
    }

    @SuppressWarnings("unused")
    public void setScale(float scale) {
        setScale(scale, false);
    }

    public ScaleType getScaleType() {
        return scaleType;
    }

    public void setScaleType(ScaleType scaleType) {
        if (scaleType != null && scaleType != ScaleType.MATRIX && scaleType != this.scaleType) {
            this.scaleType = scaleType;
            update();
        }
    }

    /**
     * Helper method that 'unpacks' a Matrix and returns the required value
     *
     * @param matrix     - Matrix to unpack
     * @param whichValue - Which value from Matrix.M* to return
     * @return float - returned value
     */
    private float getValue(Matrix matrix, int whichValue) {
        matrix.getValues(matrixValues);
        return matrixValues[whichValue];
    }

    @SuppressWarnings("unused")
    public void getDisplayMatrix(Matrix matrix) {
        matrix.set(getDrawMatrix());
    }

    public int getDrawableWidth() {
        ImageView imageView = imageViewWeakReference.get();
        if (imageView == null) {
            return 0;
        }

        Drawable drawable = imageView.getDrawable();
        if (drawable == null) {
            return 0;
        }

        return drawable.getIntrinsicWidth();
    }

    public int getDrawableHeight() {
        ImageView imageView = imageViewWeakReference.get();
        if (imageView == null) {
            return 0;
        }

        Drawable drawable = imageView.getDrawable();
        if (drawable == null) {
            return 0;
        }

        return drawable.getIntrinsicHeight();
    }

    /**
     * Method should be private
     * Use {@link #getDisplayMatrix(Matrix)}
     */
    @Deprecated
    public Matrix getDrawMatrix() {
        drawMatrix.set(baseMatrix);
        drawMatrix.postConcat(suppMatrix);
        return drawMatrix;
    }

    @SuppressWarnings("unused")
    public boolean setDisplayMatrix(Matrix finalMatrix) {
        if (finalMatrix == null) {
            throw new IllegalArgumentException("Matrix cannot be null");
        }

        ImageView imageView = getImageView();
        if (null == imageView) {
            return false;
        }

        if (null == imageView.getDrawable()) {
            return false;
        }

        suppMatrix.set(finalMatrix);
        setImageViewMatrix(getDrawMatrix());
        checkMatrixBounds();

        return true;
    }

    @SuppressWarnings("unused")
    public void setBaseRotation(final float degrees) {
        baseRotation = degrees % 360;
        update();
        setRotationBy(baseRotation);
        checkAndDisplayMatrix();
    }

    @SuppressWarnings("unused")
    public boolean canZoom() {
        return zoomEnabled;
    }

    public void cleanup() {
        if (imageViewWeakReference == null) {
            return; // cleanup already done
        }

        final ImageView imageView = imageViewWeakReference.get();
        if (imageView != null) {
            // Remove this as a global layout listener
            ViewTreeObserver observer = imageView.getViewTreeObserver();
            if (observer != null && observer.isAlive()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    observer.removeOnGlobalLayoutListener(this);
                } else {
                    //noinspection deprecation
                    observer.removeGlobalOnLayoutListener(this);
                }
            }

            // Remove the ImageView's reference to this
            imageView.setOnTouchListener(null);

            // make sure a pending fling runnable won't be run
            cancelFling();
        }

        if (tapGestureDetector != null) {
            tapGestureDetector.setOnDoubleTapListener(null);
        }

        // Clear listeners too
        onMatrixChangedListenerList = null;
        onViewTapListener = null;
        onSingleFlingListener = null;
        onScaleChangeListener = null;

        // Finally, clear ImageView
        imageViewWeakReference = null;
    }

    @Override
    public void onGlobalLayout() {
        ImageView imageView = getImageView();
        if (imageView == null) {
            return;
        }

        if (zoomEnabled) {
            final int top = imageView.getTop();
            final int right = imageView.getRight();
            final int bottom = imageView.getBottom();
            final int left = imageView.getLeft();

            /**
             * We need to check whether the ImageView's bounds have changed.
             * This would be easier if we targeted API 11+ as we could just use
             * View.OnLayoutChangeListener. Instead we have to replicate the
             * work, keeping track of the ImageView's bounds and then checking
             * if the values change.
             */
            if (top != imageViewTop || bottom != imageViewBottom || left != imageViewLeft
                    || right != imageViewRight) {
                // Update our base matrix, as the bounds have changed
                updateBaseMatrix(imageView.getDrawable());

                // Update values as something has changed
                imageViewTop = top;
                imageViewRight = right;
                imageViewBottom = bottom;
                imageViewLeft = left;
            }
        } else {
            updateBaseMatrix(imageView.getDrawable());
        }
    }

    public void update() {
        ImageView imageView = getImageView();
        if (imageView == null) {
            return;
        }
        // TODO 优化最大、最小以及默认缩放比例，要根据图片的宽高以及ImageView的宽高来计算
        if (zoomEnabled) {
            // Make sure we using MATRIX Scale Type
            setImageViewScaleTypeMatrix(imageView);

            // Update the base matrix using the current drawable
            updateBaseMatrix(imageView.getDrawable());
        } else {
            // Reset the Matrix...
            resetMatrix();
        }

        // TODO: 16/8/9 初始化完成后会调用下onMatrixChangeListener
    }

    /**
     * Helper method that maps the supplied Matrix to the current Drawable
     *
     * @param matrix - Matrix to map Drawable against
     * @return RectF - Displayed Rectangle
     */
    private RectF getDisplayRect(Matrix matrix) {
        ImageView imageView = getImageView();

        if (null != imageView) {
            Drawable d = imageView.getDrawable();
            if (null != d) {
                RectF displayRect = new RectF(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
                matrix.mapRect(displayRect);
                return displayRect;
            }
        }
        return null;
    }

    public RectF getDisplayRect() {
        checkMatrixBounds();
        return getDisplayRect(getDrawMatrix());
    }

    public RectF getVisibleRect() {
        ImageView imageView = getImageView();
        if (imageView == null) {
            return null;
        }

        Drawable drawable = imageView.getDrawable();
        if (drawable == null || drawable.getIntrinsicWidth() == 0) {
            return null;
        }

        RectF displayRect = getDisplayRect();
        int viewWidth = imageView.getWidth();
        int viewHeight = imageView.getHeight();
        float displayWidth = displayRect.width();
        float displayHeight = displayRect.height();
        int drawableWidth = drawable.getIntrinsicWidth();

        float scale = displayWidth / drawableWidth;

        float left;
        float right;
        if (displayRect.left >= 0) {
            left = 0;
        } else {
            left = Math.abs(displayRect.left);
        }
        if (displayWidth >= viewWidth) {
            right = viewWidth + left;
        } else {
            right = displayRect.right - displayRect.left;
        }

        float top;
        float bottom;
        if (displayRect.top >= 0) {
            top = 0;
        } else {
            top = Math.abs(displayRect.top);
        }
        if (displayHeight >= viewHeight) {
            bottom = viewHeight + top;
        } else {
            bottom = displayRect.bottom - displayRect.top;
        }

        left /= scale;
        right /= scale;
        top /= scale;
        bottom /= scale;

//        srcRect.set(left, top, right, bottom);
//        return srcRect;
        return new RectF(left, top, right, bottom);
    }

    int getImageViewWidth(ImageView imageView) {
        if (null == imageView) {
            return 0;
        }
        return imageView.getWidth() - imageView.getPaddingLeft() - imageView.getPaddingRight();
    }

    int getImageViewHeight(ImageView imageView) {
        if (null == imageView) {
            return 0;
        }
        return imageView.getHeight() - imageView.getPaddingTop() - imageView.getPaddingBottom();
    }

    /**
     * Helper method that simply checks the Matrix, and then displays the result
     */
    private void checkAndDisplayMatrix() {
        if (checkMatrixBounds()) {
            setImageViewMatrix(getDrawMatrix());
        }
    }

    private boolean checkMatrixBounds() {
        final ImageView imageView = getImageView();
        if (null == imageView) {
            return false;
        }

        final RectF rect = getDisplayRect(getDrawMatrix());
        if (null == rect) {
            return false;
        }

        final float height = rect.height(), width = rect.width();
        float deltaX = 0, deltaY = 0;

        final int viewHeight = getImageViewHeight(imageView);
        if (height <= viewHeight) {
            switch (scaleType) {
                case FIT_START:
                    deltaY = -rect.top;
                    break;
                case FIT_END:
                    deltaY = viewHeight - height - rect.top;
                    break;
                default:
                    deltaY = (viewHeight - height) / 2 - rect.top;
                    break;
            }
        } else if (rect.top > 0) {
            deltaY = -rect.top;
        } else if (rect.bottom < viewHeight) {
            deltaY = viewHeight - rect.bottom;
        }

        final int viewWidth = getImageViewWidth(imageView);
        if (width <= viewWidth) {
            switch (scaleType) {
                case FIT_START:
                    deltaX = -rect.left;
                    break;
                case FIT_END:
                    deltaX = viewWidth - width - rect.left;
                    break;
                default:
                    deltaX = (viewWidth - width) / 2 - rect.left;
                    break;
            }
            scrollEdge = EDGE_BOTH;
        } else if (rect.left > 0) {
            scrollEdge = EDGE_LEFT;
            deltaX = -rect.left;
        } else if (rect.right < viewWidth) {
            deltaX = viewWidth - rect.right;
            scrollEdge = EDGE_RIGHT;
        } else {
            scrollEdge = EDGE_NONE;
        }

        // Finally actually translate the matrix
        suppMatrix.postTranslate(deltaX, deltaY);
        return true;
    }

    private void setImageViewMatrix(Matrix matrix) {
        ImageView imageView = getImageView();
        if (null != imageView) {

            checkImageViewScaleType();
            imageView.setImageMatrix(matrix);

            // Call MatrixChangedListener if needed
            if (onMatrixChangedListenerList != null && !onMatrixChangedListenerList.isEmpty()) {
                RectF displayRect = getDisplayRect(matrix);
                if (displayRect != null) {
                    for (int w = 0, size = onMatrixChangedListenerList.size(); w < size; w++) {
                        onMatrixChangedListenerList.get(w).onMatrixChanged(displayRect);
                    }
                }
            }
        }
    }

    private void checkImageViewScaleType() {
        ImageView imageView = getImageView();
        if (null != imageView && !ScaleType.MATRIX.equals(imageView.getScaleType())) {
            throw new IllegalStateException(
                    "The ImageView's ScaleType has been changed since attaching a PhotoViewAttacher. " +
                            "You should call setScaleType on the PhotoViewAttacher instead of on the ImageView");
        }
    }

    public float getMaximumScale() {
        return maxScale;
    }

    @SuppressWarnings("unused")
    public void setMaximumScale(float maximumScale) {
        checkZoomLevels(minScale, midScale, maximumScale);
        maxScale = maximumScale;
    }

    public float getMediumScale() {
        return midScale;
    }

    @SuppressWarnings("unused")
    public void setMediumScale(float mediumScale) {
        checkZoomLevels(minScale, mediumScale, maxScale);
        midScale = mediumScale;
    }

    public float getMinimumScale() {
        return minScale;
    }

    @SuppressWarnings("unused")
    public void setMinimumScale(float minimumScale) {
        checkZoomLevels(minimumScale, midScale, maxScale);
        minScale = minimumScale;
    }

    @SuppressWarnings("unused")
    public void setRotationTo(float degrees) {
        suppMatrix.setRotate(degrees % 360);
        checkAndDisplayMatrix();
    }

    public void setRotationBy(float degrees) {
        suppMatrix.postRotate(degrees % 360);
        checkAndDisplayMatrix();
    }

    @SuppressWarnings("unused")
    public void setOnSingleFlingListener(OnSingleFlingListener onSingleFlingListener) {
        this.onSingleFlingListener = onSingleFlingListener;
    }

    @SuppressWarnings("unused")
    public void setAllowParentInterceptOnEdge(boolean allowParentInterceptOnEdge) {
        this.allowParentInterceptOnEdge = allowParentInterceptOnEdge;
    }

    @SuppressWarnings("unused")
    public void setOnScaleChangeListener(OnScaleChangeListener onScaleChangeListener) {
        this.onScaleChangeListener = onScaleChangeListener;
    }

    public void addOnMatrixChangeListener(OnMatrixChangedListener listener) {
        if (listener != null) {
            if (onMatrixChangedListenerList == null) {
                onMatrixChangedListenerList = new ArrayList<OnMatrixChangedListener>(1);
            }
            onMatrixChangedListenerList.add(listener);
        }
    }

    @SuppressWarnings("unused")
    public void setZoomable(boolean zoomable) {
        zoomEnabled = zoomable;
        update();
    }

    @SuppressWarnings("unused")
    public void setZoomTransitionDuration(int milliseconds) {
        if (milliseconds < 0)
            milliseconds = DEFAULT_ZOOM_DURATION;
        this.zoomDuration = milliseconds;
    }

    @SuppressWarnings("unused")
    public Bitmap getVisibleRectangleBitmap() {
        ImageView imageView = getImageView();
        return imageView == null ? null : imageView.getDrawingCache();
    }

    /**
     * Resets the Matrix back to FIT_CENTER, and then displays it.s
     */
    private void resetMatrix() {
        suppMatrix.reset();
        setRotationBy(baseRotation);
        setImageViewMatrix(getDrawMatrix());
        checkMatrixBounds();
    }

    @SuppressWarnings("unused")
    public boolean isZoomEnabled() {
        return zoomEnabled;
    }

    int getZoomDuration() {
        return zoomDuration;
    }

    OnViewTapListener getOnViewTapListener() {
        return onViewTapListener;
    }

    @SuppressWarnings("unused")
    public void setOnViewTapListener(OnViewTapListener onViewTapListener) {
        this.onViewTapListener = onViewTapListener;
    }

    Interpolator getZoomInterpolator() {
        return zoomInterpolator;
    }

    /**
     * Set the zoom interpolator
     *
     * @param interpolator the zoom interpolator
     */
    @SuppressWarnings("unused")
    public void setZoomInterpolator(Interpolator interpolator) {
        zoomInterpolator = interpolator;
    }

    /**
     * Calculate Matrix for FIT_CENTER
     *
     * @param d - Drawable being displayed
     */
    private void updateBaseMatrix(Drawable d) {
        ImageView imageView = getImageView();
        if (null == imageView || null == d) {
            return;
        }

        final float viewWidth = getImageViewWidth(imageView);
        final float viewHeight = getImageViewHeight(imageView);
        final int drawableWidth = d.getIntrinsicWidth();
        final int drawableHeight = d.getIntrinsicHeight();

        baseMatrix.reset();

        final float widthScale = viewWidth / drawableWidth;
        final float heightScale = viewHeight / drawableHeight;

        if (scaleType == ScaleType.CENTER) {
            baseMatrix.postTranslate((viewWidth - drawableWidth) / 2F,
                    (viewHeight - drawableHeight) / 2F);

        } else if (scaleType == ScaleType.CENTER_CROP) {
            float scale = Math.max(widthScale, heightScale);
            baseMatrix.postScale(scale, scale);
            baseMatrix.postTranslate((viewWidth - drawableWidth * scale) / 2F,
                    (viewHeight - drawableHeight * scale) / 2F);

        } else if (scaleType == ScaleType.CENTER_INSIDE) {
            float scale = Math.min(1.0f, Math.min(widthScale, heightScale));
            baseMatrix.postScale(scale, scale);
            baseMatrix.postTranslate((viewWidth - drawableWidth * scale) / 2F,
                    (viewHeight - drawableHeight * scale) / 2F);

        } else {
            RectF mTempSrc = new RectF(0, 0, drawableWidth, drawableHeight);
            RectF mTempDst = new RectF(0, 0, viewWidth, viewHeight);

            if ((int) baseRotation % 180 != 0) {
                mTempSrc = new RectF(0, 0, drawableHeight, drawableWidth);
            }

            switch (scaleType) {
                case FIT_CENTER:
                    baseMatrix
                            .setRectToRect(mTempSrc, mTempDst, Matrix.ScaleToFit.CENTER);
                    break;

                case FIT_START:
                    baseMatrix.setRectToRect(mTempSrc, mTempDst, Matrix.ScaleToFit.START);
                    break;

                case FIT_END:
                    baseMatrix.setRectToRect(mTempSrc, mTempDst, Matrix.ScaleToFit.END);
                    break;

                case FIT_XY:
                    baseMatrix.setRectToRect(mTempSrc, mTempDst, Matrix.ScaleToFit.FILL);
                    break;

                default:
                    break;
            }
        }

        resetMatrix();
    }

    private void cancelFling() {
        if (currentFlingTranslateRunner != null) {
            currentFlingTranslateRunner.cancelFling();
            currentFlingTranslateRunner = null;
        }
    }

    public interface OnSingleFlingListener {
        boolean onFling(float startX, float startY, float velocityX, float velocityY);
    }

    public interface OnViewTapListener {
        void onViewTap(View view, float x, float y);
    }

    public interface OnMatrixChangedListener {
        void onMatrixChanged(RectF displayRect);
    }

    public interface OnScaleChangeListener {
        void onScaleChange(float scaleFactor, float focusX, float focusY);
    }
}