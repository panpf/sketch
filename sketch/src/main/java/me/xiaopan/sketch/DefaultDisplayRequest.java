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

package me.xiaopan.sketch;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;

import me.xiaopan.sketch.display.ImageDisplayer;
import me.xiaopan.sketch.display.TransitionImageDisplayer;
import me.xiaopan.sketch.process.ImageProcessor;
import me.xiaopan.sketch.util.SketchUtils;

/**
 * 显示请求
 */
public class DefaultDisplayRequest implements DisplayRequest, Runnable{
    private static final int WHAT_CALLBACK_COMPLETED = 102;
    private static final int WHAT_CALLBACK_FAILED = 103;
    private static final int WHAT_CALLBACK_CANCELED = 104;
    private static final int WHAT_CALLBACK_PROGRESS = 105;
    private static final int WHAT_CALLBACK_PAUSE_DOWNLOAD = 106;
    private static final String NAME = "DefaultDisplayRequest";

    // Base fields
    private Sketch sketch;  // Sketch
    private String uri;	// 图片地址
    private String name;	// 名称，用于在输出LOG的时候区分不同的请求
    private UriScheme uriScheme;	// Uri协议格式
    private RequestLevel requestLevel = RequestLevel.NET;  // 请求Level
    private RequestLevelFrom requestLevelFrom;

    // Download fields
    private boolean cacheInDisk = true;	// 是否开启磁盘缓存
    private ProgressListener progressListener;  // 下载进度监听器

    // Load fields
    private Resize resize;	// 裁剪尺寸，ImageProcessor会根据此尺寸来裁剪图片
    private boolean decodeGifImage = true; // 是否解码GIF图
    private boolean forceUseResize; // 是否强制使用resize
    private boolean lowQualityImage;   // 是否返回低质量的图片
    private MaxSize maxSize;	// 最大尺寸，用于读取图片时计算inSampleSize
    private ImageProcessor imageProcessor;	// 图片处理器

    // Display fields
    private String memoryCacheId;	// 内存缓存ID
    private boolean cacheInMemory = true;	// 是否开启内存缓存
    private FixedSize fixedSize;    // 固定尺寸
    private ImageHolder failureImageHolder;	// 当失败时显示的图片
    private ImageHolder pauseDownloadImageHolder;	// 当暂停下载时显示的图片
    private ImageDisplayer imageDisplayer;	// 图片显示器
    private DisplayListener displayListener;	// 监听器

    // Runtime fields
    private File cacheFile;	// 缓存文件
    private byte[] imageData;   // 如果不使用磁盘缓存的话下载完成后图片数据就用字节数组保存着
    private String mimeType;
    private Context context;
    private Drawable resultDrawable;    // 最终的图片
    private ImageFrom imageFrom;    // 图片来自哪里
    private FailCause failCause;    // 失败原因
    private RunStatus runStatus = RunStatus.DISPATCH;    // 运行状态，用于在执行run方法时知道该干什么
    private CancelCause cancelCause;  // 取消原因
    private RequestStatus requestStatus = RequestStatus.WAIT_DISPATCH;  // 状态
    private SketchImageViewInterfaceHolder sketchImageViewInterfaceHolder;    // 绑定ImageView

    protected ImageView.ScaleType scaleType;

    public DefaultDisplayRequest(Sketch sketch, String uri, UriScheme uriScheme, String memoryCacheId, SketchImageViewInterface sketchImageViewInterface) {
        this.context = sketch.getConfiguration().getContext();
        this.sketch = sketch;
        this.uri = uri;
        this.uriScheme = uriScheme;
        this.memoryCacheId = memoryCacheId;
        this.sketchImageViewInterfaceHolder = new SketchImageViewInterfaceHolder(sketchImageViewInterface, this);
        this.scaleType = sketchImageViewInterface.getScaleType();
    }

    /****************************************** Base methods ******************************************/
    @Override
    public Sketch getSketch() {
        return sketch;
    }

