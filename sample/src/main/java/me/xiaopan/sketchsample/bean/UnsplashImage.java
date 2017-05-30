package me.xiaopan.sketchsample.bean;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

public class UnsplashImage {
    @SerializedName("id")
    public String id;

    @SerializedName("created_at")
    public String createdAt;

    @SerializedName("updated_at")
    public String updatedAt;

    @SerializedName("width")
    public int width;

    @SerializedName("height")
    public int height;

    @SerializedName("color")
    public String color;

    @SerializedName("user")
    public User user;

    @SerializedName("urls")
    public Urls urls;

    @SerializedName("likes")
    public int likes;

    @SerializedName("liked_by_user")
    public boolean likedByUser;

    private transient String formattedUpdateDate;

    public String getFormattedUpdateDate() {
        if (formattedUpdateDate == null && !TextUtils.isEmpty(updatedAt) && updatedAt.length() >= 10) {
            formattedUpdateDate = updatedAt.substring(0, 10).replace("-", ".");
        }
        return formattedUpdateDate;
    }

    public static class User {
        @SerializedName("id")
        public String id;

        @SerializedName("updatedAt")
        public String updated_at;

        @SerializedName("username")
        public String username;

        @SerializedName("name")
        public String name;

        @SerializedName("first_name")
        public String firstName;

        @SerializedName("last_name")
        public String lastName;

        @SerializedName("portfolio_url")
        public String portfolioUrl;

        @SerializedName("bio")
        public String bio;

        @SerializedName("location")
        public String location;

        @SerializedName("total_likes")
        public int totalLikes;

        @SerializedName("total_photos")
        public int totalPhotos;

        @SerializedName("total_collections")
        public int totalCollections;

        @SerializedName("profile_image")
        public ProfileImage profileImage;

        @SerializedName("links")
        public Links links;

        public static class ProfileImage {
            @SerializedName("small")
            public String small;

            @SerializedName("medium")
            public String medium;

            @SerializedName("large")
            public String large;
        }

        public static class Links {
            @SerializedName("self")
            public String self;

            @SerializedName("html")
            public String html;

            @SerializedName("photos")
            public String photos;

            @SerializedName("likes")
            public String likes;

            @SerializedName("portfolio")
            public String portfolio;

            @SerializedName("following")
            public String following;

            @SerializedName("followers")
            public String followers;
        }
    }

    public static class Urls {
        @SerializedName("raw")
        public String raw;

        @SerializedName("full")
        public String full;

        @SerializedName("regular")
        public String regular;

        @SerializedName("small")
        public String small;

        @SerializedName("thumb")
        public String thumb;
    }
}

