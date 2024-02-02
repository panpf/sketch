//package com.github.panpf.sketch.sample.ui.screen
//
//import androidx.paging.Pager
//import androidx.paging.PagingConfig
//import androidx.paging.cachedIn
//import app.cash.paging.PagingData
//import cafe.adriel.voyager.core.model.ScreenModel
//import cafe.adriel.voyager.core.model.screenModelScope
//import com.github.panpf.sketch.sample.ui.model.Photo
//import kotlinx.coroutines.flow.Flow
//
//class PexelsPhotoListViewModel : ScreenModel {
//
//    val pagingFlow: Flow<PagingData<Photo>> = Pager(
//        config = PagingConfig(
//            pageSize = 40,
//            enablePlaceholders = false,
//        ),
//        initialKey = 0,
//        pagingSourceFactory = {
//            PexelsPhotoListPagingSource()
//        }
//    ).flow.cachedIn(screenModelScope)
//}