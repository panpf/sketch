package com.github.panpf.sketch.sample.ui

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyGridScope
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.runtime.Composable
import androidx.paging.compose.LazyPagingItems


@OptIn(ExperimentalFoundationApi::class)
public fun <T : Any> LazyGridScope.itemsIndexed(
    items: LazyPagingItems<T>,
    key: ((index: Int, item: T) -> Any)? = null,
    itemContent: @Composable LazyItemScope.(index: Int, value: T?) -> Unit
) {
    items(
        count = items.itemCount,
    ) { index ->
        itemContent(index, items[index])
    }
}



@SuppressLint("BanParcelableUsage")
private data class PagingPlaceholderKey2(private val index: Int) : Parcelable {
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(index)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        @Suppress("unused")
        @JvmField
        val CREATOR: Parcelable.Creator<PagingPlaceholderKey2> =
            object : Parcelable.Creator<PagingPlaceholderKey2> {
                override fun createFromParcel(parcel: Parcel) =
                    PagingPlaceholderKey2(parcel.readInt())

                override fun newArray(size: Int) = arrayOfNulls<PagingPlaceholderKey2?>(size)
            }
    }
}