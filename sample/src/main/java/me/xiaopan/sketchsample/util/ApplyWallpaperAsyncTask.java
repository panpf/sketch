package me.xiaopan.sketchsample.util;

import android.app.WallpaperManager;
import android.content.Context;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 应用壁纸异步任务
 */
public class ApplyWallpaperAsyncTask extends AsyncTask<Integer, Integer, Boolean> {
    private Context context;
    private File imageFile;

    public ApplyWallpaperAsyncTask(Context context, File imageFile) {
        this.context = context;
        this.imageFile = imageFile;
    }

    @Override
    protected Boolean doInBackground(Integer... params) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(imageFile);
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
            wallpaperManager.setStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }
}
