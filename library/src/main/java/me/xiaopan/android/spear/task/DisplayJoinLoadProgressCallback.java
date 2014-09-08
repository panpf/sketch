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

package me.xiaopan.android.spear.task;

import me.xiaopan.android.spear.request.DisplayRequest;
import me.xiaopan.android.spear.request.ProgressCallback;

public class DisplayJoinLoadProgressCallback implements ProgressCallback{
    private DisplayRequest request;
    private ProgressCallback displayProgressCallback;

    public DisplayJoinLoadProgressCallback(DisplayRequest request, ProgressCallback displayProgressCallback) {
        this.request = request;
        this.displayProgressCallback = displayProgressCallback;
    }

    @Override
    public void onUpdateProgress(final long totalLength, final long completedLength) {
        if(displayProgressCallback != null){
            request.getSpear().getHandler().post(new UpdateProgressRunnable(request, totalLength, completedLength, displayProgressCallback));
        }
    }

    private static class UpdateProgressRunnable implements Runnable{
        private DisplayRequest request;
        private long totalLength;
        private long completedLength;
        private ProgressCallback displayProgressCallback;

        private UpdateProgressRunnable(DisplayRequest request, long totalLength, long completedLength, ProgressCallback displayProgressCallback) {
            this.request = request;
            this.totalLength = totalLength;
            this.completedLength = completedLength;
            this.displayProgressCallback = displayProgressCallback;
        }

        @Override
        public void run() {
            if(request.isCanceled() || request.isFinished()){
                return;
            }
            displayProgressCallback.onUpdateProgress(totalLength, completedLength);
        }
    }
}
