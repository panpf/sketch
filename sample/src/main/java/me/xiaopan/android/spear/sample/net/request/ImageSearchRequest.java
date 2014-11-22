package me.xiaopan.android.spear.sample.net.request;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import me.xiaopan.android.gohttp.MethodType;
import me.xiaopan.android.gohttp.requestobject.Method;
import me.xiaopan.android.gohttp.requestobject.Param;
import me.xiaopan.android.gohttp.requestobject.Request;
import me.xiaopan.android.gohttp.requestobject.URL;

/**
 * 搜索百度图片
 */
@URL("http://image.baidu.com/i")
@Method(MethodType.GET)
public class ImageSearchRequest implements Request{
    @Param private String tn = "resultjsonavatarnew";
    @Param private String ie = "utf-8";
    @Param private String word;
    @Param private String cg = "girl";
    @Param("pn") private int start;
    @Param private String rn = "60";
    @Param private String z;
    @Param private String fr;
    @Param private String width;
    @Param private String height;
    @Param private String lm="-1";
    @Param private String ic="0";
    @Param private String s="0";

    public ImageSearchRequest(String word) {
        this.word = word;
    }

    public void setStart(int newStart){
        this.start = newStart;
    }

    public static class Response{
        @SerializedName("imgtotal") private int total;
        @SerializedName("imgs") private List<Image> images;

        public int getTotal() {
            return total;
        }

        public List<Image> getImages() {
            return images;
        }
    }

    public static class Image{
        @SerializedName("thumbURL") private String thumbUrl;
        @SerializedName("objURL") private String sourceUrl;
        private int width;
        private int height;

        public String getThumbUrl() {
            return thumbUrl;
        }

        public String getSourceUrl() {
            return sourceUrl;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }
    }
}
