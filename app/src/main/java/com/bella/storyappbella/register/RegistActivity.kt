package com.bella.storyappbella.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
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
import com.bella.storyappbella.R
import com.bella.storyappbella.api.ApiConfig
import com.bella.storyappbella.api.RegistRespon
import com.bella.storyappbella.api.data.LoginPreference
import com.bella.storyappbella.api.data.UserModel
import com.bella.storyappbella.api.data.ViewModelFactory
import com.bella.storyappbella.databinding.ActivityRegistBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("Setting")

class RegistActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistBinding
    private lateinit var registrationViewModel: RegistViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_regist)
        binding = ActivityRegistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setViewModel()
        setView()
        regisAnimation()

        binding.edtPassReg.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                validationButton()
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                validationButton()
            }

            override fun afterTextChanged(s: Editable) {

            }

        })
        binding.btnRegist.setOnClickListener {
            val name = binding.edtNamaReg.text.toString()
            val email = binding.edtEmailReg.text.toString()
            val password = binding.edtPassReg.text.toString()

            registrationViewModel.saveUser(UserModel(name, email, password, false))
            postRegister(name, email, password)
        }

    }

    private fun validationButton(){
        val result = binding.edtPassReg.text
        binding.btnRegist.isEnabled = result != null && result.toString().isNotEmpty() && result.length >= 8
    }

    private fun postRegister(name: String, email: String, password: String) {
        val client = ApiConfig.getApiService().register(name, email, password)
        client.enqueue(object : Callback<RegistRespon> {
            override fun onResponse(
                call: Call<RegistRespon>,
                response: Response<RegistRespon>
            ) {
                if(response.isSuccessful){
                    val responsBody = response.body()
                    if (responsBody!= null && !responsBody.error){
                        Toast.makeText(this@RegistActivity, responsBody.message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@RegistActivity, response.message(), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RegistRespon>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message.toString()}")
                Toast.makeText(this@RegistActivity, "Gagal instance Retrofit", Toast.LENGTH_SHORT).show()
            }
        })
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
        registrationViewModel = ViewModelProvider(
            this,
            ViewModelFactory(LoginPreference.getInstance(dataStore))
        )[RegistViewModel::class.java]
    }

    private fun regisAnimation(){
        val edtnama = ObjectAnimator.ofFloat(binding.edtNamaReg, View.ALPHA, 1f).setDuration(700)
        val edtemail = ObjectAnimator.ofFloat(binding.edtEmailReg, View.ALPHA, 1f).setDuration(700)
        val edtpass = ObjectAnimator.ofFloat(binding.edtPassReg, View.ALPHA, 1f).setDuration(700)
        val tvminpass = ObjectAnimator.ofFloat(binding.tvMinPass, View.ALPHA, 1f).setDuration(700)

        val button = ObjectAnimator.ofFloat(binding.btnRegist, View.ALPHA, 1f).setDuration(700)

        AnimatorSet().apply {
            playSequentially(edtnama, edtemail, edtpass, tvminpass, button)
            start()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        const val TAG = "RegistrationActivity"
    }
}