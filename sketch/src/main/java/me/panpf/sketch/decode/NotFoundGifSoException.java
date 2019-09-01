package me.panpf.sketch.decode;

import androidx.annotation.NonNull;

import me.panpf.sketch.SketchException;

public class NotFoundGifSoException extends SketchException {
    public NotFoundGifSoException(@NonNull UnsatisfiedLinkError cause) {
        super(cause);
    }

    public NotFoundGifSoException(@NonNull ExceptionInInitializerError cause) {
        super(cause);
    }

    @NonNull
    @Override
    public synchronized Throwable getCause() {
        return super.getCause();
    }
}
