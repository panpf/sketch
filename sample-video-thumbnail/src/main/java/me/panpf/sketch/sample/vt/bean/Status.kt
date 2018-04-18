package me.panpf.sketch.sample.vt.bean

class Status(private val status: Int, val message: String? = null) {

    fun isLoading() = status == 0

    fun isSuccess() = status == 1

    fun isError() = status == -1

    companion object {
        @Suppress("unused")
        fun success(): Status {
            return Status(1)
        }

        @Suppress("unused")
        fun error(msg: String): Status {
            return Status(-1, msg)
        }

        @Suppress("unused")
        fun loading(): Status {
            return Status(0)
        }
    }
}