package me.xiaopan.android.spear.sample.net.request;

import android.graphics.Color;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.json.JSONException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.xiaopan.android.gohttp.HttpRequest;
import me.xiaopan.android.gohttp.requestobject.Request;
import me.xiaopan.android.spear.Spear;
import me.xiaopan.android.spear.display.ColorFadeInImageDisplayer;
import me.xiaopan.android.spear.request.DisplayOptions;
import me.xiaopan.android.spear.sample.DisplayOptionsType;

/**
 * 明星主页请求
 */
public class StarHomeRequest implements Request{
    public static class HomeRequestResponseHandle implements HttpRequest.ResponseHandleCompletedAfterListener<String>{

        @Override
        public Object onResponseHandleAfter(HttpRequest httpRequest, HttpResponse httpResponse, String starHomeSourceCode, boolean b, boolean b2) throws Throwable {
            Response response = new Response();
            response.backgroundImageUrl = parseBackgroundImageUrl(starHomeSourceCode);
            response.backgroundColor = parseBackgroundColor(starHomeSourceCode);
            return response;
        }

        public String parseBackgroundImageUrl(String starHomeSourceCode) throws JSONException {
            String prefix = "style=\"background-image:url(";
            String suffix = ");";
            String categoryRecommendRegex = prefix+"[\\d\\D\\s\\S]*?"+suffix;
            Matcher matcher2 = Pattern.compile(categoryRecommendRegex).matcher(starHomeSourceCode);
            if(matcher2.find()){
                String backgroundImageUrl = matcher2.group();
                // 截掉前面的“var sliderData = “和后面的"];"
                if(backgroundImageUrl.length() < prefix.length()+suffix.length()){
                    throw new IllegalArgumentException();
                }
                backgroundImageUrl = backgroundImageUrl.substring(prefix.length(), backgroundImageUrl.length()-suffix.length()).trim();
                return backgroundImageUrl;
            }else{
                return null;
            }
        }

        private int parseBackgroundColor(String starHomeSourceCode){
            String prefix = "background-color:";
            String suffix = ";\">";
            String categoryRecommendRegex = prefix+"[\\d\\D\\s\\S]*?"+suffix;
            Matcher matcher2 = Pattern.compile(categoryRecommendRegex).matcher(starHomeSourceCode);
            int backgroundColor = Color.WHITE;
            if(matcher2.find()){
                String color = matcher2.group();
                // 截掉前面的“var sliderData = “和后面的"];"
                if(color.length() < prefix.length()+suffix.length()){
                    throw new IllegalArgumentException();
                }
                color = color.substring(prefix.length(), color.length()-suffix.length()).trim();
                try{
                    backgroundColor = Color.parseColor(color);
                }catch (IllegalArgumentException e){
                    e.printStackTrace();
                    Log.e(StarHomeRequest.class.getSimpleName(), "解析背景颜色失败："+color);
                }
            }
            ((DisplayOptions) Spear.getOptions(DisplayOptionsType.STAR_ITEM)).displayer(new ColorFadeInImageDisplayer(backgroundColor));
            return backgroundColor;
        }
    }

    public static class Response {
        private String backgroundImageUrl;
        private int backgroundColor;

        public String getBackgroundImageUrl() {
            return backgroundImageUrl;
        }

        public int getBackgroundColor() {
            return backgroundColor;
        }
    }
}
