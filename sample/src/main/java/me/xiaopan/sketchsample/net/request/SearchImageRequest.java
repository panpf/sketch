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
import me.xiaopan.sketchsample.bean.BaiduSearchImage;

/**
 * 搜索百度图片
 */
@URL("http://image.baidu.com/search/acjson")
@Method(MethodType.GET)
public class SearchImageRequest implements Request {
    @Param("queryWord")
    String queryWord = "";
    @Param("word")
    String word = "";
    @Param("rn")
    private int size = 60;
    @Param("pn")
    private int start = 0;

    @Param("tn")
    String tn = "resultjson_com";
    @Param("ipn")
    String ipn = "rj";
    @Param("ct")
    String ct = "201326592";
    @Param("is")
    String is = "";
    @Param("fp")
    String fp = "result";
    @Param("cl")
    String cl = "2";
    @Param("lm")
    String lm = "-1";
    @Param("ie")
    String ie = "utf-8";
    @Param("oe")
    String oe = "utf-8";
    @Param("adpicid")
    String adpicid = "";
    @Param("st")
    String st = "-1";
    @Param("z")
    String z = "";
    @Param("ic")
    String ic = "0";
    @Param("s")
    String s = "";
    @Param("se")
    String se = "";
    @Param("tab")
    String tab = "";
    @Param("width")
    String width = "";
    @Param("height")
    String height = "";
    @Param("face")
    String face = "0";
    @Param("istype")
    String istype = "2";
    @Param("qc")
    String qc = "";
    @Param("nc")
    String nc = "1";
    @Param("fr")
    String fr = "";
    @Param("gsm")
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
        private List<BaiduSearchImage> baiduSearchImages;

        public int getTotal() {
            return total;
        }

        public List<BaiduSearchImage> getBaiduSearchImages() {
            return baiduSearchImages;
        }
    }

    public static class ResponseHandler implements HttpRequest.ResponseHandleCompletedAfterListener<Response> {

        @Override
        public Object onResponseHandleAfter(HttpRequest httpRequest, HttpResponse httpResponse, Response response, boolean b, boolean b2) throws Throwable {
            if (response.getBaiduSearchImages() != null) {
                Iterator<BaiduSearchImage> iterator = response.getBaiduSearchImages().iterator();
                while (iterator.hasNext()) {
                    BaiduSearchImage baiduSearchImage = iterator.next();
                    if (baiduSearchImage.getSourceUrl() == null || "".equals(baiduSearchImage.getSourceUrl())) {
                        iterator.remove();
                    }
                }
            }
            return response;
        }
    }
}
