/*
 * Copyright (C) 2019 Peng fei Pan <panpfpanpf@outlook.me>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.panpf.sketch.request;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import me.panpf.sketch.SLog;

/**
 * 下载或加载结果分享管理器，用于解决重复下载、重复加载
 * <p>
 * 对于相同的请求（不同类型条件不一样），只要第一个请求执行完毕了，后续请求可以直接使用第一个请求的结果，那么我们可以将所有后续的请求都绑定在第一个请求上，
 * 等第一个请求执行完毕后直接将结果交给后续请求处理即可
 */
@SuppressWarnings("WeakerAccess")
public class ResultShareManager {
    private static final String NAME = "ResultShareManager";

    @NonNull
    private final Object displayResultShareProviderMapLock = new Object();
    @NonNull
    private final Object downloadResultShareProviderMapLock = new Object();
    @Nullable
    private Map<String, ResultShareDisplay> displayResultShareProviderMap;
    @Nullable
    private Map<String, ResultShareDownload> downloadResultShareProviderMap;

    public void registerDisplayResultShareProvider(@NonNull ResultShareDisplay provider) {
        if (!provider.canByDisplayResultShare()) {
            return;
        }

        synchronized (displayResultShareProviderMapLock) {
            if (displayResultShareProviderMap == null) {
                synchronized (this) {
                    if (displayResultShareProviderMap == null) {
                        displayResultShareProviderMap = new WeakHashMap<>();
                    }
                }
            }

            displayResultShareProviderMap.put(provider.getDisplayResultShareKey(), provider);

            if (SLog.isLoggable(SLog.DEBUG)) {
                SLog.dmf(NAME, "display. register result sharer provider. %s",
                        provider.getDisplayResultShareLog());
            }
        }
    }

    public void unregisterDisplayResultShareProvider(@NonNull ResultShareDisplay provider) {
        if (!provider.canByDisplayResultShare()) {
            return;
        }

        ResultShareDisplay resultShareDisplay = null;
        synchronized (displayResultShareProviderMapLock) {
            if (displayResultShareProviderMap != null) {
                resultShareDisplay = displayResultShareProviderMap.remove(provider.getDisplayResultShareKey());
                if (resultShareDisplay != null) {
                    if (SLog.isLoggable(SLog.DEBUG)) {
                        SLog.dmf(NAME, "display. unregister result sharer provider. %s",
                                resultShareDisplay.getDisplayResultShareLog());
                    }
                }
            }
        }

        if (resultShareDisplay != null) {
            Set<ResultShareDisplay> displayResultShareSet = resultShareDisplay.getDisplayResultShareSet();
            if (displayResultShareSet == null || displayResultShareSet.size() == 0) {
                return;
            }

            String providerId = resultShareDisplay.getDisplayResultShareLog();
            for (ResultShareDisplay resultShareDisplay1 : displayResultShareSet) {
                if (!resultShareDisplay1.isCanceled()) {
                    boolean success = resultShareDisplay1.processDisplayResultShare();

                    if (SLog.isLoggable(SLog.DEBUG)) {
                        SLog.dmf(NAME, "display. callback result sharer. %s. %s  <-  %s",
                                success ? "success" : "failed", resultShareDisplay1.getDisplayResultShareLog(), providerId);
                    }
                } else {
                    SLog.wmf(NAME, "display. callback result sharer. %s. %s  <-  %s",
                            "canceled", resultShareDisplay1.getDisplayResultShareLog(), providerId);
                }
            }
            displayResultShareSet.clear();
        }
    }

