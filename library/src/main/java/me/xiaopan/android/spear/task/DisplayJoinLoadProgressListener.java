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
import me.xiaopan.android.spear.request.ProgressListener;

public class DisplayJoinLoadProgressListener implements ProgressListener {
    private DisplayRequest request;

    public DisplayJoinLoadProgressListener(DisplayRequest request) {
        this.request = request;
    }

    @Override
    public void onUpdateProgress(final int totalLength, final int completedLength) {
        request.getSpear().getDisplayCallbackHandler().updateProgressCallback(request, totalLength, completedLength);
    }
}
