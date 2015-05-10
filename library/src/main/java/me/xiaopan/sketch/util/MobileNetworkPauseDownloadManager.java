package me.xiaopan.sketch.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import me.xiaopan.sketch.Sketch;

/**
 * 移动网络下暂停下载新图片管理器
 */
public class MobileNetworkPauseDownloadManager {
    private Context context;
    private BroadcastReceiver receiver;

    public MobileNetworkPauseDownloadManager(Context context) {
        this.context = context;
    }

    public void setPauseDownload(boolean pauseDownloadImage){
        if(pauseDownloadImage){
            updateStatus(context);
            if(receiver == null){
                receiver = new NetworkChangedBroadcastReceiver();
            }
            try{
                context.registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
            }catch(IllegalArgumentException e){

            }
        }else{
            Sketch.with(context).getConfiguration().setPauseDownload(false);
            if(receiver != null){
                try{
                    context.unregisterReceiver(receiver);
                }catch(IllegalArgumentException e){
                }
            }
        }
    }

    private void updateStatus(Context context){
        NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        boolean isPause = networkInfo != null && networkInfo.isAvailable() && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        Sketch.with(context).getConfiguration().setPauseDownload(isPause);
    }

    private class NetworkChangedBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                updateStatus(context);
            }
        }
    }
}
