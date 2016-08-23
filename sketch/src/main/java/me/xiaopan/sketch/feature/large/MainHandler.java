/*
 * Copyright (C) 2016 Peng fei Pan <sky@xiaopan.me>
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

package me.xiaopan.sketch.feature.large;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.lang.ref.WeakReference;

class MainHandler extends Handler {
    private static final int WHAT_DESTROY_THREAD = 2001;
    private static final int WHAT_INIT_COMPLETED = 2002;
    private static final int WHAT_INIT_FAILED = 2003;
    private static final int WHAT_DECODE_COMPLETED = 2004;

    private WeakReference<ImageRegionDecodeExecutor> reference;

    public MainHandler(ImageRegionDecodeExecutor decodeExecutor) {
        super(Looper.getMainLooper());
        reference = new WeakReference<ImageRegionDecodeExecutor>(decodeExecutor);
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case WHAT_DESTROY_THREAD:
                destroyThread();
                break;
            case WHAT_INIT_COMPLETED:
                initCompleted((ImageRegionDecoder) msg.obj);
                break;
            case WHAT_INIT_FAILED:
                initFailed((Exception) msg.obj);
                break;
            case WHAT_DECODE_COMPLETED:
                decodeCompleted((DecodeParams) msg.obj);
                break;
        }
    }


    /**
     * 延迟三十秒停止解码线程
     */
    public void postDelayDestroyThread() {
        Message destroyMessage = obtainMessage(MainHandler.WHAT_DESTROY_THREAD);
        sendMessageDelayed(destroyMessage, 30 * 1000);
    }

    private void destroyThread() {
        ImageRegionDecodeExecutor decodeExecutor = reference.get();
        if (decodeExecutor != null) {
            decodeExecutor.recycle();
        }
    }

    /**
     * 取消停止解码线程的延迟任务
     */
    public void cancelDelayDestroyThread() {
        removeMessages(MainHandler.WHAT_DESTROY_THREAD);
    }


    public void postInitCompleted(ImageRegionDecoder decoder) {
        obtainMessage(MainHandler.WHAT_INIT_COMPLETED, decoder).sendToTarget();
    }

    private void initCompleted(ImageRegionDecoder decoder) {
        ImageRegionDecodeExecutor decodeExecutor = reference.get();
        if (decodeExecutor != null) {
            decodeExecutor.initCompleted(decoder);
        } else {
            decoder.recycle();
        }
    }


    public void postInitFailed(Exception e) {
        obtainMessage(MainHandler.WHAT_INIT_FAILED, e).sendToTarget();
    }

    private void initFailed(Exception e) {
        ImageRegionDecodeExecutor decodeExecutor = reference.get();
        if (decodeExecutor != null) {
            decodeExecutor.initFailed(e);
        }
    }


    public void postDecodeCompleted(DecodeParams decodeParams) {
        obtainMessage(MainHandler.WHAT_DECODE_COMPLETED, decodeParams).sendToTarget();
    }

    private void decodeCompleted(DecodeParams decodeParams) {
        ImageRegionDecodeExecutor decodeExecutor = reference.get();
        if (decodeExecutor != null) {
            decodeExecutor.decodeCompleted(decodeParams);
        }
    }

    public void clean(){
        removeMessages(WHAT_DESTROY_THREAD);
        removeMessages(WHAT_INIT_COMPLETED);
        removeMessages(WHAT_INIT_FAILED);
        removeMessages(WHAT_DECODE_COMPLETED);
    }
}
