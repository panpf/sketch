package me.xiaopan.android.spear.sample.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

/**
 * 保存图片异步任务
 */
public class SaveImageAsyncTask extends AsyncTask<String, Integer, String> {

    private Context context;
    private File imageFile;

    public SaveImageAsyncTask(Context context, File imageFile) {
        this.context = context;
        this.imageFile = imageFile;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        File dir = null;
        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            dir = new File(Environment.getExternalStorageDirectory(), "download");
        }
        if(dir == null){
            dir = context.getFilesDir();
        }
        if(dir == null){
            return "无法获取目录";
        }else if(!dir.exists()){
            dir.mkdirs();
        }

        File outImageFile = new File(dir, imageFile.getName());
        try {
            outImageFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return "创建文件失败，请重试";
        }

        OutputStream outputStream = null;
        InputStream inputStream = null;
        try {
            outputStream = new FileOutputStream(outImageFile);
            inputStream = new FileInputStream(imageFile);
            byte[] data = new byte[1024];
            int length;
            while((length = inputStream.read(data)) != -1){
                outputStream.write(data, 0, length);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "保存图片失败，请重试";
        } finally {
            if(outputStream != null){
                try {
                    outputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(inputStream != null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(outImageFile)));
        return "已成功保存到 "+outImageFile.getPath()+" 目录，您可以在您的相册中找到它";
    }

    @Override
    protected void onPostExecute(String s) {
        Toast.makeText(context, s, Toast.LENGTH_LONG).show();
    }
}