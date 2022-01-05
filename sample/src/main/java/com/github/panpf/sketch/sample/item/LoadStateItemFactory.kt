package com.github.panpf.sketch.sample.item

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import com.github.panpf.assemblyadapter.BindingItemFactory
import com.github.panpf.sketch.sample.databinding.ItemLoadStateBinding

class LoadStateItemFactory :
    BindingItemFactory<LoadState, ItemLoadStateBinding>(LoadState::class) {

    override fun createItemViewBinding(
        context: Context,
        inflater: LayoutInflater,
        parent: ViewGroup
    ) = ItemLoadStateBinding.inflate(inflater, parent, false)

    override fun initItem(
        context: Context,
        binding: ItemLoadStateBinding,
        item: BindingItem<LoadState, ItemLoadStateBinding>
    ) {
    }

    override fun bindItemData(
        context: Context,
        binding: ItemLoadStateBinding,
        item: BindingItem<LoadState, ItemLoadStateBinding>,
        bindingAdapterPosition: Int,
        absoluteAdapterPosition: Int,
        data: LoadState
    ) {
        binding.loadStateItemLoadingGroup.isVisible = data is LoadState.Loading
        binding.loadStateItemErrorText.isVisible = data is LoadState.Error
        binding.loadStateItemEndText.isVisible =
            data is LoadState.NotLoading && data.endOfPaginationReached
    }
}
