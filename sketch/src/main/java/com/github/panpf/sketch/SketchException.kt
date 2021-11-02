package com.github.panpf.sketch;

public abstract class SketchException extends Exception {
    public SketchException() {
    }

    public SketchException(String message) {
        super(message);
    }

    public SketchException(String message, Throwable cause) {
        super(message, cause);
    }

    public SketchException(Throwable cause) {
        super(cause);
    }
}
