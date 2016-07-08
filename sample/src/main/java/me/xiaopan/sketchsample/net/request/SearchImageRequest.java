package me.xiaopan.sketchsample.net.request;

import com.google.gson.annotations.SerializedName;

import org.apache.http.HttpResponse;

import java.util.Iterator;
import java.util.List;

import me.xiaopan.gohttp.HttpRequest;
import me.xiaopan.gohttp.MethodType;
import me.xiaopan.gohttp.requestobject.Method;
import me.xiaopan.gohttp.requestobject.Param;
import me.xiaopan.gohttp.requestobject.Request;
import me.xiaopan.gohttp.requestobject.URL;

/**
 * 搜索百度图片
 */
@URL("http://image.baidu.com/search/acjson")
@Method(MethodType.GET)
public class SearchImageRequest implements Request {
    @Param
    String queryWord = "";
    @Param
    String word = "";
    @Param("rn")
    private int size = 60;
    @Param("pn")
    private int start = 0;

    @Param
    String tn = "resultjson_com";
    @Param
    String ipn = "rj";
    @Param
    String ct = "201326592";
    @Param
    String is = "";
    @Param
    String fp = "result";
    @Param
    String cl = "2";
    @Param
    String lm = "-1";
    @Param
    String ie = "utf-8";
    @Param
    String oe = "utf-8";
    @Param
    String adpicid = "";
    @Param
    String st = "-1";
    @Param
    String z = "";
    @Param
    String ic = "0";
    @Param
    String s = "";
    @Param
    String se = "";
    @Param
    String tab = "";
    @Param
    String width = "";
    @Param
    String height = "";
    @Param
    String face = "0";
    @Param
    String istype = "2";
    @Param
    String qc = "";
    @Param
    String nc = "1";
    @Param
    String fr = "";
    @Param
    String gsm = "3c";
    @Param("1437824884073")
    String temp = "";

    public SearchImageRequest(String word) {
        this.word = word;
        this.queryWord = word;
    }

    public void setStart(int newStart) {
        this.start = newStart;
    }

    public int getSize() {
        return size;
    }

    public static class Response {
        @SerializedName("listNum")
        private int total;
        @SerializedName("data")
        private List<Image> images;

        public int getTotal() {
            return total;
        }

        public List<Image> getImages() {
            return images;
        }
    }

    public static class Image extends StarImageRequest.Image {
        @SerializedName("thumbURL")
        private String sourceUrl;
        @SerializedName("width")
        private int width;
        @SerializedName("height")
        private int height;

        public String getSourceUrl() {
            return sourceUrl;
        }

        @Override
        public String getThumbUrl() {
            return sourceUrl;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }
    }

    public static class ResponseHandler implements HttpRequest.ResponseHandleCompletedAfterListener<Response> {

        @Override
        public Object onResponseHandleAfter(HttpRequest httpRequest, HttpResponse httpResponse, Response response, boolean b, boolean b2) throws Throwable {
            if (response.getImages() != null) {
                Iterator<Image> iterator = response.getImages().iterator();
                while (iterator.hasNext()) {
                    Image image = iterator.next();
                    if (image.sourceUrl == null || "".equals(image.sourceUrl)) {
                        iterator.remove();
                    }
                }
            }
            return response;
        }
    }
}
