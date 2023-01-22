package com.example.storyapp.view.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityDetailStoryBinding
import com.example.storyapp.data.retrofit.response.ListStoryItem

class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.list_story_page)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val data = intent.getParcelableExtra<ListStoryItem>(EXTRA_STORY)
        val empty = ListStoryItem("", "", "", "", 0.0, "", 0.0)
        populateView(data ?: empty)
    }

    private fun populateView(data: ListStoryItem) {
        Glide.with(this@DetailStoryActivity)
            .load(data.photoUrl)
            .into(binding.ivStory)


        binding.tvNama.text = data.name
        binding.tvDesc.text = data.description
        binding.tvDate.text = data.createdAt
    }

    companion object {
        const val EXTRA_STORY = "EXTRA_STORY"
    }
}