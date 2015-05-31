package me.xiaopan.sketch;

import android.graphics.Bitmap;

public enum ImageFormat {
    PNG("image/png", Bitmap.Config.ARGB_8888, Bitmap.Config.ARGB_4444),
    JPEG("image/jpeg", Bitmap.Config.RGB_565, Bitmap.Config.RGB_565),
    GIF("image/gif", Bitmap.Config.ARGB_8888, Bitmap.Config.ARGB_4444),
    BMP("image/bmp", Bitmap.Config.RGB_565, Bitmap.Config.RGB_565),
    WEBP("image/webp", Bitmap.Config.ARGB_8888, Bitmap.Config.ARGB_4444);

    String mimeType;
    Bitmap.Config bestConfig;
    Bitmap.Config lowQualityConfig;

    ImageFormat(String mimeType, Bitmap.Config bestConfig, Bitmap.Config lowQualityConfig) {
        this.mimeType = mimeType;
        this.bestConfig = bestConfig;
        this.lowQualityConfig = lowQualityConfig;
    }

    public Bitmap.Config getConfig(boolean lowQualityImage) {
        return lowQualityImage?lowQualityConfig:bestConfig;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setBestConfig(Bitmap.Config bestConfig) {
        this.bestConfig = bestConfig;
    }

    public void setLowQualityConfig(Bitmap.Config lowQualityConfig) {
        this.lowQualityConfig = lowQualityConfig;
    }

    public boolean equals(String mimeType){
        return this.mimeType.equalsIgnoreCase(mimeType);
    }

    public static ImageFormat valueOfMimeType(String mimeType){
        for(ImageFormat imageFormat : values()){
            if(imageFormat.equals(mimeType)){
                return imageFormat;
            }
        }
        return null;
    }
}