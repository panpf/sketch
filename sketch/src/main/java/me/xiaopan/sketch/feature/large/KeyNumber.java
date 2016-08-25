package me.xiaopan.sketch.feature.large;

import java.util.concurrent.atomic.AtomicInteger;

public class KeyNumber {
    private AtomicInteger keyNumber;

    public KeyNumber() {
        this.keyNumber = new AtomicInteger();
    }

    /**
     * 刷新key，那些持有旧key的任务就会自动取消
     */
    public void refresh() {
        if (keyNumber.get() == Integer.MAX_VALUE) {
            keyNumber.set(0);
        } else {
            keyNumber.addAndGet(1);
        }
    }

    public int getKey() {
        return keyNumber.get();
    }
}
