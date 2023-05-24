package com.bella.storyappbella.api.respon

data class LoginRespon(
    val loginResult: LoginResult,
    val error: Boolean,
    val message: String
)

data class LoginResult(
    val name: String,
    val userId: String,
    val token: String
)