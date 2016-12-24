package me.xiaopan.sketch.feature;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.lang.ref.WeakReference;

import me.xiaopan.sketch.Sketch;

/**
 * 移动网络下全局暂停下载控制器
 */
public class MobileNetworkGlobalPauseDownloadController {
    private NetworkChangedBroadcastReceiver receiver;
    private boolean opened;

    public MobileNetworkGlobalPauseDownloadController(Context context) {
        context = context.getApplicationContext();
        receiver = new NetworkChangedBroadcastReceiver(context, this);
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
            Sketch.with(receiver.context).getConfiguration().setGlobalPauseDownload(false);
            receiver.unregister();
        }
    }

    /**
     * 网络状态变化或初始化时更新全局暂停功能
     *
     * @param context {@link Context}
     */
    private void updateStatus(Context context) {
        NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        boolean isPause = networkInfo != null && networkInfo.isAvailable() && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        Sketch.with(context).getConfiguration().setGlobalPauseDownload(isPause);
    }

    /**
     * 监听网络变化的广播
     */
    private static class NetworkChangedBroadcastReceiver extends BroadcastReceiver {
        private Context context;
        private WeakReference<MobileNetworkGlobalPauseDownloadController> downloadWeakReference;

        public NetworkChangedBroadcastReceiver(Context context, MobileNetworkGlobalPauseDownloadController download) {
            context = context.getApplicationContext();
            this.context = context;
            this.downloadWeakReference = new WeakReference<MobileNetworkGlobalPauseDownloadController>(download);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                MobileNetworkGlobalPauseDownloadController download = downloadWeakReference.get();
                if (download != null) {
                    download.updateStatus(context);
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
