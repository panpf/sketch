package me.xiaopan.android.spear.sample.net.request;

import com.google.gson.annotations.SerializedName;

import org.apache.http.HttpResponse;

import java.util.Iterator;
import java.util.List;

import me.xiaopan.android.gohttp.HttpRequest;
import me.xiaopan.android.gohttp.MethodType;
import me.xiaopan.android.gohttp.requestobject.Method;
import me.xiaopan.android.gohttp.requestobject.Param;
import me.xiaopan.android.gohttp.requestobject.Request;
import me.xiaopan.android.gohttp.requestobject.URL;

/**
 * 明星
 */
@URL("http://image.baidu.com/data/star/listjson")
@Method(MethodType.GET)
public class StarImageRequest implements Request{
    @Param("pn") private int start = 0;
    @Param("rn") private int size = 60;
    @Param("name") private String name;
    @Param("sorttype") private int sorttype = 0;
    @Param("p") private String page = "star.home";
    @Param("col") private String col = "明星";
    @Param("tag") private String tag;

    public StarImageRequest(String name) {
        this.name = name;
        this.tag = name;
    }

    public void setStart(int newStart){
        this.start = newStart;
    }

    public int getSize() {
        return size;
    }

    public static class Response{
        @SerializedName("totalNum") private int total;
        @SerializedName("return_number") private int returnNumber;
        @SerializedName("data") private List<Image> images;

        public int getTotal() {
            return total;
        }

        public List<Image> getImages() {
            return images;
        }
    }

    public static class Image{
        @SerializedName("thumbnail_url") private String thumbUrl;
        @SerializedName("image_url") private String sourceUrl;
        @SerializedName("image_width") private int width;
        @SerializedName("image_height") private int height;
        private String imageSizeStr;

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

        public String getImageSizeStr() {
            if(imageSizeStr == null){
                imageSizeStr = width+"x"+height;
            }
            return imageSizeStr;
        }
    }

    public static class ResponseHandler implements HttpRequest.ResponseHandleCompletedAfterListener<Response>{

        @Override
        public Object onResponseHandleAfter(HttpRequest httpRequest, HttpResponse httpResponse, Response response, boolean b, boolean b2) throws Throwable {
            if(response.getImages() != null && response.returnNumber < response.getImages().size()){
                Iterator<Image> iterator = response.getImages().iterator();
                int number = 0;
                while (iterator.hasNext()){
                    iterator.next();
                    if(number++ >= response.returnNumber){
                        iterator.remove();
                    }
                }
            }
            return response;
        }
    }
}
