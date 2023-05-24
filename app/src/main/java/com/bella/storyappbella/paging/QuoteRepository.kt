package com.bella.storyappbella.paging

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.bella.storyappbella.api.ApiService
import com.bella.storyappbella.api.data.LoginPreference
import com.bella.storyappbella.api.respon.ListStoryItem

class QuoteRepository(private val apiService: ApiService, private val pref: LoginPreference) {
    fun getQuote(): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                QuotePagingSource(apiService, pref)
            }
        ).liveData
    }
}