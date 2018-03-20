package me.panpf.sketch.sample.vt.bean

class ApiResponse<out DATA> constructor(private val status: Status, private val data: DATA?, val message: String?) {

    companion object {
        fun <DATA> success(data: DATA?): ApiResponse<DATA> {
            return ApiResponse(Status.SUCCESS, data, null)
        }

        @Suppress("unused")
        fun <DATA> error(msg: String, data: DATA?): ApiResponse<DATA> {
            return ApiResponse(Status.ERROR, data, msg)
        }

        fun <DATA> loading(data: DATA?): ApiResponse<DATA> {
            return ApiResponse(Status.LOADING, data, null)
        }
    }

    fun isLoadingStatus() = status == Status.LOADING

    @Suppress("unused")
    fun isSuccessStatus() = status == Status.SUCCESS

    fun isErrorStatus() = status == Status.ERROR

    fun isEmptyData() = data == null || (data is Collection<*> && data.isEmpty())

    fun getNoEmptyData(): DATA {
        if (!isEmptyData()) {
            // 依赖于 isEmptyData() 这里才能强制写 !!
            return data!!
        }
        throw IllegalArgumentException()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ApiResponse<*>

        if (status != other.status) return false
        if (data != other.data) return false
        if (message != other.message) return false

        return true
    }

    override fun hashCode(): Int {
        var result = status.hashCode()
        result = 31 * result + (data?.hashCode() ?: 0)
        result = 31 * result + (message?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "ApiResponse(status=$status, data=$data, message=$message)"
    }
}

enum class Status {
    LOADING,
    SUCCESS,
    ERROR,
}

