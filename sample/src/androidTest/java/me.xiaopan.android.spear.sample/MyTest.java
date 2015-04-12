package me.xiaopan.android.spear.sample;

import android.test.AndroidTestCase;
import android.text.format.Formatter;
import android.util.Log;

import java.io.File;

import me.xiaopan.android.spear.util.CommentUtils;

public class MyTest extends AndroidTestCase{

    public void testApkIcon() throws Exception{
        File cacheDir = new File(getContext().getExternalCacheDir()+File.separator+"spear");
        long useTime = System.currentTimeMillis();
        long size = CommentUtils.countFileLength(cacheDir);
        useTime = System.currentTimeMillis() - useTime;
        Log.e("Test", "用时："+useTime+"; 大小："+ Formatter.formatFileSize(getContext(), size));
    }
}