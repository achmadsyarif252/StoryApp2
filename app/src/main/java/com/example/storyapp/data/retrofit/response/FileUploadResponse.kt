package com.example.storyapp.data.retrofit.response

import com.google.gson.annotations.SerializedName

data class FileUploadResponse(

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)
