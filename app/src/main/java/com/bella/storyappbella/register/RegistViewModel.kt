package com.bella.storyappbella.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bella.storyappbella.api.data.LoginPreference
import com.bella.storyappbella.api.data.UserModel
import kotlinx.coroutines.launch

class RegistViewModel(private val pref: LoginPreference) : ViewModel() {
    fun saveUser(login: UserModel){
        viewModelScope.launch {
            pref.saveUserData(login)
        }
    }
}