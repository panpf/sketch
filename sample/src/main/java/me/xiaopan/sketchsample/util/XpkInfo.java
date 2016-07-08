package me.xiaopan.sketchsample.util;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Locale;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import me.xiaopan.sketch.util.SketchUtils;

/**
 * XPK信息包装类
 */
public class XpkInfo implements Serializable {
    private static final long serialVersionUID = -6368588590272075114L;

    private int versionCode;// 版本号
    private long dataSize = 0;// 数据大小
    private long apkSize = 0;// apk大小
    private String appName;// 应用名
    private String packageName; // 包名
    private String versionName;// 版本信息
    private String destination; // 数据文件夹位置

    public boolean isEmpty() {
        return destination == null || dataSize == 0 || apkSize == 0;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public long getDataSize() {
        return dataSize;
    }

    public long getApkSize() {
        return apkSize;
    }

    public String getAppName() {
        return appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getVersionName() {
        return versionName;
    }

    public String getDestination() {
        return destination;
    }

    @Override
    public String toString() {
        return SketchUtils.concat(" ", new Object[]{
                "apk(loc|size|pack)", this.apkSize,
                this.packageName, "\ndata(loc|size)", this.destination,
                this.dataSize, "\nresult(st|suc)"});
    }

    /**
     * 从manifest.xml中获取xpk的信息
     *
     * @throws ZipException
     * @throws XmlPullParserException
     * @throws IOException
     */
    @SuppressLint("SdCardPath")
    public static XpkInfo getXPKManifestDom(ZipFile zipFile) throws ZipException, XmlPullParserException, IOException {
        XpkInfo xpkDom = null;
        try {
            xpkDom = new XpkInfo();
            XmlPullParser parser = Xml.newPullParser();
            InputStream is = zipFile.getInputStream(zipFile.getEntry("manifest.xml"));

            parser.setInput(is, "utf-8");
            String defaultStoragePath = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ? Environment.getExternalStorageDirectory().getPath() : null;

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    // 数据包大小 long
                    if (parser.getName().equals("data")) {
                        xpkDom.dataSize = Long.parseLong(parser
                                .getAttributeValue(1));
                    }
                    // 数据包安装位置
                    else if (parser.getName().equals("destination")) {
                        xpkDom.destination = parser.nextText();
                        if (xpkDom.destination != null && defaultStoragePath != null) {
                            xpkDom.destination = xpkDom.destination.replace("/sdcard", defaultStoragePath);
                        }
                    }
                    // apk大小 long
                    else if (parser.getName().equals("apkinfo")) {
                        xpkDom.apkSize = Long.parseLong(parser
                                .getAttributeValue(0));
                    }
                    // 包名
                    else if (parser.getName().equals("package")) {
                        xpkDom.packageName = parser.nextText();
                    }
                    // 版本信息
                    else if (parser.getName().toLowerCase(Locale.getDefault())
                            .equals("versionname")) {
                        xpkDom.versionName = parser.nextText();
                    }
                    // 版本号
                    else if (parser.getName().toLowerCase(Locale.getDefault())
                            .equals("versioncode")) {
                        xpkDom.versionCode = Integer
                                .parseInt(parser.nextText());
                    }
                    // 游戏名
                    else if (parser.getName().equals("label")) {
                        parser.next();
                        xpkDom.appName = parser.getText();
                    }
                }
                eventType = parser.next();
            }
        } catch (ZipException e) {
            throw e;
        } catch (XmlPullParserException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        } catch (ClassCastException e) {
            e.printStackTrace();
            xpkDom = null;
        }
        return xpkDom;
    }
}