package me.panpf.sketch.sample.vt.bean

import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList

enum class BoundaryStatus {
    ZERO_ITEMS_LOADED,

    ITEM_AT_END_LOADED,

    ITEM_AT_FRONT_LOADED,
}

class BoundaryStatusCallback<T>(private val status: MutableLiveData<BoundaryStatus>) : PagedList.BoundaryCallback<T>() {

    override fun onZeroItemsLoaded() {
        super.onZeroItemsLoaded()
        status.value = BoundaryStatus.ZERO_ITEMS_LOADED
    }

    override fun onItemAtEndLoaded(itemAtEnd: T) {
        super.onItemAtEndLoaded(itemAtEnd)
        status.value = BoundaryStatus.ITEM_AT_END_LOADED
    }

    override fun onItemAtFrontLoaded(itemAtFront: T) {
        super.onItemAtFrontLoaded(itemAtFront)
        status.value = BoundaryStatus.ITEM_AT_FRONT_LOADED
    }
}
