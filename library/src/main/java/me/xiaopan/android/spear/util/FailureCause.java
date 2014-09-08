package me.xiaopan.android.spear.util;

/**
 * 失败原因
 */
public enum FailureCause {
    /**
     * URI为NULL或空
     */
    URI_NULL_OR_EMPTY,

    /**
     * ImageView为NULL
     */
    IMAGE_VIEW_NULL,

    /**
     * URI不支持
     */
    URI_NO_SUPPORT,

    /**
     * 解码失败
     */
    DECODE_FAILED,
}
