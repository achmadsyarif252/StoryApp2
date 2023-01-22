package com.example.storyapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.storyapp.data.StoryRepository
import com.example.storyapp.data.retrofit.response.FileUploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import com.example.storyapp.data.Result

class AddStoryViewModel(
    private val storyRepository: StoryRepository,
) : ViewModel() {
    fun uploadToServer(
        token: String,
        imageMultipart: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody,
        lon: RequestBody
    ): LiveData<Result<FileUploadResponse>> =
        storyRepository.uploadToServer(token, imageMultipart, description, lat, lon)
}
