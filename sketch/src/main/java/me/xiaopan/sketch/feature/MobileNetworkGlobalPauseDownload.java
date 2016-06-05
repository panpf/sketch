package me.xiaopan.sketch.feature;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import me.xiaopan.sketch.Sketch;

/**
 * 移动网络下全局暂停下载图片
 */
public class MobileNetworkGlobalPauseDownload {
    private Context context;
    private BroadcastReceiver receiver;
    private boolean opened;

    public MobileNetworkGlobalPauseDownload(Context context) {
        this.context = context;
    }

    public boolean isOpened() {
        return opened;
    }

    public void setOpened(boolean opened) {
        if(this.opened == opened){
            return;
        }
        this.opened = opened;

        if (this.opened) {
            updateStatus(context);
            if (receiver == null) {
                receiver = new NetworkChangedBroadcastReceiver();
            }
            try {
                context.registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
            } catch (IllegalArgumentException e) {

            }
        } else {
            Sketch.with(context).getConfiguration().setGlobalPauseDownload(false);
            if (receiver != null) {
                try {
                    context.unregisterReceiver(receiver);
                } catch (IllegalArgumentException e) {
                }
            }
        }
    }

    private void updateStatus(Context context) {
        NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        boolean isPause = networkInfo != null && networkInfo.isAvailable() && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        Sketch.with(context).getConfiguration().setGlobalPauseDownload(isPause);
    }

    private class NetworkChangedBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                updateStatus(context);
            }
        }
    }
}
