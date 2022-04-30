package com.github.panpf.sketch.sample.ui.common.list

import android.content.Context
import androidx.core.view.isVisible
import androidx.paging.LoadState
import com.github.panpf.sketch.sample.databinding.LoadStateItemBinding

class LoadStateItemFactory :
    MyBindingItemFactory<LoadState, LoadStateItemBinding>(LoadState::class) {

    override fun initItem(
        context: Context,
        binding: LoadStateItemBinding,
        item: BindingItem<LoadState, LoadStateItemBinding>
    ) {
    }

    override fun bindItemData(
        context: Context,
        binding: LoadStateItemBinding,
        item: BindingItem<LoadState, LoadStateItemBinding>,
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
