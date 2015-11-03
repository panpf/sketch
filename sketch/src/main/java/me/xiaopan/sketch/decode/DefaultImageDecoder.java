/*
 * Copyright (C) 2013 Peng fei Pan <sky@xiaopan.me>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.xiaopan.sketch.decode;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Point;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import me.xiaopan.sketch.ImageFormat;
import me.xiaopan.sketch.LoadRequest;
import me.xiaopan.sketch.MaxSize;
import me.xiaopan.sketch.RecycleGifDrawable;
import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.UriScheme;
import me.xiaopan.sketch.util.SketchUtils;

/**
 * 默认的图片解码器
 */
public class DefaultImageDecoder implements ImageDecoder {
    private static final String NAME = "DefaultImageDecoder";

    @Override
	public Object decode(LoadRequest loadRequest){
        try{
            if(loadRequest.getUriScheme() == UriScheme.HTTP || loadRequest.getUriScheme() == UriScheme.HTTPS){
                return decodeHttpOrHttps(loadRequest);
            }else if(loadRequest.getUriScheme() == UriScheme.FILE){
                return decodeFile(loadRequest);
            }else if(loadRequest.getUriScheme() == UriScheme.CONTENT){
                return decodeContent(loadRequest);
            }else if(loadRequest.getUriScheme() == UriScheme.ASSET){
                return decodeAsset(loadRequest);
            }else if(loadRequest.getUriScheme() == UriScheme.DRAWABLE){
                return decodeDrawable(loadRequest);
            }else{
                return null;
            }
        }catch(Throwable e){
            e.printStackTrace();
            return null;
        }
	}

    @Override
    public String getIdentifier() {
        return NAME;
    }

    @Override
    public StringBuilder appendIdentifier(StringBuilder builder) {
        return builder.append(NAME);
    }

    public Object decodeHttpOrHttps(LoadRequest loadRequest){
        File cacheFile = loadRequest.getCacheFile();
        if(cacheFile != null && cacheFile.exists()){
            return decodeFromHelper(loadRequest, new CacheFileDecodeHelper(cacheFile, loadRequest));
        }

        byte[] imageData = loadRequest.getImageData();
        if (imageData != null && imageData.length > 0){
            return decodeFromHelper(loadRequest, new ByteArrayDecodeHelper(imageData, loadRequest));
        }

        return null;
    }

    public Object decodeFile(LoadRequest loadRequest){
        if(loadRequest.isLocalApkFile() && loadRequest.getCacheFile() != null){
            return decodeFromHelper(loadRequest, new CacheFileDecodeHelper(loadRequest.getCacheFile(), loadRequest));
        }else{
            return decodeFromHelper(loadRequest, new FileDecodeHelper(new File(loadRequest.getUri()), loadRequest));
        }
    }

    public Object decodeContent(LoadRequest loadRequest){
        return decodeFromHelper(loadRequest, new ContentDecodeHelper(Uri.parse(loadRequest.getUri()), loadRequest));
    }

    public Object decodeAsset(LoadRequest loadRequest){
        return decodeFromHelper(loadRequest, new AssetsDecodeHelper(UriScheme.ASSET.crop(loadRequest.getUri()), loadRequest));
    }

    public Object decodeDrawable(LoadRequest loadRequest){
        return decodeFromHelper(loadRequest, new DrawableDecodeHelper(Integer.valueOf(UriScheme.DRAWABLE.crop(loadRequest.getUri())), loadRequest));
    }

