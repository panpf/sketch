package me.xiaopan.android.spear.sample.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import me.xiaopan.android.spear.Spear;

/**
 * 移动网络下暂停下载新图片管理器
 */
public class MobileNetworkPauseDownloadNewImageManager{
    private static MobileNetworkPauseDownloadNewImageManager instance;
    private Context context;
    private BroadcastReceiver receiver;

    private MobileNetworkPauseDownloadNewImageManager(Context context) {
        this.context = context;
    }

    public static MobileNetworkPauseDownloadNewImageManager with(Context context){
        if(instance == null){
            synchronized (MobileNetworkPauseDownloadNewImageManager.class){
                if(instance == null){
                    instance = new MobileNetworkPauseDownloadNewImageManager(context);
                }
            }
        }
        return instance;
    }

    public void setPauseDownloadImage(boolean pauseDownloadImage){
        try{
            if(pauseDownloadImage){
                updateStatus(context);
                if(receiver == null){
                    receiver = new NetworkChangedBroadcastReceiver();
                }
                context.registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
            }else{
                Spear.with(context).setPauseDownloadNewImage(false);
                if(receiver != null){
                    context.unregisterReceiver(receiver);
                }
            }
        }catch(IllegalArgumentException e){

        }
    }

    private static void updateStatus(Context context){
        NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        boolean isPause = networkInfo != null && networkInfo.isAvailable() && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        Spear.with(context).setPauseDownloadNewImage(isPause);
    }

    private static class NetworkChangedBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                updateStatus(context);
            }
        }
    }
}
