package com.github.panpf.sketch.resize

import androidx.annotation.Keep
import com.github.panpf.sketch.decode.Transformed
import org.json.JSONObject

@Keep
class ResizeTransformed constructor(val resize: Resize) : Transformed {

    override val key: String by lazy {
        resize.key.replace("Resize", "ResizeTransformed")
    }
    override val cacheResultToDisk: Boolean = true

    @Keep
    constructor(jsonObject: JSONObject) : this(
        Class.forName(jsonObject.getString("resizeClassName"))
            .getConstructor(JSONObject::class.java)
            .newInstance(jsonObject.getJSONObject("resizeContent")) as Resize,
    )

    override fun serializationToJSON(): JSONObject =
        JSONObject().apply {
            put("resizeClassName", resize::class.java.name)
            put("resizeContent", resize.serializationToJSON())
        }

    override fun toString(): String = key
}

fun List<Transformed>.getResizeTransformed(): ResizeTransformed? =
    find { it is ResizeTransformed } as ResizeTransformed?