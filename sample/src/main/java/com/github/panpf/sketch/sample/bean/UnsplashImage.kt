package com.github.panpf.sketch.sample.bean

import com.github.panpf.assemblyadapter.recycler.DiffKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
class UnsplashImage(
    @SerialName("id") val id: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
    @SerialName("width") val width: Int = 0,
    @SerialName("height") val height: Int = 0,
    @SerialName("color") val color: String? = null,
    @SerialName("user") val user: User? = null,
    @SerialName("urls") val urls: Urls? = null,
    @SerialName("likes") val likes: Int = 0,
    @SerialName("liked_by_user") val likedByUser: Boolean = false,
) : DiffKey {

    val formattedUpdateDate: String? by lazy {
        updatedAt?.substring(0, 10)?.replace("-", ".")
    }

    @Transient
    override val diffKey: String = "UnsplashImage-${urls?.raw ?: urls?.full ?: id ?: throw IllegalArgumentException("No diffKey")}"

    @Serializable
    class User(
        @SerialName("id") val id: String? = null,
        @SerialName("updatedAt") val updated_at: String? = null,
        @SerialName("username") val username: String? = null,
        @SerialName("name") val name: String? = null,
        @SerialName("first_name") val firstName: String? = null,
        @SerialName("last_name") val lastName: String? = null,
        @SerialName("portfolio_url") val portfolioUrl: String? = null,
        @SerialName("bio") val bio: String? = null,
        @SerialName("location") val location: String? = null,
        @SerialName("total_likes") val totalLikes: Int = 0,
        @SerialName("total_photos") val totalPhotos: Int = 0,
        @SerialName("total_collections") val totalCollections: Int = 0,
        @SerialName("profile_image") val profileImage: ProfileImage? = null,
        @SerialName("links") val links: Links? = null,
    ) {

        @Serializable
        class ProfileImage(
            @SerialName("small") val small: String? = null,
            @SerialName("medium") val medium: String? = null,
            @SerialName("large") val large: String? = null,
        )

        @Serializable
        class Links(
            @SerialName("self") val self: String? = null,
            @SerialName("html") val html: String? = null,
            @SerialName("photos") val photos: String? = null,
            @SerialName("likes") val likes: String? = null,
            @SerialName("portfolio") val portfolio: String? = null,
            @SerialName("following") val following: String? = null,
            @SerialName("followers") val followers: String? = null,
        )
    }

    @Serializable
    class Urls(
        @SerialName("raw") val raw: String? = null,
        @SerialName("full") val full: String? = null,
        @SerialName("regular") val regular: String? = null,
        @SerialName("small") val small: String? = null,
        @SerialName("thumb") val thumb: String? = null,
    )
}