    public boolean byDisplayResultShare(@NonNull ResultShareDisplay resultShareDisplay1) {
        if (!resultShareDisplay1.canByDisplayResultShare()) {
            return false;
        }

        synchronized (displayResultShareProviderMapLock) {
            ResultShareDisplay resultShareDisplay = null;
            if (displayResultShareProviderMap != null) {
                resultShareDisplay = displayResultShareProviderMap.get(resultShareDisplay1.getDisplayResultShareKey());
            }
            if (resultShareDisplay == null) {
                return false;
            }

            resultShareDisplay.byDisplayResultShare(resultShareDisplay1);

            if (SLog.isLoggable(SLog.DEBUG)) {
                SLog.dmf(NAME, "display. by result sharer. %s -> %s",
                        resultShareDisplay1.getDisplayResultShareLog(), resultShareDisplay.getDisplayResultShareLog());
            }
            return true;
        }
    }

    public void registerDownloadResultShareProvider(@NonNull ResultShareDownload provider) {
        if (!provider.canUseResultShare()) {
            return;
        }

        synchronized (downloadResultShareProviderMapLock) {
            if (downloadResultShareProviderMap == null) {
                synchronized (this) {
                    if (downloadResultShareProviderMap == null) {
                        downloadResultShareProviderMap = new WeakHashMap<>();
                    }
                }
            }

            downloadResultShareProviderMap.put(provider.getDownloadResultShareKey(), provider);

            if (SLog.isLoggable(SLog.DEBUG)) {
                SLog.dmf(NAME, "download. register result sharer provider. %s",
                        provider.getDownloadResultShareLog());
            }
        }
    }

    public void unregisterDownloadResultShareProvider(@NonNull ResultShareDownload provider) {
        if (!provider.canUseResultShare()) {
            return;
        }

        ResultShareDownload resultShareDownload = null;
        synchronized (downloadResultShareProviderMapLock) {
            if (downloadResultShareProviderMap != null) {
                resultShareDownload = downloadResultShareProviderMap.remove(provider.getDownloadResultShareKey());
                if (resultShareDownload != null) {
                    if (SLog.isLoggable(SLog.DEBUG)) {
                        SLog.dmf(NAME, "download. unregister result sharer provider. %s",
                                resultShareDownload.getDownloadResultShareLog());
                    }
                }
            }
        }

        if (resultShareDownload != null) {
            Set<ResultShareDownload> downloadResultShareSet = resultShareDownload.getDownloadResultShareSet();
            if (downloadResultShareSet == null || downloadResultShareSet.size() == 0) {
                return;
            }

            String providerId = resultShareDownload.getDownloadResultShareLog();
            for (ResultShareDownload resultShareDownload1 : downloadResultShareSet) {
                if (!resultShareDownload1.isCanceled()) {
                    boolean success = resultShareDownload1.processDownloadResultShare();

                    if (SLog.isLoggable(SLog.DEBUG)) {
                        SLog.dmf(NAME, "download. callback result sharer. %s. %s  <-  %s",
                                success ? "success" : "failed", resultShareDownload1.getDownloadResultShareLog(), providerId);
                    }
                } else {
                    SLog.wmf(NAME, "download. callback result sharer. %s. %s  <-  %s",
                            "canceled", resultShareDownload1.getDownloadResultShareLog(), providerId);
                }
            }
            downloadResultShareSet.clear();
        }
    }

    public boolean byDownloadResultShare(@NonNull ResultShareDownload resultShareDownload) {
        if (!resultShareDownload.canUseResultShare()) {
            return false;
        }

        synchronized (downloadResultShareProviderMapLock) {
            ResultShareDownload resultShareDownload1 = null;
            if (downloadResultShareProviderMap != null) {
                resultShareDownload1 = downloadResultShareProviderMap.get(resultShareDownload.getDownloadResultShareKey());
            }
            if (resultShareDownload1 == null) {
                return false;
            }

            resultShareDownload1.byDownloadResultShare(resultShareDownload);

            if (SLog.isLoggable(SLog.DEBUG)) {
                SLog.dmf(NAME, "download. by result sharer. %s -> %s",
                        resultShareDownload.getDownloadResultShareLog(), resultShareDownload1.getDownloadResultShareLog());
            }
            return true;
        }
    }

    @NonNull
    @Override
    public String toString() {
        return NAME;
    }
}
