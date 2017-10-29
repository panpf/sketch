package me.xiaopan.sketch;

import android.support.annotation.NonNull;

public interface Identifier {
    /**
     * 用来生成缓存 key，或者在 log 中标识一个组件
     * <br>
     * 通常 key 是由组件名称和组件属性组成的，例如 String.format("%s(radius=%d,maskColor=%d)", "GaussianBlurImageProcessor", radius, maskColor)
     */
    @NonNull
    String getKey();
}
