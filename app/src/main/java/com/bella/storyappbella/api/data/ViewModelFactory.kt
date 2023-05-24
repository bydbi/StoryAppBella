package com.bella.storyappbella.api.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bella.storyappbella.login.LoginViewModel
import com.bella.storyappbella.main.MainViewModel
import com.bella.storyappbella.main.detail.StoryDetailViewModel
import com.bella.storyappbella.main.maps.MapsViewModel
import com.bella.storyappbella.main.story.UploadViewModel
import com.bella.storyappbella.register.RegistViewModel

class ViewModelFactory(private val pref: LoginPreference) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(pref) as T
            }
            modelClass.isAssignableFrom(RegistViewModel::class.java) -> {
                RegistViewModel(pref) as T
            }
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(pref) as T
            }
            modelClass.isAssignableFrom(StoryDetailViewModel::class.java) -> {
                StoryDetailViewModel(pref) as T
            }
            modelClass.isAssignableFrom(UploadViewModel::class.java) -> {
                UploadViewModel(pref) as T
            }
            modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                MapsViewModel(pref) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}