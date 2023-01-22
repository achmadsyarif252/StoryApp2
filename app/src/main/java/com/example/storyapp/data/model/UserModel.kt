package com.example.storyapp.data.model

data class UserModel(
    val uId: String,
    val name: String,
    val isLogin: Boolean,
    val token: String = ""
)