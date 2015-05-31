package me.xiaopan.sketch;

import android.graphics.Rect;
import android.widget.ImageView;

public class DefaultResizeCalculator implements ResizeCalculator{
    private static final String NAME = "DefaultResizeCalculator";

    @Override
    public String getIdentifier() {
        return NAME;
    }

    @Override
    public StringBuilder appendIdentifier(StringBuilder builder) {
        return builder.append(NAME);
    }

    @Override
    public Result calculator(int originalImageWidth, int originalImageHeight, int targetImageWidth, int targetImageHeight, ImageView.ScaleType scaleType, boolean forceUseResize){
        if(originalImageWidth == targetImageWidth && originalImageHeight == targetImageHeight){
            Result result = new Result();
            result.imageWidth = originalImageWidth;
            result.imageHeight = originalImageHeight;
            result.srcRect = new Rect(0, 0, originalImageWidth, originalImageHeight);
            result.destRect = result.srcRect;
            return result;
        }

        if(scaleType == null){
            scaleType = ImageView.ScaleType.FIT_CENTER;
        }

        int newImageWidth;
        int newImageHeight;
        if(forceUseResize){
            newImageWidth = targetImageWidth;
            newImageHeight = targetImageHeight;
        }else{
            int[] finalImageSize = scaleTargetSize(originalImageWidth, originalImageHeight, targetImageWidth, targetImageHeight);
            newImageWidth = finalImageSize[0];
            newImageHeight = finalImageSize[1];
        }
        Rect srcRect;
        Rect destRect = new Rect(0, 0, newImageWidth, newImageHeight);
        if(scaleType == ImageView.ScaleType.CENTER || scaleType == ImageView.ScaleType.CENTER_CROP || scaleType == ImageView.ScaleType.CENTER_INSIDE){
            srcRect = srcMappingCenterRect(originalImageWidth, originalImageHeight, newImageWidth, newImageHeight);
        }else if(scaleType == ImageView.ScaleType.FIT_START){
            srcRect = srcMappingStartRect(originalImageWidth, originalImageHeight, newImageWidth, newImageHeight);
        }else if(scaleType == ImageView.ScaleType.FIT_CENTER){
            srcRect = srcMappingCenterRect(originalImageWidth, originalImageHeight, newImageWidth, newImageHeight);
        }else if(scaleType == ImageView.ScaleType.FIT_END){
            srcRect = srcMappingEndRect(originalImageWidth, originalImageHeight, newImageWidth, newImageHeight);
        }else if(scaleType == ImageView.ScaleType.FIT_XY){
            srcRect = new Rect(0, 0, originalImageWidth, originalImageHeight);
        }else if(scaleType == ImageView.ScaleType.MATRIX){
            srcRect = srcMatrixRect(originalImageWidth, originalImageHeight, newImageWidth, newImageHeight);
        }else{
            srcRect = srcMappingCenterRect(originalImageWidth, originalImageHeight, newImageWidth, newImageHeight);
        }

        Result result = new Result();
        result.imageWidth = newImageWidth;
        result.imageHeight = newImageHeight;
        result.srcRect = srcRect;
        result.destRect = destRect;
        return result;
    }

    public static Rect srcMappingStartRect(int originalImageWidth, int originalImageHeight, int targetImageWidth, int targetImageHeight){
        float widthScale = (float)originalImageWidth/targetImageWidth;
        float heightScale = (float)originalImageHeight/targetImageHeight;
        float finalScale = widthScale<heightScale?widthScale:heightScale;
        int srcWidth = (int)(targetImageWidth*finalScale);
        int srcHeight = (int)(targetImageHeight*finalScale);
        int srcLeft = 0;
        int srcTop = 0;
        return new Rect(srcLeft, srcTop, srcLeft+srcWidth, srcTop+srcHeight);
    }

    public static Rect srcMappingCenterRect(int originalImageWidth, int originalImageHeight, int targetImageWidth, int targetImageHeight){
        float widthScale = (float)originalImageWidth/targetImageWidth;
        float heightScale = (float)originalImageHeight/targetImageHeight;
        float finalScale = widthScale<heightScale?widthScale:heightScale;
        int srcWidth = (int)(targetImageWidth*finalScale);
        int srcHeight = (int)(targetImageHeight*finalScale);
        int srcLeft = (originalImageWidth-srcWidth) / 2;
        int srcTop = (originalImageHeight-srcHeight) / 2;
        return new Rect(srcLeft, srcTop, srcLeft+srcWidth, srcTop+srcHeight);
    }

    public static Rect srcMappingEndRect(int originalImageWidth, int originalImageHeight, int targetImageWidth, int targetImageHeight){
        float widthScale = (float)originalImageWidth/targetImageWidth;
        float heightScale = (float)originalImageHeight/targetImageHeight;
        float finalScale = widthScale<heightScale?widthScale:heightScale;
        int srcWidth = (int)(targetImageWidth*finalScale);
        int srcHeight = (int)(targetImageHeight*finalScale);

        int srcLeft;
        int srcTop;
        if(originalImageWidth > originalImageHeight){
            srcLeft= originalImageWidth-srcWidth;
            srcTop= originalImageHeight-srcHeight;
        }else{
            srcLeft= originalImageWidth-srcWidth;
            srcTop= originalImageHeight-srcHeight;
        }
        return new Rect(srcLeft, srcTop, srcLeft+srcWidth, srcTop+srcHeight);
    }

    public static int[] scaleTargetSize(int originalImageWidth, int originalImageHeight, int targetImageWidth, int targetImageHeight){
        if(targetImageWidth > originalImageWidth || targetImageHeight > originalImageHeight){
            float scale = targetImageWidth-originalImageWidth > targetImageHeight-originalImageHeight ? (float) targetImageWidth/originalImageWidth : (float) targetImageHeight/originalImageHeight;
            targetImageWidth /= scale;
            targetImageHeight /= scale;
        }

        return new int[]{targetImageWidth, targetImageHeight};
    }

    public static Rect srcMatrixRect(int originalImageWidth, int originalImageHeight, int targetImageWidth, int targetImageHeight){
        if(originalImageWidth > targetImageWidth && originalImageHeight > targetImageHeight){
            return new Rect(0, 0, targetImageWidth, targetImageHeight);
        }else{
            float scale = targetImageWidth-originalImageWidth > targetImageHeight-originalImageHeight ? (float) targetImageWidth/originalImageWidth:(float) targetImageHeight/originalImageHeight;
            int srcWidth = (int)(targetImageWidth / scale);
            int srcHeight = (int)(targetImageHeight / scale);
            int srcLeft = 0;
            int srcTop = 0;
            return new Rect(srcLeft, srcTop, srcLeft+srcWidth, srcTop+srcHeight);
        }
    }
}
