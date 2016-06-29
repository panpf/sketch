package me.xiaopan.sketch.util;

import java.io.File;

public class NoSpaceException extends Exception {
    public File dir;

    public NoSpaceException(File dir, String detailMessage) {
        super(detailMessage);
        this.dir = dir;
    }
}
