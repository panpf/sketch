package me.xiaopan.sketch;

public interface Identifier {
    /**
     * 获取标识符
     */
    String getIdentifier();

    /**
     * 追加标识符
     */
    StringBuilder appendIdentifier(String join, StringBuilder builder);
}
