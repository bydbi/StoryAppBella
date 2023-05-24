package com.bella.storyappbella.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.bella.storyappbella.api.data.LoginPreference
import com.bella.storyappbella.api.data.UserDataModel
import com.bella.storyappbella.api.data.UserModel
import kotlinx.coroutines.launch

class LoginViewModel(private val pref: LoginPreference) : ViewModel() {
    fun getUser(): LiveData<UserModel> {
        return pref.getUserData().asLiveData()
    }

    fun saveUserData(userData: UserDataModel){
        viewModelScope.launch {
            pref.saveUserToken(userData)
        }
    }

    fun login(){
        viewModelScope.launch {
            pref.login()
        }
    }
}