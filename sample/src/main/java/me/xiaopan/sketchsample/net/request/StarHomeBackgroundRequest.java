package me.xiaopan.sketchsample.net.request;

import android.graphics.Color;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.json.JSONException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.xiaopan.gohttp.HttpRequest;
import me.xiaopan.gohttp.requestobject.Request;

/**
 * 明星个人主页背景请求
 */
public class StarHomeBackgroundRequest implements Request{
    public static class ResponseHandler implements HttpRequest.ResponseHandleCompletedAfterListener<String>{

        @Override
        public Object onResponseHandleAfter(HttpRequest httpRequest, HttpResponse httpResponse, String starHomeSourceCode, boolean b, boolean b2) throws Throwable {
            Background background = new Background();
            background.backgroundImageUrl = parseBackgroundImageUrl(starHomeSourceCode);
            background.backgroundColor = parseBackgroundColor(starHomeSourceCode);
            return background;
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
                    Log.e("StarHomeBackground", "解析背景颜色失败："+color);
                }
            }
            return backgroundColor;
        }
    }

    public static class Background {
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
