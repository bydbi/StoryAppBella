package com.bella.storyappbella.home

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import com.bella.storyappbella.R
import com.bella.storyappbella.databinding.ActivityWelcomeBinding
import com.bella.storyappbella.login.LoginActivity
import com.bella.storyappbella.register.RegistActivity

class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setView()
        setAction()
        setAnimation()
    }

    private fun setAction(){
        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this@WelcomeActivity, LoginActivity::class.java))
        }

        binding.btnRegist.setOnClickListener {
            startActivity(Intent(this@WelcomeActivity, RegistActivity::class.java))
        }
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

    private fun setAnimation(){
        val login = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(700)
        val regis = ObjectAnimator.ofFloat(binding.btnRegist, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(login, regis)
            start()
        }
    }
}