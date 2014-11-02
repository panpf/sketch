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

package me.xiaopan.android.spear.request;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import me.xiaopan.android.spear.Spear;
import me.xiaopan.android.spear.display.ImageDisplayer;
import me.xiaopan.android.spear.util.FailureCause;

/**
 * 显示回调处理器
 */
public class DisplayCallbackHandler implements Handler.Callback{
    private static final String NAME = DisplayCallbackHandler.class.getSimpleName();
    private static final int WHAT_CALLBACK_COMPLETED = 102;
    private static final int WHAT_CALLBACK_FAILED = 103;
    private static final int WHAT_CALLBACK_CANCELED = 104;
    private static final int WHAT_CALLBACK_PROGRESS = 105;
    private Handler handler;

    public DisplayCallbackHandler() {
        handler = new Handler(Looper.getMainLooper(), this);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what){
            case WHAT_CALLBACK_COMPLETED:
                DisplayRequest displayRequest = (DisplayRequest) msg.obj;
                if(displayRequest.isCanceled()){
                    if(displayRequest.getSpear().isDebugMode()){
                        Log.w(Spear.LOG_TAG, NAME + "：" + "已取消显示" + "；" + displayRequest.getName());
                    }
                    if(displayRequest.getDisplayListener() != null){
                        displayRequest.getDisplayListener().onCanceled();
                    }
                    return true;
                }

                ImageView imageView = displayRequest.getImageViewHolder().getImageView();
                if(imageView == null){
                    if(displayRequest.getSpear().isDebugMode()){
                        Log.w(Spear.LOG_TAG, NAME + "：" + "已取消显示（ImageView为null）" + "；" + displayRequest.getName());
                    }
                    if(displayRequest.getDisplayListener() != null){
                        displayRequest.getDisplayListener().onCanceled();
                    }
                    return true;
                }

                displayRequest.getImageDisplayer().display(imageView, displayRequest.getBitmapDrawable(), ImageDisplayer.BitmapType.SUCCESS, displayRequest);
                displayRequest.setStatus(Request.Status.COMPLETED);

                if(displayRequest.getDisplayListener() != null){
                    displayRequest.getDisplayListener().onCompleted(displayRequest.getUri(), imageView, displayRequest.getBitmapDrawable(), displayRequest.getFrom());
                }
                return true;
            case WHAT_CALLBACK_PROGRESS :
                DisplayRequest displayRequestOnProgress = (DisplayRequest) msg.obj;
                if(displayRequestOnProgress.isCanceled() || displayRequestOnProgress.isFinished()){
                    return true;
                }
                displayRequestOnProgress.getDisplayProgressListener().onUpdateProgress(msg.arg1, msg.arg2);
                return true;
            case WHAT_CALLBACK_FAILED:
                DisplayRequest displayRequestOnFail = (DisplayRequest) msg.obj;
                if(displayRequestOnFail.isCanceled()){
                    if(displayRequestOnFail.getSpear().isDebugMode()){
                        Log.w(Spear.LOG_TAG, NAME + "：" + "已取消显示" + "；" + displayRequestOnFail.getName());
                    }
                    if(displayRequestOnFail.getDisplayListener() != null){
                        displayRequestOnFail.getDisplayListener().onCanceled();
                    }
                    return true;
                }

                ImageView imageViewOnFail = displayRequestOnFail.getImageViewHolder().getImageView();
                if(imageViewOnFail == null){
                    if(displayRequestOnFail.getSpear().isDebugMode()){
                        Log.w(Spear.LOG_TAG, NAME + "：" + "已取消显示（ImageView为null）" + "；" + displayRequestOnFail.getName());
                    }
                    if(displayRequestOnFail.getDisplayListener() != null){
                        displayRequestOnFail.getDisplayListener().onCanceled();
                    }
                    return true;
                }

                displayRequestOnFail.getImageDisplayer().display(imageViewOnFail, displayRequestOnFail.getBitmapDrawable(), ImageDisplayer.BitmapType.FAILURE, displayRequestOnFail);
                displayRequestOnFail.setStatus(Request.Status.FAILED);

                if(displayRequestOnFail.getDisplayListener() != null){
                    displayRequestOnFail.getDisplayListener().onFailed(displayRequestOnFail.getFailureCause());
                }
                return true;
            case WHAT_CALLBACK_CANCELED:
                ((DisplayListener) msg.obj).onCanceled();
                return true;
            default:
                return false;
        }
    }

    public void startCallbackOnFire(DisplayListener displayListener){
        if(displayListener == null){
            return;
        }
        displayListener.onStarted();
    }

    public void completeCallback(DisplayRequest displayRequest, BitmapDrawable bitmapDrawable, DisplayListener.From from){
        displayRequest.setBitmapDrawable(bitmapDrawable);
        displayRequest.setFrom(from);
        handler.obtainMessage(WHAT_CALLBACK_COMPLETED, displayRequest).sendToTarget();
    }

    public void completeCallbackOnFire(ImageView imageView, String uri, BitmapDrawable bitmapDrawable, DisplayListener displayListener, DisplayListener.From from){
        imageView.clearAnimation();
        imageView.setImageDrawable(bitmapDrawable);
        if(displayListener == null){
            return;
        }
        displayListener.onCompleted(uri, imageView, bitmapDrawable, from);
    }

    public void failCallback(DisplayRequest displayRequest, BitmapDrawable bitmapDrawable, FailureCause failureCause){
        displayRequest.setBitmapDrawable(bitmapDrawable);
        displayRequest.setFailureCause(failureCause);
        handler.obtainMessage(WHAT_CALLBACK_FAILED, displayRequest).sendToTarget();
    }

    public void failCallbackOnFire(ImageView imageView, Drawable bitmapDrawable, FailureCause failureCause, DisplayListener displayListener){
        if(bitmapDrawable != null){
            imageView.setImageDrawable(bitmapDrawable);
        }
        if(displayListener == null){
            return;
        }
        displayListener.onFailed(failureCause);
    }

    public void cancelCallback(DisplayListener displayListener){
        if(displayListener == null){
            return;
        }
        handler.obtainMessage(WHAT_CALLBACK_CANCELED, displayListener).sendToTarget();
    }

    public void updateProgressCallback(DisplayRequest request, int totalLength, int completedLength){
        handler.obtainMessage(WHAT_CALLBACK_PROGRESS, totalLength, completedLength, request).sendToTarget();
    }
}
