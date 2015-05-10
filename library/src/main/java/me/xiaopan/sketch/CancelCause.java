package me.xiaopan.sketch;

/**
 * 取消的原因
 */
public enum CancelCause {
    NORMAL,
    LEVEL_IS_LOCAL,
    LEVEL_IS_MEMORY,
    PAUSE_DOWNLOAD,
    PAUSE_LOAD,
}
