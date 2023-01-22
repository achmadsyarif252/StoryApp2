package com.example.storyapp

import com.example.storyapp.data.retrofit.response.ListStoryItem

object DataDummy {
    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val quote = ListStoryItem(
                "https://id.wikipedia.org/wiki/Batik#/media/Berkas:Batik_Indonesia.jpg",
                "2023-01-19T16:25:29.332Z",
                "Story hari ke-$i",
                "Deskripsi...",
                0.0,
                "",
                0.0,
            )
            items.add(quote)
        }
        return items
    }
}