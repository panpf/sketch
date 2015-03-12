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

package me.xiaopan.android.spear.decode;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import me.xiaopan.android.spear.Spear;
import me.xiaopan.android.spear.request.LoadRequest;
import me.xiaopan.android.spear.util.ImageScheme;
import me.xiaopan.android.spear.util.ImageSize;

/**
 * 默认的位图解码器
 */
public class DefaultImageDecoder implements ImageDecoder {

    @Override
	public Bitmap decode(LoadRequest loadRequest){
        if(loadRequest.getImageScheme() == ImageScheme.HTTP || loadRequest.getImageScheme() == ImageScheme.HTTPS){
            return decodeHttpOrHttps(loadRequest);
        }else if(loadRequest.getImageScheme() == ImageScheme.FILE){
            return decodeFile(loadRequest);
        }else if(loadRequest.getImageScheme() == ImageScheme.CONTENT){
            return decodeContent(loadRequest);
        }else if(loadRequest.getImageScheme() == ImageScheme.ASSETS){
            return decodeAssets(loadRequest);
        }else if(loadRequest.getImageScheme() == ImageScheme.DRAWABLE){
            return decodeDrawable(loadRequest);
        }else{
            return null;
        }
	}

    public Bitmap decodeHttpOrHttps(LoadRequest loadRequest){
        if(loadRequest.getCacheFile() != null && loadRequest.getCacheFile().exists()){
            return decodeFromHelper(loadRequest, new CacheFileDecodeHelper(loadRequest.getCacheFile(), loadRequest));
        }else if(loadRequest.getImageData() != null && loadRequest.getImageData().length > 0){
            return decodeFromHelper(loadRequest, new ByteArrayDecodeHelper(loadRequest.getImageData(), loadRequest));
        }else{
            return null;
        }
    }

    public Bitmap decodeFile(LoadRequest loadRequest){
        String fileNameSuffix = null;
        int lastIndex = loadRequest.getUri().lastIndexOf(".");
        if(lastIndex > -1){
            fileNameSuffix = loadRequest.getUri().substring(lastIndex);
        }

        if(".apk".equalsIgnoreCase(fileNameSuffix)){
            return decodeIconFromApk(loadRequest.getSpear().getConfiguration().getContext(), loadRequest.getUri());
        }else{
            return decodeFromHelper(loadRequest, new FileDecodeHelper(new File(loadRequest.getUri()), loadRequest));
        }
    }

    public Bitmap decodeContent(LoadRequest loadRequest){
        return decodeFromHelper(loadRequest, new ContentDecodeHelper(loadRequest.getUri(), loadRequest));
    }

    public Bitmap decodeAssets(LoadRequest loadRequest){
        return decodeFromHelper(loadRequest, new AssetsDecodeHelper(ImageScheme.ASSETS.crop(loadRequest.getUri()), loadRequest));
    }

    public Bitmap decodeDrawable(LoadRequest loadRequest){
        return decodeFromHelper(loadRequest, new DrawableDecodeHelper(ImageScheme.DRAWABLE.crop(loadRequest.getUri()), loadRequest));
    }

    public static Bitmap decodeFromHelper(LoadRequest loadRequest, DecodeHelper decodeHelper){
        ImageSize maxsize = loadRequest.getMaxsize();
        Bitmap bitmap = null;
        Point originalSize = null;
        int inSampleSize = 1;

        if(maxsize != null){
            // 只解码宽高
            Options options = new Options();
            options.inJustDecodeBounds = true;
            decodeHelper.onDecode(options);
            if(!(options.outWidth == 1 && options.outHeight == 1)){
                originalSize = new Point(options.outWidth, options.outHeight);

                // 计算缩放倍数
                inSampleSize = loadRequest.getSpear().getConfiguration().getImageSizeCalculator().calculateInSampleSize(options.outWidth, options.outHeight, maxsize.getWidth(), maxsize.getHeight());
                options.inSampleSize = inSampleSize;

                // 再次解码
                options.inJustDecodeBounds = false;
                bitmap = decodeHelper.onDecode(options);
            }
        }else{
            bitmap = decodeHelper.onDecode(null);
            if(bitmap != null){
                if(!(bitmap.getWidth()==1 && bitmap.getHeight() == 1)){
                    originalSize = new Point(bitmap.getWidth(), bitmap.getHeight());
                }else{
                    if(Spear.isDebugMode()){
                        Log.e(Spear.TAG, "recycle bitmap@"+Integer.toHexString(bitmap.hashCode())+"（1x1 Image）");
                    }
                    bitmap.recycle();
                    bitmap = null;
                }
            }
        }

        // 回调
        if(bitmap != null && !bitmap.isRecycled() && originalSize != null){
            decodeHelper.onDecodeSuccess(bitmap, originalSize, inSampleSize);
        }else{
            bitmap = null;
            decodeHelper.onDecodeFailure();
        }

        return bitmap;
    }

