/*
 * Copyright (C) 2017 Peng fei Pan <sky@panpf.me>
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

package me.panpf.sketch.viewfun;

import java.lang.ref.WeakReference;

import me.panpf.sketch.request.DownloadProgressListener;

class ProgressListenerProxy implements DownloadProgressListener {
    private WeakReference<FunctionCallbackView> viewWeakReference;

    public ProgressListenerProxy(FunctionCallbackView view) {
        this.viewWeakReference = new WeakReference<>(view);
    }

    @Override
    public void onUpdateDownloadProgress(int totalLength, int completedLength) {
        FunctionCallbackView view = viewWeakReference.get();
        if (view == null) {
            return;
        }

        boolean needInvokeInvalidate = view.getFunctions().onUpdateDownloadProgress(totalLength, completedLength);
        if (needInvokeInvalidate) {
            view.invalidate();
        }

        if (view.wrappedProgressListener != null) {
            view.wrappedProgressListener.onUpdateDownloadProgress(totalLength, completedLength);
        }
    }
}
