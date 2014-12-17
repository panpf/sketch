package me.xiaopan.android.spear.sample.net.request;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.xiaopan.android.gohttp.HttpRequest;
import me.xiaopan.android.gohttp.requestobject.Request;
import me.xiaopan.android.gohttp.requestobject.URL;

/**
 * 百度图片首页请求
 */
@URL("http://image.baidu.com/")
public class IndexRequest implements Request{
    public static class ResponseHandler implements HttpRequest.ResponseHandleCompletedAfterListener<String>{
        private static final String PREFIX = "var sliderData = [";
        private static final String SUFFIX = "];";

        private static final String PREFIX2 = "var data = ";
        private static final String SUFFIX2 = ";";

        @Override
        public Object onResponseHandleAfter(HttpRequest httpRequest, HttpResponse httpResponse, String baiduImageHomeSourceCode, boolean b, boolean b2) throws Throwable {
            Response response = new Response();
            response.setRecommendImages(parseRecommendImage(baiduImageHomeSourceCode));
            response.setImageCategories(parseImageCategory(baiduImageHomeSourceCode));
            return response;
        }

        public List<ImageGroup> parseRecommendImage(String baiduImageHomeSourceCode) throws JSONException {
            String sliderRegex = "var sliderData = \\[[\\d\\D\\s\\S]*?\\];";
            Matcher matcher2 = Pattern.compile(sliderRegex).matcher(baiduImageHomeSourceCode);
            if(matcher2.find()){
                String  sliderJson = matcher2.group();

                // 截掉前面的“var sliderData = “和后面的"];"
                if(sliderJson.length() < PREFIX.length()+SUFFIX.length()){
                    throw new IllegalArgumentException("轮播图数据异常，可能是百度把首页改了");
                }
                sliderJson = sliderJson.substring(PREFIX.length(), sliderJson.length()-SUFFIX.length()).trim();

                // 解析数据
                JSONArray jsonArray = new JSONArray(sliderJson);
                List<ImageGroup> imageList = null;
                for(int w = 0, size = jsonArray.length(); w < size; w++){
                    if(imageList == null){
                        imageList = new ArrayList<ImageGroup>();
                    }
                    imageList.add(ImageGroup.parse(jsonArray.getJSONObject(w)));
                }
                if(imageList == null || imageList.size() == 0){
                    throw new IllegalArgumentException("分类推荐中没有图片，可能是百度把首页改了");
                }

                return imageList;
            }else{
                return null;
            }
        }

        private List<ImageCategory> parseImageCategory(String baiduImageHomeSourceCode){
            String categoryRecommendRegex = "var data = \\[[\\d\\D\\s\\S]*?\\];";

            Matcher matcher = Pattern.compile(categoryRecommendRegex).matcher(baiduImageHomeSourceCode);
            List<ImageCategory> imageCategories = null;
            Gson gson = null;
            while(matcher.find()){
                String jsonContent = matcher.group();

                // 截掉前面的“var data = “和后面的";"
                if(jsonContent.length() < PREFIX2.length()+SUFFIX2.length()){
                    throw new IllegalArgumentException("分类推荐数据异常，可能是百度把首页改了");
                }
                jsonContent = jsonContent.substring(PREFIX2.length(), jsonContent.length()-SUFFIX2.length()).trim();

                // 解析
                if(gson == null){
                    gson = new Gson();
                }
                List<Image> imageList = gson.fromJson(jsonContent, new TypeToken<List<Image>>(){}.getType());
                if(imageList == null || imageList.size() == 0){
                    throw new IllegalArgumentException("分类推荐中没有图片，可能是百度把首页改了");
                }

                // 加入列表
                ImageCategory imageCategory = new ImageCategory();
                imageCategory.setImageList(imageList);
                imageCategory.setName(imageList.get(0).getCategory());
                if(imageCategories == null){
                    imageCategories = new ArrayList<ImageCategory>();
                }
                imageCategories.add(imageCategory);
            }

            return imageCategories;
        }
    }

    public static class Response {
        private List<ImageGroup> recommendImages;
        private List<ImageCategory> imageCategories;

        @Override
        public String toString() {
            return "recommendImages="+recommendImages.toString()+"; imageCategories="+imageCategories.toString();
        }

        public List<ImageGroup> getRecommendImages() {
            return recommendImages;
        }

        public void setRecommendImages(List<ImageGroup> recommendImages) {
            this.recommendImages = recommendImages;
        }

        public List<ImageCategory> getImageCategories() {
            return imageCategories;
        }

        public void setImageCategories(List<ImageCategory> imageCategories) {
            this.imageCategories = imageCategories;
        }
    }

    public static class ImageCategory{
        private String name;
        private String url;
        private List<Image> imageList;

        @Override
        public String toString() {
            return "name="+name+"; url="+url+"; imageList="+imageList.toString();
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public List<Image> getImageList() {
            return imageList;
        }

        public void setImageList(List<Image> imageList) {
            this.imageList = imageList;
        }
    }

    public static class Image {
        @SerializedName("image_id") private String id;
        @SerializedName("src") private String url;
        @SerializedName("url") private String link;
        @SerializedName("title") private String title;
        @SerializedName("width") private int width;
        @SerializedName("height") private int height;
        @SerializedName("col") private String category;
        @SerializedName("tag3") private String tag;

        @Override
        public String toString() {
            return "title="+title+"; id="+id+"; url="+url+"; link="+link+"; width="+width+"; height="+height+"; category="+category+"; tag="+tag;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

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

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }
    }

    public static class ImageGroup {
        @SerializedName("width") private int width;
        @SerializedName("height") private int height;
        @SerializedName("title") private String title;
        @SerializedName("coverNum") private String size;
        @SerializedName("src") private String url;
        @SerializedName("url") private String link;
        @SerializedName("column") private String category;

        @Override
        public String toString() {
            return "title="+title+"url="+url+"; link="+link+"; width="+width+"; height="+height+"; category="+category+"; size="+size;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

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

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public static ImageGroup parse(JSONObject jsonObject){
            ImageGroup imageGroup = new ImageGroup();
            try {
                imageGroup.setCategory(jsonObject.getString("column"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                imageGroup.setHeight(jsonObject.getInt("height"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                imageGroup.setLink(jsonObject.getString("url"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                imageGroup.setSize(jsonObject.getString("coverNum"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                imageGroup.setTitle(jsonObject.getString("title"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                imageGroup.setUrl(jsonObject.getString("src"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                imageGroup.setWidth(jsonObject.getInt("width"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return imageGroup;
        }
    }
}
