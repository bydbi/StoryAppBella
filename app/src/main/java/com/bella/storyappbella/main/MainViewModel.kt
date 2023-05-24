package com.bella.storyappbella.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.bella.storyappbella.api.data.LoginPreference
import com.bella.storyappbella.api.data.UserDataModel
import com.bella.storyappbella.api.data.UserModel
import kotlinx.coroutines.launch

class MainViewModel(private val pref: LoginPreference): ViewModel() {

    fun getLoginUser(): LiveData<UserModel> {
        return pref.getUserData().asLiveData()
    }

    fun logout(){
        viewModelScope.launch {
            pref.logout()
        }
    }

    fun getUserData(): LiveData<UserDataModel> {
        return pref.getUserToken().asLiveData()
    }

    fun deleteToken(){
        viewModelScope.launch {
            pref.deleteToken()
        }
    }
}