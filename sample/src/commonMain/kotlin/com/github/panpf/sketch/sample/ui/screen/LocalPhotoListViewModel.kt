//package com.github.panpf.sketch.sample.ui.screen
//
//import androidx.paging.Pager
//import androidx.paging.PagingConfig
//import androidx.paging.cachedIn
//import cafe.adriel.voyager.core.model.ScreenModel
//import cafe.adriel.voyager.core.model.screenModelScope
//
//class LocalPhotoListViewModel : ScreenModel {
//
//    val pagingFlow = Pager(
//        config = PagingConfig(
//            pageSize = 40,
//            enablePlaceholders = false,
//        ),
//        initialKey = 0,
//        pagingSourceFactory = {
//            LocalPhotoListPagingSource()
//        }
//    ).flow.cachedIn(screenModelScope)
//}