package com.example.storyapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.model.UserModel
import com.example.storyapp.data.model.UserPreference
import com.example.storyapp.data.retrofit.api.ApiConfig
import com.example.storyapp.data.retrofit.response.LoginResponse
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val pref: UserPreference) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _msg = MutableLiveData<String>()
    val msg: LiveData<String> = _msg

    fun getUser(): LiveData<UserModel> {
        return pref.getUser().asLiveData()
    }

    fun authenticate(email: String, password: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().login(email, password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false

                _msg.value = t.message.toString()
            }

            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        _msg.value = "Login Success"
                        viewModelScope.launch {
                            val user = UserModel(
                                responseBody.loginResult.userId,
                                responseBody.loginResult.name,
                                true,
                                responseBody.loginResult.token
                            )
                            if (getUser().value == null) pref.saveUser(user)
                        }
                    } else {
                        _msg.value = responseBody?.message
                    }
                } else {
                    val responseBody = Gson().fromJson(
                        response.errorBody()?.charStream(),
                        LoginResponse::class.java
                    )
                    _msg.value = responseBody.message
                }
            }
        })

    }
}