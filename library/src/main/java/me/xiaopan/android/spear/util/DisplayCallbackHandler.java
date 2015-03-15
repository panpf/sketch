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

package me.xiaopan.android.spear.util;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import me.xiaopan.android.spear.Spear;
import me.xiaopan.android.spear.display.ImageDisplayer;
import me.xiaopan.android.spear.request.DisplayListener;
import me.xiaopan.android.spear.request.DisplayRequest;
import me.xiaopan.android.spear.request.FailCause;
import me.xiaopan.android.spear.request.ImageFrom;
import me.xiaopan.android.spear.request.Status;

/**
 * 显示回调处理器
 */
public class DisplayCallbackHandler implements Handler.Callback{
    private static final String NAME = "DisplayCallbackHandler";
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
                    displayRequest.tryReleaseImage("CompletedCallback - Cancel");
                    if(Spear.isDebugMode()){
                        Log.w(Spear.TAG, NAME + " - " + "COMPLETED"+ "：" + "已取消显示" + "；" + displayRequest.getName());
                    }
                    return true;
                }

                ImageView imageView = displayRequest.getImageViewHolder().getImageView();
                ImageDisplayer imageDisplayer = displayRequest.getImageDisplayer();
                if(imageDisplayer == null){
                    imageDisplayer = displayRequest.getSpear().getConfiguration().getDefaultImageDisplayer();
                }
                displayRequest.toDisplayingStatus();
                imageDisplayer.display(imageView, displayRequest.getResultBitmap(), ImageDisplayer.BitmapType.SUCCESS, displayRequest);
                displayRequest.setStatus(Status.COMPLETED);

                if(displayRequest.getDisplayListener() != null){
                    displayRequest.getDisplayListener().onCompleted(displayRequest.getImageFrom());
                }
                return true;
            case WHAT_CALLBACK_PROGRESS :
                DisplayRequest displayRequestOnProgress = (DisplayRequest) msg.obj;
                if(displayRequestOnProgress.isFinished()){
                    return true;
                }
                displayRequestOnProgress.getProgressListener().onUpdateProgress(msg.arg1, msg.arg2);
                return true;
            case WHAT_CALLBACK_FAILED:
                DisplayRequest displayRequestOnFail = (DisplayRequest) msg.obj;
                if(displayRequestOnFail.isCanceled()){
                    if(Spear.isDebugMode()){
                        Log.w(Spear.TAG, NAME + " - " + "FAILED" + "：" + "已取消显示" + "；" + displayRequestOnFail.getName());
                    }
                    return true;
                }

                ImageView imageViewOnFail = displayRequestOnFail.getImageViewHolder().getImageView();
                ImageDisplayer imageDisplayerOnFail = displayRequestOnFail.getImageDisplayer();
                if(imageDisplayerOnFail == null){
                    imageDisplayerOnFail = displayRequestOnFail.getSpear().getConfiguration().getDefaultImageDisplayer();
                }
                imageDisplayerOnFail.display(imageViewOnFail, displayRequestOnFail.getLoadFailDrawable(), ImageDisplayer.BitmapType.FAILURE, displayRequestOnFail);
                displayRequestOnFail.setStatus(Status.FAILED);

                if(displayRequestOnFail.getDisplayListener() != null){
                    displayRequestOnFail.getDisplayListener().onFailed(displayRequestOnFail.getFailCause());
                }
                return true;
            case WHAT_CALLBACK_CANCELED:
                DisplayRequest displayRequestOnCancel = (DisplayRequest) msg.obj;
                displayRequestOnCancel.getDisplayListener().onCanceled(displayRequestOnCancel.getCancelCause());
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

    public void completeCallback(DisplayRequest displayRequest){
        displayRequest.toWaitDisplayStatus();
        handler.obtainMessage(WHAT_CALLBACK_COMPLETED, displayRequest).sendToTarget();
    }

    public void completeCallbackOnFire(ImageView imageView, BitmapDrawable bitmapDrawable, DisplayListener displayListener, ImageFrom imageFrom){
        imageView.clearAnimation();
        imageView.setImageDrawable(bitmapDrawable);
        if(displayListener != null){
            displayListener.onCompleted(imageFrom);
        }
    }

    public void failCallback(DisplayRequest displayRequest){
        displayRequest.toWaitDisplayStatus();
        handler.obtainMessage(WHAT_CALLBACK_FAILED, displayRequest).sendToTarget();
    }

    public void failCallbackOnFire(ImageView imageView, Drawable loadFailDrawable, FailCause failCause, DisplayListener displayListener){
        if(loadFailDrawable != null){
            imageView.setImageDrawable(loadFailDrawable);
        }
        if(displayListener != null){
            displayListener.onFailed(failCause);
        }
    }

    public void cancelCallback(DisplayRequest displayRequest){
        if(displayRequest.getDisplayListener() == null){
            return;
        }
        handler.obtainMessage(WHAT_CALLBACK_CANCELED, displayRequest).sendToTarget();
    }

    public void updateProgressCallback(DisplayRequest request, int totalLength, int completedLength){
        handler.obtainMessage(WHAT_CALLBACK_PROGRESS, totalLength, completedLength, request).sendToTarget();
    }
}
