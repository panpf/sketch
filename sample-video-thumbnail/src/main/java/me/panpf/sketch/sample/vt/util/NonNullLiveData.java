package me.panpf.sketch.sample.vt.util;

import android.annotation.SuppressLint;
import android.arch.core.internal.SafeIterableMap;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

@SuppressLint("RestrictedApi")
public class NonNullLiveData<DATA> extends MutableLiveData<DATA> {

    private SafeIterableMap<NonNullObserver<DATA>, NonNullObserverWrapper<DATA>> mObservers = new SafeIterableMap<>();

    public NonNullLiveData(@NonNull DATA data) {
        super.setValue(data);
    }

    @SuppressLint("RestrictedApi")
    public void observeNonNull(@NonNull LifecycleOwner owner, @NonNull NonNullObserver<DATA> observer) {
        NonNullObserverWrapper<DATA> wrapper = new NonNullObserverWrapper<>(observer);
        mObservers.putIfAbsent(observer, wrapper);
        super.observe(owner, wrapper);
    }

    @SuppressWarnings("unused")
    public void observeForeverNonNull(@NonNull NonNullObserver<DATA> observer) {
        NonNullObserverWrapper<DATA> wrapper = new NonNullObserverWrapper<>(observer);
        mObservers.putIfAbsent(observer, wrapper);
        super.observeForever(wrapper);
    }

    @SuppressWarnings("unused")
    public void removeObserverNonNull(@NonNull NonNullObserver<DATA> observer) {
        NonNullObserverWrapper<DATA> wrapper = mObservers.remove(observer);
        if (wrapper != null) {
            super.removeObserver(wrapper);
        }
    }

    private static class NonNullObserverWrapper<DATA> implements Observer<DATA> {
        private NonNullObserver<DATA> nonNullObserver;

        public NonNullObserverWrapper(NonNullObserver<DATA> nonNullObserver) {
            this.nonNullObserver = nonNullObserver;
        }

        @Override
        public void onChanged(@Nullable DATA data) {
            if (data == null) {
                throw new NullPointerException("data is null");
            }
            nonNullObserver.onChanged(data);
        }
    }
}
