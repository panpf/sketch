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
import android.widget.ImageView;

import me.xiaopan.android.spear.DisplayListener;
import me.xiaopan.android.spear.DisplayRequest;
import me.xiaopan.android.spear.FailCause;
import me.xiaopan.android.spear.ImageFrom;

/**
 * 显示回调处理器
 */
public class DisplayCallbackHandler implements Handler.Callback{
    private static final int WHAT_CALLBACK_COMPLETED = 102;
    private static final int WHAT_CALLBACK_FAILED = 103;
    private static final int WHAT_CALLBACK_CANCELED = 104;
    private static final int WHAT_CALLBACK_PROGRESS = 105;
    private static final int WHAT_CALLBACK_PAUSE_DOWNLOAD = 106;
    private Handler handler;

    public DisplayCallbackHandler() {
        handler = new Handler(Looper.getMainLooper(), this);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what){
            case WHAT_CALLBACK_COMPLETED:
                ((DisplayRequest) msg.obj).handleCompletedOnMainThread();
                return true;
            case WHAT_CALLBACK_PROGRESS :
                ((DisplayRequest) msg.obj).updateProgressOnMainThread(msg.arg1, msg.arg2);
                return true;
            case WHAT_CALLBACK_FAILED:
                ((DisplayRequest) msg.obj).handleFailedOnMainThread();
                return true;
            case WHAT_CALLBACK_CANCELED:
                ((DisplayRequest) msg.obj).handleCanceledOnMainThread();
                return true;
            case WHAT_CALLBACK_PAUSE_DOWNLOAD:
                ((DisplayRequest) msg.obj).handlePauseDownloadOnMainThread();
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

    public void completeCallbackOnFire(ImageView imageView, BitmapDrawable bitmapDrawable, DisplayListener displayListener, ImageFrom imageFrom){
        imageView.clearAnimation();
        imageView.setImageDrawable(bitmapDrawable);
        if(displayListener != null){
            displayListener.onCompleted(imageFrom);
        }
    }

    public void failCallbackOnFire(ImageView imageView, Drawable loadFailDrawable, FailCause failCause, DisplayListener displayListener){
        if(loadFailDrawable != null){
            imageView.setImageDrawable(loadFailDrawable);
        }
        if(displayListener != null){
            displayListener.onFailed(failCause);
        }
    }

    public void completeCallback(DisplayRequest displayRequest){
        handler.obtainMessage(WHAT_CALLBACK_COMPLETED, displayRequest).sendToTarget();
    }

    public void failCallback(DisplayRequest displayRequest){
        handler.obtainMessage(WHAT_CALLBACK_FAILED, displayRequest).sendToTarget();
    }

    public void cancelCallback(DisplayRequest displayRequest){
        handler.obtainMessage(WHAT_CALLBACK_CANCELED, displayRequest).sendToTarget();
    }

    public void updateProgressCallback(DisplayRequest request, int totalLength, int completedLength){
        handler.obtainMessage(WHAT_CALLBACK_PROGRESS, totalLength, completedLength, request).sendToTarget();
    }

    public void pauseDownloadCallback(DisplayRequest displayRequest){
        handler.obtainMessage(WHAT_CALLBACK_PAUSE_DOWNLOAD, displayRequest).sendToTarget();
    }
}
