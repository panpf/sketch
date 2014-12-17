package me.xiaopan.android.spear.sample.net;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NetUtils {

    public static String substring(String sourceContent, String prefix, String suffix, String subPrefix){
        String categoryRecommendRegex = prefix+"[\\d\\D\\s\\S]*?"+suffix;
        Matcher matcher = Pattern.compile(categoryRecommendRegex).matcher(sourceContent);
        if(!matcher.find()){
            return null;
        }
        String fullJsonContent = matcher.group();
        // 截掉prefix和suffix
        if(fullJsonContent.length() < prefix.length()+suffix.length()){
            return null;
        }
        fullJsonContent = fullJsonContent.substring(prefix.length(), fullJsonContent.length()-suffix.length()).trim();
        if(subPrefix != null && fullJsonContent.length() > subPrefix.length()){
            fullJsonContent = fullJsonContent.substring(subPrefix.length(), fullJsonContent.length());
        }
        return fullJsonContent;
    }
}
