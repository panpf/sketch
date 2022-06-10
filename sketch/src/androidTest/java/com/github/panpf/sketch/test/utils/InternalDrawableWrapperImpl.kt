package com.github.panpf.sketch.test.utils

import android.graphics.drawable.Drawable
import android.graphics.drawable.DrawableWrapper
import androidx.annotation.RequiresApi

@RequiresApi(23)
class InternalDrawableWrapperImpl(dr: Drawable?) : DrawableWrapper(dr)