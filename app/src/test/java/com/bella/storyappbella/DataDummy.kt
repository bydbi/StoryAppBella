package com.bella.storyappbella

import com.bella.storyappbella.api.respon.ListStoryItem

object DataDummy {
    fun generateDummyQuoteRespon(): List<ListStoryItem> {
        val newsList = ArrayList<ListStoryItem>()
        for (i in 0..10) {
            val news = ListStoryItem(
                "Title $i",
                "2022-02-22T22:22:22Z",
                "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/commons/feature-1-kurikulum-global-3.png",
                "https://www.dicoding.com/",
                2.272829151,
                "lokasi",
                2.45672459872
            )
            newsList.add(news)
        }
        return newsList
    }
}