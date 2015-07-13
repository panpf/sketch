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
@URL("http://image.baidu.com/i")
@Method(MethodType.GET)
public class SearchImageRequest implements Request{
//    @Param private String tn = "resultjsonavatarnew";
//    @Param private String ie = "utf-8";
//    @Param private String word;
//    @Param private String cg = "girl";
//    @Param("pn") private int start;
//    @Param("rn") private int size = 60;
//    @Param private String z;
//    @Param private String fr;
//    @Param private String width;
//    @Param private String height;
//    @Param private String lm="-1";
//    @Param private String ic="0";
//    @Param private String s="0";

    @Param private String tn="resultjson_com";
    @Param private String ipn="rj";
    @Param private String ct="201326592";
    @Param private String cl="2";
    @Param private String lm="-1";
    @Param private String st="-1";
    @Param private String fm="index";
    @Param private String fr="";
    @Param private String sf="1";
    @Param private String fmq="";
    @Param private String pv="";
    @Param private String ic="0";
    @Param private String nc="1";
    @Param private String z="";
    @Param private String se="1";
    @Param private String showtab="0";
    @Param private String fb="0";
    @Param private String width="";
    @Param private String height="";
    @Param private String face="0";
    @Param private String istype="2";
    @Param private String ie="utf-8";
    @Param private String word;
    @Param private String oq;
    @Param private String rsp="-1";
    @Param private String oe="utf-8";
    @Param("rn") private int size = 60;
    @Param("pn") private int start = 0;

    public SearchImageRequest(String word) {
        this.word = word;
        this.oq = word;
    }

    public void setStart(int newStart){
        this.start = newStart;
    }

    public int getSize() {
        return size;
    }

    public static class Response{
        @SerializedName("listNum") private int total;
        @SerializedName("data") private List<Image> images;

        public int getTotal() {
            return total;
        }

        public List<Image> getImages() {
            return images;
        }
    }

    public static class Image extends StarImageRequest.Image{
        @SerializedName("largeTnImageUrl") private String sourceUrl;
        @SerializedName("width") private int width;
        @SerializedName("height") private int height;

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

    public static class ResponseHandler implements HttpRequest.ResponseHandleCompletedAfterListener<Response>{

        @Override
        public Object onResponseHandleAfter(HttpRequest httpRequest, HttpResponse httpResponse, Response response, boolean b, boolean b2) throws Throwable {
            if(response.getImages() != null){
                Iterator<Image> iterator = response.getImages().iterator();
                while (iterator.hasNext()){
                    Image image = iterator.next();
                    if(image.sourceUrl == null || "".equals(image.sourceUrl)){
                        iterator.remove();
                    }
                }
            }
            return response;
        }
    }
}
