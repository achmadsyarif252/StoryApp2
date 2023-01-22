package com.example.storyapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.paging.*
import com.example.storyapp.data.retrofit.api.ApiConfig
import com.example.storyapp.data.retrofit.api.ApiService
import com.example.storyapp.data.retrofit.response.FileUploadResponse
import com.example.storyapp.data.retrofit.response.ListStoryItem
import com.example.storyapp.data.retrofit.response.LoginResponse
import com.example.storyapp.data.retrofit.response.StoryResponse
import com.example.storyapp.database.StoryDatabase
import com.example.storyapp.database.StoryRemoteMediator
import com.google.gson.Gson
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class StoryRepository(
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService,
) {
    private val result = MediatorLiveData<Result<List<ListStoryItem>>>()
    private val resultUpload = MediatorLiveData<Result<FileUploadResponse>>()

    @OptIn(ExperimentalPagingApi::class)
    fun getStory(token: String): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(token, storyDatabase, apiService),
            pagingSourceFactory = {
                storyDatabase.storyDao().getALlStories()
            }
        ).liveData
    }

    fun getStoryLocation(token: String): LiveData<Result<List<ListStoryItem>>> {
        result.value = Result.Loading
        val client = ApiConfig.getApiService().getStoryLocation("Bearer $token")
        client.enqueue(object : Callback<StoryResponse> {
            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                result.value = Result.Error(t.message.toString())
            }

            override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        result.value = Result.Success(responseBody.listStory)
                    }
                }
            }
        })
        return result
    }

    fun uploadToServer(
        token: String,
        imageMultipart: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody,
        lon: RequestBody
    ): LiveData<Result<FileUploadResponse>> {
        resultUpload.value = Result.Loading
        val client =
            ApiConfig.getApiService()
                .uploadImage("Bearer $token", imageMultipart, description, lat, lon)

        client.enqueue(object : Callback<FileUploadResponse> {
            override fun onResponse(
                call: Call<FileUploadResponse>,
                response: Response<FileUploadResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if ((responseBody != null) && (responseBody.error == false)) {
                        resultUpload.value = Result.Success(FileUploadResponse(false, "Success"))
                    }
                } else {
                    val responseBody = Gson().fromJson(
                        response.errorBody()?.charStream(),
                        LoginResponse::class.java
                    )
                    resultUpload.value =
                        Result.Success(FileUploadResponse(true, responseBody.message))
                }
            }

            override fun onFailure(call: Call<FileUploadResponse>, t: Throwable) {
                Result.Success(FileUploadResponse(true, "Failed"))

            }

        })
        return resultUpload
    }
}