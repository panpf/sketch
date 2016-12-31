package me.xiaopan.sketch;

// todo 改名KEY
public interface Identifier {
    /**
     * 用来生成缓存key，或者在log中标识
     */
    String getKey();
}
