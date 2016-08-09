/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package me.xiaopan.sketch.feature.zoom.gestures;

import android.content.Context;
import android.os.Build;

public class ScaleDragGestureDetectorCompat {
    public static ScaleDragGestureDetector newInstance(Context context, OnScaleDragGestureListener listener) {
        final int sdkVersion = Build.VERSION.SDK_INT;
        ScaleDragGestureDetector detector;

        if (sdkVersion >= Build.VERSION_CODES.FROYO) {
            detector = new FroyoScaleDragGestureDetector(context);
        } else if (sdkVersion >= Build.VERSION_CODES.ECLAIR) {
            detector = new EclairScaleDragGestureDetector(context);
        } else {
            detector = new CupcakeScaleDragGestureDetector(context);
        }

        detector.setOnGestureListener(listener);

        return detector;
    }
}