    /**
     * 解压APK的图标
     * @param context 上下文
     * @param apkFilePath APK文件的位置
     * @return  APK的图标
     */
    public static Bitmap decodeIconFromApk(Context context, String apkFilePath){
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo = packageManager.getPackageArchiveInfo(apkFilePath, PackageManager.GET_ACTIVITIES);
        if(packageInfo == null){
            return null;
        }

        packageInfo.applicationInfo.sourceDir = apkFilePath;
        packageInfo.applicationInfo.publicSourceDir = apkFilePath;

        Drawable drawable = packageManager.getApplicationIcon(packageInfo.applicationInfo);
        if(drawable == null){
            return null;
        }
        if(drawable instanceof BitmapDrawable && ((BitmapDrawable) drawable).getBitmap() == ((BitmapDrawable) packageManager.getDefaultActivityIcon()).getBitmap()){
            if(Spear.isDebugMode()){
                Log.d(Spear.TAG, "没有取到图标 "+apkFilePath);
            }
            return null;
        }
        return drawableToBitmap(drawable);
    }

    /**
     * Drawable转成Bitmap
     * @param drawable drawable
     * @return bitmap
     */
    public static Bitmap drawableToBitmap(Drawable drawable){
        if(drawable == null ){
            return null;
        }else if(drawable instanceof BitmapDrawable){
            return ((BitmapDrawable) drawable).getBitmap();
        }else{
            if(drawable.getIntrinsicWidth() == 0 || drawable.getIntrinsicHeight() == 0){
                return null;
            }

            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.draw(canvas);
            return bitmap;
        }
    }

    /**
     * 解码监听器
     */
    public interface DecodeHelper {
        /**
         * 解码
         * @param options 解码选项
         */
        Bitmap onDecode(BitmapFactory.Options options);

        /**
         * 解码成功
         */
        void onDecodeSuccess(Bitmap bitmap, Point originalSize, int inSampleSize);