    @Override
    public String getUri() {
        return uri;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public UriScheme getUriScheme() {
        return uriScheme;
    }

    @Override
    public void setRequestLevel(RequestLevel requestLevel) {
        this.requestLevel = requestLevel;
    }

    @Override
    public void setRequestLevelFrom(RequestLevelFrom requestLevelFrom) {
        this.requestLevelFrom = requestLevelFrom;
    }

    /****************************************** Download methods ******************************************/
    @Override
    public void setCacheInDisk(boolean cacheInDisk) {
        this.cacheInDisk = cacheInDisk;
    }

    @Override
    public boolean isCacheInDisk() {
        return cacheInDisk;
    }

    @Override
    public void setProgressListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    /****************************************** Load methods ******************************************/
    @Override
    public Resize getResize() {
        return resize;
    }

    @Override
    public void setResize(Resize resize) {
        this.resize = resize;
    }

    @Override
    public boolean isForceUseResize() {
        return forceUseResize;
    }

    @Override
    public void setForceUseResize(boolean forceUseResize) {
        this.forceUseResize = forceUseResize;
    }

    @Override
    public MaxSize getMaxSize() {
        return maxSize;
    }

    @Override
    public void setMaxSize(MaxSize maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    public boolean isLowQualityImage() {
        return lowQualityImage;
    }

    @Override
    public void setLowQualityImage(boolean lowQualityImage) {
        this.lowQualityImage = lowQualityImage;
    }

    @Override
    public ImageProcessor getImageProcessor() {
        return imageProcessor;
    }

    @Override
    public void setImageProcessor(ImageProcessor imageProcessor) {
        this.imageProcessor = imageProcessor;
    }

    /****************************************** Display methods ******************************************/
    @Override
    public String getMemoryCacheId() {
        return memoryCacheId;
    }

    @Override
    public void setCacheInMemory(boolean cacheInMemory) {
        this.cacheInMemory = cacheInMemory;
    }

    @Override
    public void setImageDisplayer(ImageDisplayer imageDisplayer) {
        this.imageDisplayer = imageDisplayer;
    }

    @Override
    public void setFailureImageHolder(ImageHolder failureImageHolder) {
        this.failureImageHolder = failureImageHolder;
    }

    @Override
    public Drawable getFailureDrawable() {
        if(failureImageHolder == null){
            return null;
        }else if(imageDisplayer != null && imageDisplayer instanceof TransitionImageDisplayer && fixedSize != null && scaleType == ImageView.ScaleType.CENTER_CROP){
            return new FixedRecycleBitmapDrawable(failureImageHolder.getRecycleBitmapDrawable(context), fixedSize);
        }else{
            return failureImageHolder.getRecycleBitmapDrawable(context);
        }
    }

    @Override
    public void setPauseDownloadImageHolder(ImageHolder pauseDownloadImageHolder) {
        this.pauseDownloadImageHolder = pauseDownloadImageHolder;
    }

    @Override
    public Drawable getPauseDownloadDrawable() {
        if(pauseDownloadImageHolder == null){
            return null;
        }else if(imageDisplayer != null && imageDisplayer instanceof TransitionImageDisplayer && fixedSize != null && scaleType == ImageView.ScaleType.CENTER_CROP){
            return new FixedRecycleBitmapDrawable(pauseDownloadImageHolder.getRecycleBitmapDrawable(context), fixedSize);
        }else{
            return pauseDownloadImageHolder.getRecycleBitmapDrawable(context);
        }
    }

    @Override
    public void setDisplayListener(DisplayListener displayListener) {
        this.displayListener = displayListener;
    }

    @Override
    public void setFixedSize(FixedSize fixedSize) {
        this.fixedSize = fixedSize;
    }

    /****************************************** Runtime methods ******************************************/
    @Override
    public RequestStatus getRequestStatus() {
        return requestStatus;
    }

    @Override
    public void setRequestStatus(RequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }

    @Override
    public FailCause getFailCause() {
        return failCause;
    }

    @Override
    public CancelCause getCancelCause() {
        return cancelCause;
    }

    @Override
    public boolean isFinished() {
        return requestStatus == RequestStatus.COMPLETED || requestStatus == RequestStatus.CANCELED || requestStatus == RequestStatus.FAILED;
    }

    @Override
    public boolean cancel() {
        if(isFinished()){
            return false;
        }
        toCanceledStatus(CancelCause.NORMAL);
        return true;
    }

    @Override
    public File getCacheFile() {
        return cacheFile;
    }

    @Override
    public byte[] getImageData() {
        return imageData;
    }

    @Override
    public ImageFrom getImageFrom() {
        return imageFrom;
    }

    /****************************************** Other methods ******************************************/
    @Override
    public boolean isCanceled() {
        boolean isCanceled = requestStatus == RequestStatus.CANCELED;
        if(!isCanceled){
            isCanceled = sketchImageViewInterfaceHolder != null && sketchImageViewInterfaceHolder.isCollected();
            if(isCanceled){
                toCanceledStatus(CancelCause.NORMAL);
            }
        }
        return isCanceled;
    }

    @Override
    public void toFailedStatus(FailCause failCause) {
        this.failCause = failCause;
        setRequestStatus(RequestStatus.WAIT_DISPLAY);
        sketch.getConfiguration().getHandler().obtainMessage(WHAT_CALLBACK_FAILED, this).sendToTarget();
    }

    @Override
    public void toCanceledStatus(CancelCause cancelCause) {
        this.cancelCause = cancelCause;
        setRequestStatus(RequestStatus.CANCELED);
        if(displayListener != null){
            sketch.getConfiguration().getHandler().obtainMessage(WHAT_CALLBACK_CANCELED, this).sendToTarget();
        }
    }

    @Override
    public void invokeInMainThread(Message msg) {
        switch (msg.what){
            case WHAT_CALLBACK_COMPLETED:
                handleCompletedOnMainThread();
                break;
            case WHAT_CALLBACK_PROGRESS :
                updateProgressOnMainThread(msg.arg1, msg.arg2);
                break;
            case WHAT_CALLBACK_FAILED:
                handleFailedOnMainThread();
                break;
            case WHAT_CALLBACK_CANCELED:
                handleCanceledOnMainThread();
                break;
            case WHAT_CALLBACK_PAUSE_DOWNLOAD:
                handlePauseDownloadOnMainThread();
                break;
            default:
                new IllegalArgumentException("unknown message what: "+msg.what).printStackTrace();
                break;
        }
    }

    @Override
    public void updateProgress(int totalLength, int completedLength) {
        if(progressListener != null){
            sketch.getConfiguration().getHandler().obtainMessage(WHAT_CALLBACK_PROGRESS, totalLength, completedLength, this).sendToTarget();
        }
    }

    @Override
    public void postRunDispatch() {
        setRequestStatus(RequestStatus.WAIT_DISPATCH);
        this.runStatus = RunStatus.DISPATCH;
        sketch.getConfiguration().getRequestExecutor().getRequestDispatchExecutor().execute(this);
    }

    @Override
    public void postRunDownload() {
        setRequestStatus(RequestStatus.WAIT_DOWNLOAD);
        this.runStatus = RunStatus.DOWNLOAD;
        sketch.getConfiguration().getRequestExecutor().getNetRequestExecutor().execute(this);
    }

    @Override
    public void postRunLoad() {
        setRequestStatus(RequestStatus.WAIT_LOAD);
        this.runStatus = RunStatus.LOAD;
        sketch.getConfiguration().getRequestExecutor().getLocalRequestExecutor().execute(this);
    }

    @Override
    public void run() {
        switch(runStatus){
            case DISPATCH:
                executeDispatch();
                break;
            case LOAD:
                executeLoad();
                break;
            case DOWNLOAD:
                executeDownload();
                break;
            default:
                new IllegalArgumentException("unknown runStatus: "+runStatus.name()).printStackTrace();
                break;
        }
    }

    /**
     * 执行分发
     */
    private void executeDispatch() {
        setRequestStatus(RequestStatus.DISPATCHING);
        if(uriScheme == UriScheme.HTTP || uriScheme == UriScheme.HTTPS){
            File diskCacheFile = cacheInDisk ? sketch.getConfiguration().getDiskCache().getCacheFile(uri):null;
            if(diskCacheFile != null && diskCacheFile.exists()){
                this.cacheFile = diskCacheFile;
                this.imageFrom = ImageFrom.DISK_CACHE;
                postRunLoad();
                if(Sketch.isDebugMode()){
                    Log.d(Sketch.TAG, SketchUtils.concat(NAME, " - ", "executeDispatch", " - ", "diskCache", " - ", name));
                }
            }else{
                if(requestLevel == RequestLevel.LOCAL){
                    if(requestLevelFrom == RequestLevelFrom.PAUSE_DOWNLOAD){
                        setRequestStatus(RequestStatus.WAIT_DISPLAY);
                        sketch.getConfiguration().getHandler().obtainMessage(WHAT_CALLBACK_PAUSE_DOWNLOAD, this).sendToTarget();
                        if(Sketch.isDebugMode()){
                            Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "canceled", " - ", "pause download", " - ", name));
                        }
                    }else{
                        toCanceledStatus(CancelCause.LEVEL_IS_LOCAL);
                        if(Sketch.isDebugMode()){
                            Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "canceled", " - ", "requestLevel is local", " - ", name));
                        }
                    }
                    return;
                }

                postRunDownload();
                if(Sketch.isDebugMode()){
                    Log.d(Sketch.TAG, SketchUtils.concat(NAME, " - ", "executeDispatch", " - ", "download", " - ", name));
                }
            }
        }else{
            this.imageFrom = ImageFrom.LOCAL;
            postRunLoad();
            if(Sketch.isDebugMode()){
                Log.d(Sketch.TAG, SketchUtils.concat(NAME, " - ", "executeDispatch", " - ", "local", " - ", name));
            }
        }
    }

    /**
     * 执行下载
     */
    private void executeDownload() {
        if(isCanceled()){
            if(Sketch.isDebugMode()){
                Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "executeDownload", " - ", "canceled", " - ", "startDownload", " - ", name));
            }
            return;
        }

        DownloadResult downloadResult = sketch.getConfiguration().getImageDownloader().download(this);

        if(isCanceled()){
            if(Sketch.isDebugMode()){
                Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "executeDownload", " - ", "canceled", " - ", "downloadAfter", " - ", name));
            }
            return;
        }

        if(downloadResult != null  && downloadResult.getResult() != null){
            if(downloadResult.getResult().getClass().isAssignableFrom(File.class)){
                this.cacheFile = (File) downloadResult.getResult();
            }else{
                this.imageData = (byte[]) downloadResult.getResult();
            }
            this.imageFrom = downloadResult.isFromNetwork()?ImageFrom.NETWORK:ImageFrom.DISK_CACHE;
            postRunLoad();
        }else{
            toFailedStatus(FailCause.DOWNLOAD_FAIL);
        }
    }

    /**
     * 执行加载
     */
    private void executeLoad(){
        if(isCanceled()){
            if(Sketch.isDebugMode()){
                Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "executeLoad", " - ", "canceled", " - ", "startLoad", " - ", name));
            }
            return;
        }

        // 检查是否已经有了
        if(cacheInMemory){
            Drawable cacheDrawable = sketch.getConfiguration().getMemoryCache().get(memoryCacheId);
            if(cacheDrawable != null){
                RecycleDrawableInterface recycleDrawable = (RecycleDrawableInterface) cacheDrawable;
                if(!recycleDrawable.isRecycled()){
                    if(Sketch.isDebugMode()){
                        Log.i(Sketch.TAG, SketchUtils.concat(NAME, " - ", "executeLoad", " - ", "from memory get drawable", " - ", recycleDrawable.getInfo(), " - ", name));
                    }
                    this.resultDrawable = cacheDrawable;
                    imageFrom = ImageFrom.MEMORY_CACHE;
                    setRequestStatus(RequestStatus.WAIT_DISPLAY);
                    recycleDrawable.setIsWaitDisplay("executeLoad:fromMemory", true);
                    sketch.getConfiguration().getHandler().obtainMessage(WHAT_CALLBACK_COMPLETED, this).sendToTarget();
                    return;
                }else{
                    sketch.getConfiguration().getMemoryCache().remove(memoryCacheId);
                    if(Sketch.isDebugMode()){
                        Log.e(Sketch.TAG, SketchUtils.concat(NAME, " - ", "executeLoad", "bitmap recycled", " - ", recycleDrawable.getInfo(), " - ", name));
                    }
                }
            }
        }

        setRequestStatus(RequestStatus.LOADING);

        // 如果是本地APK文件就尝试得到其缓存文件
        if(isLocalApkFile()){
            File apkIconCacheFile = getApkCacheIconFile();
            if(apkIconCacheFile != null){
                this.cacheFile = apkIconCacheFile;
            }
        }

        // 解码
        Object decodeResult = sketch.getConfiguration().getImageDecoder().decode(this);
        if(decodeResult == null){
            toFailedStatus(FailCause.DECODE_FAIL);
            return;
        }

        if(decodeResult instanceof Bitmap){
            Bitmap bitmap = (Bitmap) decodeResult;
            if(!bitmap.isRecycled()){
                if(Sketch.isDebugMode()){
                    Log.d(Sketch.TAG, SketchUtils.concat(NAME, " - ", "executeLoad", " - ", "new bitmap", " - ", RecycleBitmapDrawable.getInfo(bitmap, mimeType), " - ", name));
                }
            }else{
                if(Sketch.isDebugMode()){
                    Log.e(Sketch.TAG, SketchUtils.concat(NAME, " - ", "executeLoad", " - ", "decode failed bitmap recycled", " - ", "decode after", " - ", RecycleBitmapDrawable.getInfo(bitmap, mimeType), " - ", name));
                }
            }

            if(isCanceled()){
                if(Sketch.isDebugMode()){
                    Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "executeLoad", " - ", "canceled", " - ", "decode after", " - ", "recycle bitmap", " - ", RecycleBitmapDrawable.getInfo(bitmap, mimeType), " - ", name));
                }
                bitmap.recycle();
                return;
            }

            //处理
            if(!bitmap.isRecycled()){
                ImageProcessor imageProcessor = getImageProcessor();
                if(imageProcessor != null){
                    Bitmap newBitmap = imageProcessor.process(sketch, bitmap, resize, forceUseResize, lowQualityImage);
                    if(newBitmap != null && newBitmap != bitmap && Sketch.isDebugMode()){
                        Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "executeLoad", " - ", "process after", " - ", "newBitmap", " - ", RecycleBitmapDrawable.getInfo(newBitmap, mimeType), " - ", "recycled old bitmap", " - ", name));
                    }
                    if(newBitmap == null || newBitmap != bitmap){
                        bitmap.recycle();
                    }
                    bitmap = newBitmap;
                }
            }

            if(isCanceled()){
                if(Sketch.isDebugMode()){
                    Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "executeLoad", " - ", "canceled", " - ", "process after", " - ", "recycle bitmap", " - ", RecycleBitmapDrawable.getInfo(bitmap, mimeType), " - ", name));
                }
                if(bitmap != null){
                    bitmap.recycle();
                }
                return;
            }

            if(bitmap != null && !bitmap.isRecycled()){
                RecycleBitmapDrawable bitmapDrawable = new RecycleBitmapDrawable(bitmap);
                if(cacheInMemory && memoryCacheId != null){
                    sketch.getConfiguration().getMemoryCache().put(memoryCacheId, bitmapDrawable);
                }
                bitmapDrawable.setMimeType(mimeType);
                this.resultDrawable = bitmapDrawable;
                setRequestStatus(RequestStatus.WAIT_DISPLAY);
                bitmapDrawable.setIsWaitDisplay("executeLoad:new", true);
                sketch.getConfiguration().getHandler().obtainMessage(WHAT_CALLBACK_COMPLETED, this).sendToTarget();
            }else{
                toFailedStatus(FailCause.DECODE_FAIL);
            }
        }else if(decodeResult instanceof RecycleGifDrawable){
            RecycleGifDrawable gifDrawable = (RecycleGifDrawable) decodeResult;
            gifDrawable.setMimeType(mimeType);

            if(Sketch.isDebugMode()){
                Log.d(Sketch.TAG, SketchUtils.concat(NAME, " - ", "executeLoad", " - ", "new gif drawable", " - ", gifDrawable.getInfo(), " - ", name));
            }

            if(!gifDrawable.isRecycled()){
                if(cacheInMemory && memoryCacheId != null){
                    sketch.getConfiguration().getMemoryCache().put(memoryCacheId, gifDrawable);
                }
                this.resultDrawable = gifDrawable;
                setRequestStatus(RequestStatus.WAIT_DISPLAY);
                gifDrawable.setIsWaitDisplay("executeLoad:new", true);
                sketch.getConfiguration().getHandler().obtainMessage(WHAT_CALLBACK_COMPLETED, this).sendToTarget();
            }else{
                toFailedStatus(FailCause.DECODE_FAIL);
            }
        }else{
            toFailedStatus(FailCause.DECODE_FAIL);
        }
    }

    @Override
    public void setLoadListener(LoadListener loadListener) {
    }

    @Override
    public boolean isDecodeGifImage() {
        return decodeGifImage;
    }

    @Override
    public void setDecodeGifImage(boolean isDecodeGifImage) {
        this.decodeGifImage = isDecodeGifImage;
    }

    @Override
    public void setDownloadListener(DownloadListener downloadListener) {
    }

    @Override
    public boolean isLocalApkFile(){
        return uriScheme == UriScheme.FILE && SketchUtils.checkSuffix(uri, ".apk");
    }

    @Override
    public String getMimeType() {
        return mimeType;
    }

    @Override
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    /**
     * 获取APK图片的缓存文件
     * @return APK图片的缓存文件
     */
    private File getApkCacheIconFile(){
        File apkIconCacheFile = sketch.getConfiguration().getDiskCache().getCacheFile(uri);
        if(apkIconCacheFile != null){
            return apkIconCacheFile;
        }

        Bitmap iconBitmap = SketchUtils.decodeIconFromApk(context, uri, lowQualityImage, NAME);
        if(iconBitmap != null && !iconBitmap.isRecycled()){
            apkIconCacheFile = sketch.getConfiguration().getDiskCache().saveBitmap(iconBitmap, uri);
            if(apkIconCacheFile != null){
                return apkIconCacheFile;
            }
        }

        return null;
    }

    private void handleCompletedOnMainThread() {
        if(isCanceled()){
            if(resultDrawable != null && resultDrawable instanceof RecycleDrawableInterface){
                ((RecycleDrawableInterface) resultDrawable).setIsWaitDisplay("completedCallback:cancel", false);
            }
            if(Sketch.isDebugMode()){
                Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "handleCompletedOnMainThread", " - ", "canceled", " - ", name));
            }
            return;
        }

        setRequestStatus(RequestStatus.DISPLAYING);

        // Set FixedSize
        Drawable finalDrawable;
        if(imageDisplayer != null && imageDisplayer instanceof TransitionImageDisplayer && resultDrawable instanceof RecycleBitmapDrawable && fixedSize != null && scaleType == ImageView.ScaleType.CENTER_CROP){
            finalDrawable = new FixedRecycleBitmapDrawable((RecycleBitmapDrawable) resultDrawable, fixedSize);
        }else{
            finalDrawable = resultDrawable;
        }

        if(imageDisplayer == null){
            imageDisplayer = sketch.getConfiguration().getDefaultImageDisplayer();
        }
        imageDisplayer.display(sketchImageViewInterfaceHolder.getSketchImageViewInterface(), finalDrawable);
        ((RecycleDrawableInterface) resultDrawable).setIsWaitDisplay("completedCallback", false);
        setRequestStatus(RequestStatus.COMPLETED);
        if(displayListener != null){
            displayListener.onCompleted(imageFrom, mimeType);
        }
    }

    private void handleFailedOnMainThread() {
        if(isCanceled()){
            if(Sketch.isDebugMode()){
                Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "handleFailedOnMainThread", " - ", "canceled", " - ", name));
            }
            return;
        }

        setRequestStatus(RequestStatus.DISPLAYING);
        if(imageDisplayer == null){
            imageDisplayer = sketch.getConfiguration().getDefaultImageDisplayer();
        }
        imageDisplayer.display(sketchImageViewInterfaceHolder.getSketchImageViewInterface(), getFailureDrawable());
        setRequestStatus(RequestStatus.FAILED);
        if(displayListener != null){
            displayListener.onFailed(failCause);
        }
    }

    private void handleCanceledOnMainThread() {
        if(displayListener != null){
            displayListener.onCanceled(cancelCause);
        }
    }

    private void handlePauseDownloadOnMainThread() {
        if(isCanceled()){
            if(Sketch.isDebugMode()){
                Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "handlePauseDownloadOnMainThread", " - ", "canceled", " - ", name));
            }
            return;
        }

        if(pauseDownloadImageHolder != null){
            setRequestStatus(RequestStatus.DISPLAYING);
            if(imageDisplayer == null){
                imageDisplayer = sketch.getConfiguration().getDefaultImageDisplayer();
            }
            imageDisplayer.display(sketchImageViewInterfaceHolder.getSketchImageViewInterface(), getPauseDownloadDrawable());
        }

        cancelCause = CancelCause.PAUSE_DOWNLOAD;
        setRequestStatus(RequestStatus.CANCELED);
        if(displayListener != null){
            displayListener.onCanceled(cancelCause);
        }
    }

    private void updateProgressOnMainThread(int totalLength, int completedLength) {
        if(isFinished()){
            if(Sketch.isDebugMode()){
                Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "updateProgressOnMainThread", " - ", "finished", " - ", name));
            }
            return;
        }

        if(progressListener != null){
            progressListener.onUpdateProgress(totalLength, completedLength);
        }
    }
}