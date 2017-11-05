package me.panpf.sketch.sample.bean

import android.text.TextUtils

import com.google.gson.annotations.SerializedName

class UnsplashImage {
    @SerializedName("id")
    var id: String? = null

    @SerializedName("created_at")
    var createdAt: String? = null

    @SerializedName("updated_at")
    var updatedAt: String? = null

    @SerializedName("width")
    var width: Int = 0

    @SerializedName("height")
    var height: Int = 0

    @SerializedName("color")
    var color: String? = null

    @SerializedName("user")
    var user: User? = null

    @SerializedName("urls")
    var urls: Urls? = null

    @SerializedName("likes")
    var likes: Int = 0

    @SerializedName("liked_by_user")
    var likedByUser: Boolean = false

    @Transient private var formattedUpdateDate: String? = null

    fun getFormattedUpdateDate(): String {
        if (formattedUpdateDate == null && !TextUtils.isEmpty(updatedAt) && updatedAt!!.length >= 10) {
            formattedUpdateDate = updatedAt!!.substring(0, 10).replace("-", ".")
        }
        return formattedUpdateDate!!
    }

    class User {
        @SerializedName("id")
        var id: String? = null

        @SerializedName("updatedAt")
        var updated_at: String? = null

        @SerializedName("username")
        var username: String? = null

        @SerializedName("name")
        var name: String? = null

        @SerializedName("first_name")
        var firstName: String? = null

        @SerializedName("last_name")
        var lastName: String? = null

        @SerializedName("portfolio_url")
        var portfolioUrl: String? = null

        @SerializedName("bio")
        var bio: String? = null

        @SerializedName("location")
        var location: String? = null

        @SerializedName("total_likes")
        var totalLikes: Int = 0

        @SerializedName("total_photos")
        var totalPhotos: Int = 0

        @SerializedName("total_collections")
        var totalCollections: Int = 0

        @SerializedName("profile_image")
        var profileImage: ProfileImage? = null

        @SerializedName("links")
        var links: Links? = null

        class ProfileImage {
            @SerializedName("small")
            var small: String? = null

            @SerializedName("medium")
            var medium: String? = null

            @SerializedName("large")
            var large: String? = null
        }

        class Links {
            @SerializedName("self")
            var self: String? = null

            @SerializedName("html")
            var html: String? = null

            @SerializedName("photos")
            var photos: String? = null

            @SerializedName("likes")
            var likes: String? = null

            @SerializedName("portfolio")
            var portfolio: String? = null

            @SerializedName("following")
            var following: String? = null

            @SerializedName("followers")
            var followers: String? = null
        }
    }

    class Urls {
        @SerializedName("raw")
        var raw: String? = null

        @SerializedName("full")
        var full: String? = null

        @SerializedName("regular")
        var regular: String? = null

        @SerializedName("small")
        var small: String? = null

        @SerializedName("thumb")
        var thumb: String? = null
    }
}

