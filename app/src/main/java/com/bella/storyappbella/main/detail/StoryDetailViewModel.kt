package com.bella.storyappbella.main.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.bella.storyappbella.api.data.LoginPreference
import com.bella.storyappbella.api.data.UserDataModel

class StoryDetailViewModel(private val pref: LoginPreference) : ViewModel() {
    fun getUserData(): LiveData<UserDataModel> {
        return pref.getUserToken().asLiveData()
    }
}