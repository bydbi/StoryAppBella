package com.bella.storyappbella.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.bella.storyappbella.api.ApiConfig
import com.bella.storyappbella.api.data.LoginPreference
import com.bella.storyappbella.api.data.UserDataModel
import com.bella.storyappbella.api.data.UserModel
import com.bella.storyappbella.api.data.ViewModelFactory
import com.bella.storyappbella.api.respon.LoginRespon
import com.bella.storyappbella.databinding.ActivityLoginBinding
import com.bella.storyappbella.main.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("Setting")

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var user: UserModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setViewModel()
        setView()
        setAnimation()

        binding.edtPassLog.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                validationPassword()
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                validationPassword()
            }

            override fun afterTextChanged(s: Editable) {

            }

        })

        binding.btnLogin.setOnClickListener {
            val email = binding.edtEmailLog.text.toString()
            val password = binding.edtPassLog.text.toString()

            postLogin(email, password)
            loginViewModel.login()

        }
    }

    private fun validationPassword(){
        val result = binding.edtPassLog.text
        binding.btnLogin.isEnabled = result != null && result.toString().isNotEmpty() && result.length >= 8
    }

    private fun setView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setViewModel(){
        loginViewModel = ViewModelProvider(
            this,
            ViewModelFactory(LoginPreference.getInstance(dataStore))
        )[LoginViewModel::class.java]

        loginViewModel.getUser().observe(this, {user ->
            this.user = user
        })
    }



    private fun postLogin(email: String, password: String) {
        val client = ApiConfig.getApiService().login(email, password)
        client.enqueue(object : Callback<LoginRespon> {
            override fun onResponse(
                call: Call<LoginRespon>,
                response: Response<LoginRespon>
            ) {
                if(response.isSuccessful){
                    val responsBody = response.body()
                    if (responsBody!= null && !responsBody.error){
                        val token = responsBody.loginResult.token
                        loginViewModel.saveUserData(UserDataModel(token))
                        Toast.makeText(this@LoginActivity, responsBody.message, Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    }
                } else {
                    Toast.makeText(this@LoginActivity, response.message(), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginRespon>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message.toString()}")
                Toast.makeText(this@LoginActivity, "Gagal instance Retrofit", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setAnimation(){
        val edtemail = ObjectAnimator.ofFloat(binding.edtEmailLog, View.ALPHA, 1f).setDuration(700)
        val edtpass = ObjectAnimator.ofFloat(binding.edtPassLog, View.ALPHA, 1f).setDuration(700)
        val tvminpass = ObjectAnimator.ofFloat(binding.tvMinPass, View.ALPHA, 1f).setDuration(700)

        val button = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(700)

        AnimatorSet().apply {
            playSequentially(edtemail, edtpass, tvminpass, button)
            start()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        const val TAG = "LoginActivity"
    }
}