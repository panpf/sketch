package me.xiaopan.spear.sample.net.request;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import me.xiaopan.android.gohttp.HttpRequest;
import me.xiaopan.android.gohttp.requestobject.Request;
import me.xiaopan.spear.sample.net.NetUtils;

/**
 * 热门明星请求
 */
public abstract class HotStarRequest implements Request{

    public static class ResponseHandler implements HttpRequest.ResponseHandleCompletedAfterListener<String> {
        private String starType;

        public ResponseHandler(boolean isMan) {
            this.starType = isMan?"男明星":"女明星";
        }

        @Override
        public Object onResponseHandleAfter(HttpRequest httpRequest, HttpResponse httpResponse, String sourceContent, boolean b, boolean b2) throws Throwable {
            List<HotStar> hotStarList = new LinkedList<>();
            hotStarList.add(new HotStar("内地最热"+starType, parse(NetUtils.substring(sourceContent, "type : \"nd\",", "\\}\\)\\;", "imgs : "))));
            hotStarList.add(new HotStar("港台最热"+starType, parse(NetUtils.substring(sourceContent, "type : \"gt\",", "\\}\\)\\;", "imgs : "))));
            hotStarList.add(new HotStar("日韩最热"+starType, parse(NetUtils.substring(sourceContent, "type : \"rh\",", "\\}\\)\\;", "imgs : "))));
            hotStarList.add(new HotStar("欧美最热"+starType, parse(NetUtils.substring(sourceContent, "type : \"om\",", "\\}\\)\\;", "imgs : "))));
            return hotStarList;
        }

        private List<Star> parse(String jsonContent){

            if(jsonContent == null){
                return null;
            }
            JSONArray jsonArray;
            try {
                jsonArray = new JSONArray(jsonContent);
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }

            List<Star> stars = new ArrayList<Star>();
            for(int w = 0; w < jsonArray.length(); w++){
                JSONObject jsonObject;
                try {
                    jsonObject = jsonArray.getJSONObject(w);
                } catch (JSONException e) {
                    e.printStackTrace();
                    continue;
                }

                Star star = new Star();
                star.setName(jsonObject.optString("name"));
                JSONObject imagsJsonObject1;
                try {
                    imagsJsonObject1 = jsonObject.getJSONObject("imgs");
                } catch (JSONException e) {
                    e.printStackTrace();
                    continue;
                }
                Image widthImage = new Image();
                widthImage.setWidth(imagsJsonObject1.optInt("w_width"));
                widthImage.setHeight(imagsJsonObject1.optInt("w_height"));
                widthImage.setUrl(imagsJsonObject1.optString("w_src"));

                Image heightImage = new Image();
                heightImage.setWidth(imagsJsonObject1.optInt("h_width"));
                heightImage.setHeight(imagsJsonObject1.optInt("h_height"));
                heightImage.setUrl(imagsJsonObject1.optString("h_src"));

                star.setHeightImage(heightImage);
                star.setWidthImage(widthImage);
                stars.add(star);
            }
            return stars;
        }
    }

    public static class HotStar {
        private String name;
        private List<Star> starList;

        public HotStar(String name, List<Star> starList) {
            this.name = name;
            this.starList = starList;
        }

        public String getName() {
            return name;
        }

        public List<Star> getStarList() {
            return starList;
        }
    }

    public static class Star{
       private String name;
       private Image widthImage;
       private Image heightImage;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Image getWidthImage() {
            return widthImage;
        }

        public void setWidthImage(Image widthImage) {
            this.widthImage = widthImage;
        }

        public Image getHeightImage() {
            return heightImage;
        }

        public void setHeightImage(Image heightImage) {
            this.heightImage = heightImage;
        }
    }

    public static class Image{
        private int width;
        private int height;
        private String url;

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
