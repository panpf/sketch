package me.xiaopan.android.spear;

/**
 * 取消的原因
 */
public enum CancelCause {
    NORMAL,
    LEVEL_IS_LOCAL,
    LEVEL_IS_MEMORY,
    PAUSE_DOWNLOAD_NEW_IMAGE,
    PAUSE_LOAD_NEW_IMAGE,
}