    public static Object decodeFromHelper(LoadRequest loadRequest, DecodeHelper decodeHelper){
        // just decode bounds
        Options options = new Options();
        options.inJustDecodeBounds = true;
        decodeHelper.decode(options);
        options.inJustDecodeBounds = false;

        // setup best bitmap config by MimeType
        loadRequest.setMimeType(options.outMimeType);
        ImageFormat imageFormat = ImageFormat.valueOfMimeType(options.outMimeType);
        if(imageFormat != null){
            options.inPreferredConfig = imageFormat.getConfig(loadRequest.isLowQualityImage());
        }

        // decode gif image
        if(imageFormat != null && imageFormat == ImageFormat.GIF && loadRequest.isDecodeGifImage()){
            try {
                return decodeHelper.getGifDrawable();
            }catch (UnsatisfiedLinkError e){
                Log.e(Sketch.TAG, "Didn't find “libpl_droidsonroids_gif.so” file, unable to process the GIF images, has automatically according to the common image decoding, and has set up a closed automatically decoding GIF image feature. If you need to decode the GIF figure please go to “https://github.com/xiaopansky/sketch” to download “libpl_droidsonroids_gif.so” file and put in your project");
                loadRequest.getSketch().getConfiguration().setDecodeGifImage(false);
                e.printStackTrace();
            }catch (ExceptionInInitializerError e){
                Log.e(Sketch.TAG, "Didn't find “libpl_droidsonroids_gif.so” file, unable to process the GIF images, has automatically according to the common image decoding, and has set up a closed automatically decoding GIF image feature. If you need to decode the GIF figure please go to “https://github.com/xiaopansky/sketch” to download “libpl_droidsonroids_gif.so” file and put in your project");
                loadRequest.getSketch().getConfiguration().setDecodeGifImage(false);
                e.printStackTrace();
            }catch (Throwable e){
                Log.e(Sketch.TAG, "When decoding GIF figure some unknown exception, has shut down automatically GIF picture decoding function");
                loadRequest.getSketch().getConfiguration().setDecodeGifImage(false);
                e.printStackTrace();
            }
        }

        // decode normal image
        Bitmap bitmap = null;
        Point originalSize = new Point(options.outWidth, options.outHeight);
        if(options.outWidth != 1 && options.outHeight != 1){
            // calculate inSampleSize
            MaxSize maxSize = loadRequest.getMaxSize();
            if (maxSize != null) {
                options.inSampleSize = loadRequest.getSketch().getConfiguration().getImageSizeCalculator().calculateInSampleSize(options.outWidth, options.outHeight, maxSize.getWidth(), maxSize.getHeight());
            }

            // Decoding and exclude the width or height of 1 pixel image
            bitmap = decodeHelper.decode(options);
            if(bitmap != null && (bitmap.getWidth() == 1 || bitmap.getHeight() == 1)){
                if(Sketch.isDebugMode()){
                    Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "bitmap width or height is 1px", " - ", "ImageSize: ", originalSize.x, "x", originalSize.y, " - ", "BitmapSize: ", bitmap.getWidth(), "x", bitmap.getHeight(), " - ", loadRequest.getName()));
                }
                bitmap.recycle();
                bitmap = null;
            }
        }else{
            if(Sketch.isDebugMode()) {
                Log.e(Sketch.TAG, SketchUtils.concat(NAME, " - ", "image width or height is 1px", " - ", "ImageSize: ", originalSize.x, "x", originalSize.y, " - ", loadRequest.getName()));
            }
        }

        // Results the callback
        if(bitmap != null && !bitmap.isRecycled()){
            decodeHelper.onDecodeSuccess(bitmap, originalSize, options.inSampleSize);
        }else{
            bitmap = null;
            decodeHelper.onDecodeFailed();
        }

        return bitmap;
    }

    /**
     * 解码监听器
     */
    public interface DecodeHelper {
        /**
         * 解码
         * @param options 解码选项
         */
        Bitmap decode(BitmapFactory.Options options);

        /**
         * 解码成功
         */
        void onDecodeSuccess(Bitmap bitmap, Point originalSize, int inSampleSize);

        /**
         * 解码失败
         */
        void onDecodeFailed();

        /**
         * 获取GIF图
         */
        RecycleGifDrawable getGifDrawable();
    }

    public static class AssetsDecodeHelper implements DecodeHelper {
        private static final String NAME = "AssetsDecodeHelper";
        private String assetsFilePath;
        private LoadRequest loadRequest;

        public AssetsDecodeHelper(String assetsFilePath, LoadRequest loadRequest) {
            this.assetsFilePath = assetsFilePath;
            this.loadRequest = loadRequest;
        }

        @Override
        public Bitmap decode(BitmapFactory.Options options) {
            InputStream inputStream = null;
            try {
                inputStream = loadRequest.getSketch().getConfiguration().getContext().getAssets().open(assetsFilePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Bitmap bitmap = null;
            if(inputStream != null){
                bitmap = BitmapFactory.decodeStream(inputStream, null, options);
                try {
                    inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return bitmap;
        }

        @Override
        public void onDecodeSuccess(Bitmap bitmap, Point originalSize, int inSampleSize) {
            if(Sketch.isDebugMode()){
                StringBuilder stringBuilder = new StringBuilder(NAME)
                        .append(" - ").append("decodeSuccess");
                if(bitmap != null && loadRequest.getMaxSize() != null){
                    stringBuilder.append(" - ").append("originalSize").append("=").append(originalSize.x).append("x").append(originalSize.y);
                    stringBuilder.append(", ").append("targetSize").append("=").append(loadRequest.getMaxSize().getWidth()).append("x").append(loadRequest.getMaxSize().getHeight());
                    stringBuilder.append(", ").append("inSampleSize").append("=").append(inSampleSize);
                    stringBuilder.append(", ").append("finalSize").append("=").append(bitmap.getWidth()).append("x").append(bitmap.getHeight());
                }else{
                    stringBuilder.append(" - ").append("unchanged");
                }
                stringBuilder.append(" - ").append(loadRequest.getName());
                Log.d(Sketch.TAG, stringBuilder.toString());
            }
        }

        @Override
        public void onDecodeFailed() {
            if(Sketch.isDebugMode()){
                Log.e(Sketch.TAG, SketchUtils.concat(NAME, " - ", "decode failed", " - ", assetsFilePath));
            }
        }

        @Override
        public RecycleGifDrawable getGifDrawable() {
            try {
                return new RecycleGifDrawable(loadRequest.getSketch().getConfiguration().getContext().getAssets(), assetsFilePath);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public static class ByteArrayDecodeHelper implements DecodeHelper {
        private static final String NAME = "ByteArrayDecodeHelper";
        private byte[] data;
        private LoadRequest loadRequest;

        public ByteArrayDecodeHelper(byte[] data, LoadRequest loadRequest) {
            this.data = data;
            this.loadRequest = loadRequest;
        }

        @Override
        public Bitmap decode(BitmapFactory.Options options) {
            return BitmapFactory.decodeByteArray(data, 0, data.length, options);
        }

        @Override
        public void onDecodeSuccess(Bitmap bitmap, Point originalSize, int inSampleSize) {
            if(Sketch.isDebugMode()){
                StringBuilder stringBuilder = new StringBuilder(NAME)
                        .append(" - ").append("decodeSuccess");
                if(bitmap != null && loadRequest.getMaxSize() != null){
                    stringBuilder.append(" - ").append("originalSize").append("=").append(originalSize.x).append("x").append(originalSize.y);
                    stringBuilder.append(", ").append("targetSize").append("=").append(loadRequest.getMaxSize().getWidth()).append("x").append(loadRequest.getMaxSize().getHeight());
                    stringBuilder.append(", ").append("inSampleSize").append("=").append(inSampleSize);
                    stringBuilder.append(", ").append("finalSize").append("=").append(bitmap.getWidth()).append("x").append(bitmap.getHeight());
                }else{
                    stringBuilder.append(" - ").append("unchanged");
                }
                stringBuilder.append(" - ").append(loadRequest.getName());
                Log.d(Sketch.TAG, stringBuilder.toString());
            }
        }

        @Override
        public void onDecodeFailed() {
            if(Sketch.isDebugMode()){
                Log.e(Sketch.TAG, SketchUtils.concat(NAME, " - ", "decode failed", " - ", loadRequest.getName()));
            }
        }

        @Override
        public RecycleGifDrawable getGifDrawable() {
            try {
                return new RecycleGifDrawable(data);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public static class CacheFileDecodeHelper implements DecodeHelper {
        private static final String NAME = "CacheFileDecodeHelper";
        private File file;
        private LoadRequest loadRequest;

        public CacheFileDecodeHelper(File file, LoadRequest loadRequest) {
            this.file = file;
            this.loadRequest = loadRequest;
        }

        @Override
        public Bitmap decode(BitmapFactory.Options options) {
            if(!file.canRead()){
                if(Sketch.isDebugMode()){
                    Log.e(Sketch.TAG, SketchUtils.concat(NAME, " - ", "can not read", " - ", file.getPath()));
                }
                return null;
            }

            return BitmapFactory.decodeFile(file.getPath(), options);
        }

        @Override
        public void onDecodeSuccess(Bitmap bitmap, Point originalSize, int inSampleSize) {
            if(!file.setLastModified(System.currentTimeMillis())){
                if(Sketch.isDebugMode()){
                    Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "update last modified failed", " - ", file.getPath()));
                }
            }
            if(Sketch.isDebugMode()){
                StringBuilder stringBuilder = new StringBuilder(NAME)
                        .append(" - ").append("decodeSuccess");
                if(bitmap != null && loadRequest.getMaxSize() != null){
                    stringBuilder.append(" - ").append("originalSize").append("=").append(originalSize.x).append("x").append(originalSize.y);
                    stringBuilder.append(", ").append("targetSize").append("=").append(loadRequest.getMaxSize().getWidth()).append("x").append(loadRequest.getMaxSize().getHeight());
                    stringBuilder.append(", ").append("inSampleSize").append("=").append(inSampleSize);
                    stringBuilder.append(", ").append("finalSize").append("=").append(bitmap.getWidth()).append("x").append(bitmap.getHeight());
                }else{
                    stringBuilder.append(" - ").append("unchanged");
                }
                stringBuilder.append(" - ").append(loadRequest.getName());
                Log.d(Sketch.TAG, stringBuilder.toString());
            }
        }

        @Override
        public void onDecodeFailed() {
            if(Sketch.isDebugMode()){
                StringBuilder logContent = new StringBuilder(NAME);
                logContent.append(" - ").append("decode failed");
                logContent.append(", ").append("filePath").append("=").append(file.getPath());
                if(file.exists()){
                    logContent.append(",  ").append("fileLength").append("=").append(file.length());
                }
                logContent.append(",  ").append("imageUri").append("=").append(loadRequest.getUri());
                Log.e(Sketch.TAG, logContent.toString());
            }
            if(!file.delete()){
                if(Sketch.isDebugMode()){
                    Log.e(Sketch.TAG, SketchUtils.concat(NAME, " - ", "delete damaged disk cache file failed", " - ", file.getPath()));
                }
            }
        }

        @Override
        public RecycleGifDrawable getGifDrawable() {
            try {
                return new RecycleGifDrawable(new RandomAccessFile(file.getPath(), "r").getFD());
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public static class DrawableDecodeHelper implements DecodeHelper {
        private static final String NAME = "DrawableDecodeHelper";
        private int drawableId;
        private LoadRequest loadRequest;

        public DrawableDecodeHelper(int drawableId, LoadRequest loadRequest) {
            this.drawableId = drawableId;
            this.loadRequest = loadRequest;
        }

        @Override
        public Bitmap decode(BitmapFactory.Options options) {
            return BitmapFactory.decodeResource(loadRequest.getSketch().getConfiguration().getContext().getResources(), drawableId, options);
        }

        @Override
        public void onDecodeSuccess(Bitmap bitmap, Point originalSize, int inSampleSize) {
            if(Sketch.isDebugMode()){
                StringBuilder stringBuilder = new StringBuilder(NAME)
                        .append(" - ").append("decodeSuccess");
                if(bitmap != null && loadRequest.getMaxSize() != null){
                    stringBuilder.append(" - ").append("originalSize").append("=").append(originalSize.x).append("x").append(originalSize.y);
                    stringBuilder.append(", ").append("targetSize").append("=").append(loadRequest.getMaxSize().getWidth()).append("x").append(loadRequest.getMaxSize().getHeight());
                    stringBuilder.append(",  ").append("inSampleSize").append("=").append(inSampleSize);
                    stringBuilder.append(",  ").append("finalSize").append("=").append(bitmap.getWidth()).append("x").append(bitmap.getHeight());
                }else{
                    stringBuilder.append(" - ").append("unchanged");
                }
                stringBuilder.append(" - ").append(loadRequest.getName());
                Log.d(Sketch.TAG, stringBuilder.toString());
            }
        }

        @Override
        public void onDecodeFailed() {
            if(Sketch.isDebugMode()){
                Log.e(Sketch.TAG, SketchUtils.concat(NAME, " - ", "decode failed", " - ", String.valueOf(drawableId)));
            }
        }

        @Override
        public RecycleGifDrawable getGifDrawable() {
            try {
                return new RecycleGifDrawable(loadRequest.getSketch().getConfiguration().getContext().getResources(), drawableId);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public static class FileDecodeHelper implements DecodeHelper {
        private static final String NAME = "FileDecodeHelper";
        private File file;
        private LoadRequest loadRequest;

        public FileDecodeHelper(File file, LoadRequest loadRequest) {
            this.file = file;
            this.loadRequest = loadRequest;
        }

        @Override
        public Bitmap decode(BitmapFactory.Options options) {
            if(file.canRead()){
                return BitmapFactory.decodeFile(file.getPath(), options);
            }else{
                if(Sketch.isDebugMode()){
                    Log.e(Sketch.TAG, SketchUtils.concat(NAME, " - ", "can not read", " - ", file.getPath()));
                }
                return null;
            }
        }

        @Override
        public void onDecodeSuccess(Bitmap bitmap, Point originalSize, int inSampleSize) {
            if(Sketch.isDebugMode()){
                StringBuilder stringBuilder = new StringBuilder(NAME)
                        .append(" - "+"decodeSuccess");
                if(bitmap != null && loadRequest.getMaxSize() != null){
                    stringBuilder.append(" - ").append("originalSize").append("=").append(originalSize.x).append("x").append(originalSize.y);
                    stringBuilder.append(", ").append("targetSize").append("=").append(loadRequest.getMaxSize().getWidth()).append("x").append(loadRequest.getMaxSize().getHeight());
                    stringBuilder.append(", ").append("inSampleSize").append("=").append(inSampleSize);
                    stringBuilder.append(", ").append("finalSize").append("=").append(bitmap.getWidth()).append("x").append(bitmap.getHeight());
                }else{
                    stringBuilder.append(" - ").append("unchanged");
                }
                stringBuilder.append(" - ").append(loadRequest.getName());
                Log.d(Sketch.TAG, stringBuilder.toString());
            }
        }

        @Override
        public void onDecodeFailed() {
            if(Sketch.isDebugMode()){
                StringBuilder log = new StringBuilder(NAME);
                log.append(" - ").append("decode failed");
                log.append(", ").append("filePath").append("=").append(file.getPath());
                if(file.exists()){
                    log.append(", ").append("fileLength").append("=").append(file.length());
                }
                Log.e(Sketch.TAG, log.toString());
            }
        }

        @Override
        public RecycleGifDrawable getGifDrawable() {
            try {
                return new RecycleGifDrawable(new RandomAccessFile(file.getPath(), "r").getFD());
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public static class ContentDecodeHelper implements DecodeHelper {
        private static final String NAME = "ContentDecodeHelper";
        private Uri contentUri;
        private LoadRequest loadRequest;

        public ContentDecodeHelper(Uri contentUri, LoadRequest loadRequest) {
            this.contentUri = contentUri;
            this.loadRequest = loadRequest;
        }

        @Override
        public Bitmap decode(BitmapFactory.Options options) {
            InputStream inputStream = null;
            try {
                inputStream = loadRequest.getSketch().getConfiguration().getContext().getContentResolver().openInputStream(contentUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Bitmap bitmap = null;
            if(inputStream != null){
                bitmap = BitmapFactory.decodeStream(inputStream, null, options);
                try {
                    inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return bitmap;
        }

        @Override
        public void onDecodeSuccess(Bitmap bitmap, Point originalSize, int inSampleSize) {
            if(Sketch.isDebugMode()){
                StringBuilder stringBuilder = new StringBuilder(NAME)
                        .append(" - ").append("decodeSuccess");
                if(bitmap != null && loadRequest.getMaxSize() != null){
                    stringBuilder.append(" - ").append("originalSize").append("=").append(originalSize.x).append("x").append(originalSize.y);
                    stringBuilder.append(", ").append("targetSize").append("=").append(loadRequest.getMaxSize().getWidth()).append("x").append(loadRequest.getMaxSize().getHeight());
                    stringBuilder.append(", ").append("inSampleSize").append("=").append(inSampleSize);
                    stringBuilder.append(", ").append("finalSize").append("=").append(bitmap.getWidth()).append("x").append(bitmap.getHeight());
                }else{
                    stringBuilder.append(" - ").append("unchanged");
                }
                stringBuilder.append(" - ").append(loadRequest.getName());
                Log.d(Sketch.TAG, stringBuilder.toString());
            }
        }

        @Override
        public void onDecodeFailed() {
            if(Sketch.isDebugMode()){
                Log.e(Sketch.TAG, SketchUtils.concat(NAME, " - ", "decode failed", " - ", contentUri.toString()));
            }
        }

        @Override
        public RecycleGifDrawable getGifDrawable() {
            try {
                return new RecycleGifDrawable(loadRequest.getSketch().getConfiguration().getContext().getContentResolver(), contentUri);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}