package me.xiaopan.android.imageloader.task.display;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import me.xiaopan.android.imageloader.ImageLoader;
import me.xiaopan.android.imageloader.display.BitmapDisplayer;
import me.xiaopan.android.imageloader.task.load.LoadRequest;
import me.xiaopan.android.imageloader.util.ImageLoaderUtils;
import me.xiaopan.android.imageloader.util.RecyclingBitmapDrawable;

/**
 * Created by xiaopan on 2014/3/21 0021.
 */
public class DisplayJoinLoadListener implements LoadRequest.LoadListener{
    private static String NAME= DisplayJoinLoadListener.class.getSimpleName();

    private DisplayRequest displayRequest;

    public DisplayJoinLoadListener(DisplayRequest displayRequest) {
        this.displayRequest = displayRequest;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onUpdateProgress(long totalLength, long completedLength) {

    }

    @Override
    public void onComplete(Bitmap bitmap) {
        //创建BitmapDrawable
        BitmapDrawable bitmapDrawable;
        if (ImageLoaderUtils.hasHoneycomb()) {
            bitmapDrawable = new BitmapDrawable(displayRequest.getConfiguration().getContext().getResources(), bitmap);
        } else {
            bitmapDrawable = new RecyclingBitmapDrawable(displayRequest.getConfiguration().getContext().getResources(), bitmap);
        }

        //放入内存缓存
        if(displayRequest.getDisplayOptions().isEnableMenoryCache()){
            displayRequest.getConfiguration().getBitmapCacher().put(displayRequest.getId(), bitmapDrawable);
        }

        //显示
        if (!displayRequest.getImageViewHolder().isCollected()) {
            displayRequest.getConfiguration().getHandler().post(new DisplayRunnable(displayRequest, bitmapDrawable, BitmapDisplayer.BitmapType.SUCCESS));
        }else{
            if(displayRequest.getDisplayListener() != null){
                displayRequest.getConfiguration().getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        displayRequest.getDisplayListener().onCancel();
                    }
                });
            }
            if(displayRequest.getConfiguration().isDebugMode()){
                Log.d(ImageLoader.LOG_TAG, new StringBuffer(NAME).append("：").append("已解除绑定关系").append("；").append(displayRequest.getName()).toString());
            }
        }
    }

    @Override
    public void onFailure() {
        displayRequest.getConfiguration().getHandler().post(new DisplayRunnable(displayRequest, displayRequest.getDisplayOptions().getFailureDrawable(), BitmapDisplayer.BitmapType.FAILURE));
    }

    @Override
    public void onCancel() {
        if(displayRequest.getDisplayListener() != null){
            displayRequest.getConfiguration().getHandler().post(new Runnable() {
                @Override
                public void run() {
                    displayRequest.getDisplayListener().onCancel();
                }
            });
        }
        if(displayRequest.getConfiguration().isDebugMode()){
            Log.d(ImageLoader.LOG_TAG, new StringBuffer(NAME).append("：").append("已取消").append("；").append(displayRequest.getName()).toString());
        }
    }
}
