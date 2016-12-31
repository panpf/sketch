package me.xiaopan.sketch;

public interface Identifier {
    /**
     * 用来生成缓存key，或者在log中标识一个组件
     * <br>
     * 通常key是由组件名称和组件属性组成的，例如 String.format("%s(radius=%d,maskColor=%d)", "GaussianBlurImageProcessor", radius, layerColor)
     */
    String getKey();
}
