package me.xiaopan.sketchsample.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import me.xiaopan.sketch.decode.DataSource;
import me.xiaopan.sketch.util.SketchUtils;

/**
 * 保存图片异步任务
 */
public class SaveImageAsyncTask extends AsyncTask<String, Integer, File> {

    private Context context;
    private DataSource dataSource;
    private String imageUri;

    public SaveImageAsyncTask(Context context, DataSource dataSource, String imageUri) {
        this.context = context;
        this.dataSource = dataSource;
        this.imageUri = imageUri;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected File doInBackground(String... params) {
        File dir = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            dir = new File(Environment.getExternalStorageDirectory(), "download");
        }
        if (dir == null) {
            dir = context.getFilesDir();
        }
        if (dir == null) {
            return null;
        } else if (!dir.exists()) {
            dir.mkdirs();
        }

        File outImageFile = new File(dir, SketchUtils.generatorTempFileName(dataSource, imageUri));
        try {
            outImageFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        OutputStream outputStream = null;
        InputStream inputStream = null;
        try {
            outputStream = new FileOutputStream(outImageFile);
            inputStream = dataSource.getInputStream();
            byte[] data = new byte[1024];
            int length;
            while ((length = inputStream.read(data)) != -1) {
                outputStream.write(data, 0, length);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            SketchUtils.close(outputStream);
            SketchUtils.close(inputStream);
        }

        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(outImageFile)));
        return outImageFile;
    }

    @Override
    protected void onPostExecute(File outFile) {
        if (outFile != null) {
            File dir = outFile.getParentFile();
            Toast.makeText(context, "已保存到 " + dir.getPath() + "目录", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "保存图片失败，请重试", Toast.LENGTH_LONG).show();
        }
    }
}