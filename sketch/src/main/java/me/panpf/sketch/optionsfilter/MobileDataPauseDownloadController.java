package me.panpf.sketch.optionsfilter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

import java.lang.ref.WeakReference;

import me.panpf.sketch.Configuration;

/**
 * 全局移动数据或有流量限制的 WIFI 下暂停下载控制器
 */
public class MobileDataPauseDownloadController {
    private NetworkChangedBroadcastReceiver receiver;
    private boolean opened;
    private Configuration configuration;

    public MobileDataPauseDownloadController(Configuration configuration) {
        receiver = new NetworkChangedBroadcastReceiver(configuration.getContext(), this);
        this.configuration = configuration;
    }

    /**
     * 已经开启了？
     */
    public boolean isOpened() {
        return opened;
    }

    /**
     * 开启功能
     *
     * @param opened 开启
     */
    public void setOpened(boolean opened) {
        if (this.opened == opened) {
            return;
        }
        this.opened = opened;

        if (this.opened) {
            updateStatus(receiver.context);
            receiver.register();
        } else {
            configuration.setPauseDownloadEnabled(false);
            receiver.unregister();
        }
    }

    /**
     * 网络状态变化或初始化时更新全局暂停功能
     *
     * @param context {@link Context}
     */
    private void updateStatus(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        boolean pause = false;
        if (networkInfo != null && networkInfo.isAvailable()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                pause = true;
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && connectivityManager.isActiveNetworkMetered()) {
                    pause = true;
                }
            }
        }
        configuration.setPauseDownloadEnabled(pause);
    }

    /**
     * 监听网络变化的广播
     */
    private static class NetworkChangedBroadcastReceiver extends BroadcastReceiver {
        private Context context;
        private WeakReference<MobileDataPauseDownloadController> weakReference;

        public NetworkChangedBroadcastReceiver(Context context, MobileDataPauseDownloadController download) {
            this.context = context.getApplicationContext();
            this.weakReference = new WeakReference<>(download);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                MobileDataPauseDownloadController pauseDownloadController = weakReference.get();
                if (pauseDownloadController != null) {
                    pauseDownloadController.updateStatus(context);
                }
            }
        }

        private void register() {
            try {
                context.registerReceiver(this, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }

        private void unregister() {
            try {
                context.unregisterReceiver(this);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }
}
