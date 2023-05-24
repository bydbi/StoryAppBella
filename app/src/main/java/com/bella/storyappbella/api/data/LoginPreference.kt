package com.bella.storyappbella.api.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LoginPreference private constructor(private val dataStore: DataStore<Preferences>){

    fun getUserData(): Flow<UserModel> {
        return dataStore.data.map { preference ->
            UserModel(
                preference[NAME_KEY] ?: "",
                preference[EMAIL_KEY] ?: "",
                preference[PASSWORD_KEY] ?: "",
                preference[STATE_KEY] ?: false
            )
        }
    }

    suspend fun saveUserData(login: UserModel){
        dataStore.edit { preference ->
            preference[NAME_KEY] = login.name
            preference[EMAIL_KEY] = login.email
            preference[PASSWORD_KEY] = login.password
            preference[STATE_KEY] = login.isLogin
        }
    }

    suspend fun login(){
        dataStore.edit { preference ->
            preference[STATE_KEY] = true
        }
    }

    suspend fun logout(){
        dataStore.edit { preference ->
            preference[STATE_KEY] = false
        }
    }

    fun getUserToken(): Flow<UserDataModel> {
        return dataStore.data.map { preference ->
            UserDataModel(
                preference[TOKEN_KEY] ?: ""
            )
        }
    }

    suspend fun saveUserToken(userToken: UserDataModel){
        dataStore.edit { preference ->
            preference[TOKEN_KEY] = userToken.token
        }
    }

    suspend fun deleteToken(){
        dataStore.edit { preference ->
            preference[TOKEN_KEY] = ""
        }
    }
    companion object {
        @Volatile
        private var INSTANCE: LoginPreference? = null

        private val NAME_KEY = stringPreferencesKey("name")
        private val EMAIL_KEY = stringPreferencesKey("email")
        private val PASSWORD_KEY = stringPreferencesKey("password")
        private val STATE_KEY = booleanPreferencesKey("state")

        private val TOKEN_KEY = stringPreferencesKey("token")

        fun getInstance(dataStore: DataStore<Preferences>): LoginPreference{
            return INSTANCE ?: synchronized(this){
                val instance = LoginPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}