package me.xiaopan.sketch;

import android.graphics.Rect;
import android.widget.ImageView;

public interface ResizeCalculator {
    Result calculator(int originalImageWidth, int originalImageHeight, int targetImageWidth, int targetImageHeight, ImageView.ScaleType scaleType, boolean forceUseResize);

    String getIdentifier();

    StringBuilder appendIdentifier(StringBuilder builder);

    class Result{
        public int imageWidth;
        public int imageHeight;
        public Rect srcRect;
        public Rect destRect;
    }
}
