package me.xiaopan.android.spear.sample.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.os.AsyncTask;

/**
 * 保存临时图片异步任务
 */
public class SaveTempImageAsyncTask extends AsyncTask<String, Integer, File> {

    private Context context;

    public SaveTempImageAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected File doInBackground(String... params) {
        // 从文件名称解析文件类型
        String fileType = parseFileType(params[0]);
        if(fileType == null){
            return null;
        }

        // 创建新文件
        File dir = context.getExternalCacheDir();
        if(dir == null){
            dir = context.getCacheDir();
        }
        if(dir == null){
            return null;
        }else if(!dir.exists()){
            dir.mkdirs();
        }
        File file = new File(dir, "temp."+fileType);
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        // 拷贝文件
        OutputStream outputStream = null;
        InputStream inputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            inputStream = context.getAssets().open(params[0]);
            byte[] data = new byte[1024];
            int length;
            while((length = inputStream.read(data)) != -1){
                outputStream.write(data, 0, length);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
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

        return file;
    }

    public static String parseFileType(String fileName){
        int lastIndexOf = fileName.lastIndexOf(".");
        if(lastIndexOf < 0){
            return null;
        }
        String fileType = fileName.substring(lastIndexOf+1);
        if(fileType == null || "".equals(fileType.trim())){
            return null;
        }
        return fileType;
    }
}