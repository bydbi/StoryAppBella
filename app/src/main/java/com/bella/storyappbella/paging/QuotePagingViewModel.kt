package com.bella.storyappbella.paging

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.bella.storyappbella.api.respon.ListStoryItem

class QuotePagingViewModel(private val storyRepository: QuoteRepository) : ViewModel() {

    val story: LiveData<PagingData<ListStoryItem>> =
        storyRepository.getQuote().cachedIn(viewModelScope)
}

class ViewModelPagingFactory(private val storyRepository: QuoteRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuotePagingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return QuotePagingViewModel(storyRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}