        /**
         * 解码失败
         */
        void onDecodeFailure();
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
        public Bitmap onDecode(BitmapFactory.Options options) {
            InputStream inputStream = null;
            try {
                inputStream = loadRequest.getSpear().getConfiguration().getContext().getAssets().open(assetsFilePath);
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
            StringBuilder stringBuilder = new StringBuilder(NAME)
                    .append("；").append("解码成功");
            if(bitmap != null && loadRequest.getMaxsize() != null){
                stringBuilder.append("；").append("原始尺寸").append("=").append(originalSize.x).append("x").append(originalSize.y);
                stringBuilder.append("；").append("目标尺寸").append("=").append(loadRequest.getMaxsize().getWidth()).append("x").append(loadRequest.getMaxsize().getHeight());
                stringBuilder.append("；").append("缩放比例").append("=").append(inSampleSize);
                stringBuilder.append("；").append("最终尺寸").append("=").append(bitmap.getWidth()).append("x").append(bitmap.getHeight());
            }else{
                stringBuilder.append("；").append("未缩放");
            }
            stringBuilder.append("；").append(loadRequest.getName());
            Log.d(Spear.TAG, stringBuilder.toString());
        }

        @Override
        public void onDecodeFailure() {
            if(Spear.isDebugMode()){
                Log.e(Spear.TAG, NAME + "；" + "解码失败" + "；" + assetsFilePath);
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
        public Bitmap onDecode(BitmapFactory.Options options) {
            return BitmapFactory.decodeByteArray(data, 0, data.length, options);
        }

        @Override
        public void onDecodeSuccess(Bitmap bitmap, Point originalSize, int inSampleSize) {
            StringBuilder stringBuilder = new StringBuilder(NAME)
                    .append("；").append("解码成功");
            if(bitmap != null && loadRequest.getMaxsize() != null){
                stringBuilder.append("；").append("原始尺寸").append("=").append(originalSize.x).append("x").append(originalSize.y);
                stringBuilder.append("；").append("目标尺寸").append("=").append(loadRequest.getMaxsize().getWidth()).append("x").append(loadRequest.getMaxsize().getHeight());
                stringBuilder.append("；").append("缩放比例").append("=").append(inSampleSize);
                stringBuilder.append("；").append("最终尺寸").append("=").append(bitmap.getWidth()).append("x").append(bitmap.getHeight());
            }else{
                stringBuilder.append("；").append("未缩放");
            }
            stringBuilder.append("；").append(loadRequest.getName());
            Log.d(Spear.TAG, stringBuilder.toString());
        }

        @Override
        public void onDecodeFailure() {
            if(Spear.isDebugMode()){
                Log.e(Spear.TAG, NAME + "；" + "解码失败" + "；" + loadRequest.getName());
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
        public Bitmap onDecode(BitmapFactory.Options options) {
            if(!file.canRead()){
                if(Spear.isDebugMode()){
                    Log.e(Spear.TAG, NAME+"；"+"不可读取"+"；"+file.getPath());
                }
                return null;
            }

            return BitmapFactory.decodeFile(file.getPath(), options);
        }

        @Override
        public void onDecodeSuccess(Bitmap bitmap, Point originalSize, int inSampleSize) {
            if(!file.setLastModified(System.currentTimeMillis())){
                Log.w(Spear.TAG, NAME+"：修改文件最后修改时间失败："+file.getPath());
            }
            if(Spear.isDebugMode()){
                StringBuilder stringBuilder = new StringBuilder(NAME)
                        .append("；").append("解码成功");
                if(bitmap != null && loadRequest.getMaxsize() != null){
                    stringBuilder.append("；").append("原始尺寸").append("=").append(originalSize.x).append("x").append(originalSize.y);
                    stringBuilder.append("；").append("目标尺寸").append("=").append(loadRequest.getMaxsize().getWidth()).append("x").append(loadRequest.getMaxsize().getHeight());
                    stringBuilder.append("；").append("缩放比例").append("=").append(inSampleSize);
                    stringBuilder.append("；").append("最终尺寸").append("=").append(bitmap.getWidth()).append("x").append(bitmap.getHeight());
                }else{
                    stringBuilder.append("；").append("未缩放");
                }
                stringBuilder.append("；").append(loadRequest.getName());
                Log.d(Spear.TAG, stringBuilder.toString());
            }
        }

        @Override
        public void onDecodeFailure() {
            if(Spear.isDebugMode()){
                Log.e(Spear.TAG, new StringBuilder(NAME)
                        .append("；").append("解码失败").append("；").append("已删除")
                        .append("；").append("文件地址").append("=").append(file.getPath())
                        .append("；").append("文件长度").append("=").append(file.length())
                        .append("；").append("URI").append("=").append(loadRequest.getUri())
                        .toString());
            }
            if(!file.delete()){
                Log.e(Spear.TAG, "删除文件失败："+file.getPath());
            }
        }
    }

    public static class DrawableDecodeHelper implements DecodeHelper {
        private static final String NAME = "DrawableDecodeHelper";
        private String drawableIdString;
        private LoadRequest loadRequest;

        public DrawableDecodeHelper(String drawableIdString, LoadRequest loadRequest) {
            this.drawableIdString = drawableIdString;
            this.loadRequest = loadRequest;
        }

        @Override
        public Bitmap onDecode(BitmapFactory.Options options) {
            return BitmapFactory.decodeResource(loadRequest.getSpear().getConfiguration().getContext().getResources(), Integer.valueOf(drawableIdString), options);
        }

        @Override
        public void onDecodeSuccess(Bitmap bitmap, Point originalSize, int inSampleSize) {
            StringBuilder stringBuilder = new StringBuilder(NAME)
                    .append("；").append("解码成功");
            if(bitmap != null && loadRequest.getMaxsize() != null){
                stringBuilder.append("；").append("原始尺寸").append("=").append(originalSize.x).append("x").append(originalSize.y);
                stringBuilder.append("；").append("目标尺寸").append("=").append(loadRequest.getMaxsize().getWidth()).append("x").append(loadRequest.getMaxsize().getHeight());
                stringBuilder.append("；").append("缩放比例").append("=").append(inSampleSize);
                stringBuilder.append("；").append("最终尺寸").append("=").append(bitmap.getWidth()).append("x").append(bitmap.getHeight());
            }else{
                stringBuilder.append("；").append("未缩放");
            }
            stringBuilder.append("；").append(loadRequest.getName());
            Log.d(Spear.TAG, stringBuilder.toString());
        }

        @Override
        public void onDecodeFailure() {
            if(Spear.isDebugMode()){
                Log.e(Spear.TAG, NAME + "；" + "解码失败" + "；" + drawableIdString);
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
        public Bitmap onDecode(BitmapFactory.Options options) {
            if(file.canRead()){
                return BitmapFactory.decodeFile(file.getPath(), options);
            }else{
                if(Spear.isDebugMode()){
                    Log.e(Spear.TAG, NAME+"；"+"不可读取"+"；"+file.getPath());
                }
                return null;
            }
        }

        @Override
        public void onDecodeSuccess(Bitmap bitmap, Point originalSize, int inSampleSize) {
            StringBuilder stringBuilder = new StringBuilder(NAME)
                    .append("；"+"解码成功");
            if(bitmap != null && loadRequest.getMaxsize() != null){
                stringBuilder.append("；").append("原始尺寸").append("=").append(originalSize.x).append("x").append(originalSize.y);
                stringBuilder.append("；").append("目标尺寸").append("=").append(loadRequest.getMaxsize().getWidth()).append("x").append(loadRequest.getMaxsize().getHeight());
                stringBuilder.append("；").append("缩放比例").append("=").append(inSampleSize);
                stringBuilder.append("；").append("最终尺寸").append("=").append(bitmap.getWidth()).append("x").append(bitmap.getHeight());
            }else{
                stringBuilder.append("；").append("未缩放");
            }
            stringBuilder.append("；").append(loadRequest.getName());
            Log.d(Spear.TAG, stringBuilder.toString());
        }

        @Override
        public void onDecodeFailure() {
            if(Spear.isDebugMode()){
                Log.e(Spear.TAG, new StringBuilder(NAME)
                        .append("；").append("解码失败")
                        .append("；").append("文件地址").append("=").append(file.getPath())
                        .append("；").append("文件长度").append("=").append(file.length())
                        .toString());
            }
        }
    }

    public static class ContentDecodeHelper implements DecodeHelper {
        private static final String NAME = "ContentDecodeHelper";
        private String contentUri;
        private LoadRequest loadRequest;

        public ContentDecodeHelper(String contentUri, LoadRequest loadRequest) {
            this.contentUri = contentUri;
            this.loadRequest = loadRequest;
        }

        @Override
        public Bitmap onDecode(BitmapFactory.Options options) {
            InputStream inputStream = null;
            try {
                inputStream = loadRequest.getSpear().getConfiguration().getContext().getContentResolver().openInputStream(Uri.parse(contentUri));
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
            StringBuilder stringBuilder = new StringBuilder(NAME)
                    .append("；").append("解码成功");
            if(bitmap != null && loadRequest.getMaxsize() != null){
                stringBuilder.append("；").append("原始尺寸").append("=").append(originalSize.x).append("x").append(originalSize.y);
                stringBuilder.append("；").append("目标尺寸").append("=").append(loadRequest.getMaxsize().getWidth()).append("x").append(loadRequest.getMaxsize().getHeight());
                stringBuilder.append("；").append("缩放比例").append("=").append(inSampleSize);
                stringBuilder.append("；").append("最终尺寸").append("=").append(bitmap.getWidth()).append("x").append(bitmap.getHeight());
            }else{
                stringBuilder.append("；").append("未缩放");
            }
            stringBuilder.append("；").append(loadRequest.getName());
            Log.d(Spear.TAG, stringBuilder.toString());
        }

        @Override
        public void onDecodeFailure() {
            if(Spear.isDebugMode()){
                Log.e(Spear.TAG, NAME + "；" + "解码失败" + "；" + contentUri);
            }
        }
    }
}