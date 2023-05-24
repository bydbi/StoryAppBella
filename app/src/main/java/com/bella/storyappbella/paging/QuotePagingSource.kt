package com.bella.storyappbella.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bella.storyappbella.api.ApiService
import com.bella.storyappbella.api.data.LoginPreference
import com.bella.storyappbella.api.respon.ListStoryItem
import kotlinx.coroutines.flow.first

class QuotePagingSource(private val apiService: ApiService, private val pref: LoginPreference) :
    PagingSource<Int, ListStoryItem>() {

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val token = pref.getUserToken().first().token
            val responseData =
                apiService.getListStories("Bearer " + token, position, params.loadSize)
            Log.d("cek token", token)

            val stories = responseData.body()?.listStory ?: emptyList()

            LoadResult.Page(
                data = stories,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (stories.isNullOrEmpty()) null else position + 1
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}