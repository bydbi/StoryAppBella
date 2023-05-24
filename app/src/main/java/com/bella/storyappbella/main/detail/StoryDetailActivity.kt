package com.bella.storyappbella.main.detail

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.bella.storyappbella.api.ApiConfig
import com.bella.storyappbella.api.data.LoginPreference
import com.bella.storyappbella.api.data.ViewModelFactory
import com.bella.storyappbella.api.respon.Story
import com.bella.storyappbella.api.respon.StoryDetailRespon
import com.bella.storyappbella.databinding.ActivityStoryDetailBinding
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("Setting")

class StoryDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStoryDetailBinding
    private lateinit var detailStoryViewModel: StoryDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Detail Story"

        setViewModel()
        detailStoryViewModel.getUserData().observe(this, {userData ->
            val tokken = userData.token
            getDetailListUser(tokken)
        })
    }

    private fun setViewModel(){
        detailStoryViewModel = ViewModelProvider(
            this,
            ViewModelFactory(LoginPreference.getInstance(dataStore))
        )[StoryDetailViewModel::class.java]
    }

    private fun getDetailListUser(token: String){
        val getId = intent.getStringExtra(KEY_ID)
        val client = getId?.let {
            ApiConfig.getApiService().getDetailStory("Bearer " + token,
                it
            )
        }
        if (client != null) {
            client.enqueue(object : Callback<StoryDetailRespon> {
                override fun onResponse(
                    call: Call<StoryDetailRespon>,
                    response: Response<StoryDetailRespon>
                ) {
                    showLoading(false)
                    if(response.isSuccessful){
                        val responsBody = response.body()
                        if (responsBody!= null && !responsBody.error){
                            setUserData(responsBody.story)
                            Toast.makeText(this@StoryDetailActivity, responsBody.message, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@StoryDetailActivity, response.message(), Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<StoryDetailRespon>, t: Throwable) {
                    showLoading(false)
                    Log.e(TAG, "onFailure: ${t.message.toString()}")
                    Toast.makeText(this@StoryDetailActivity, "Gagal instance Retrofit", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun setUserData(listUser: Story){
        binding.apply {
            tvDetailUsername.text = listUser.name
            detailDesc.text = listUser.description
            Glide.with(this@StoryDetailActivity)
                .load(listUser.photoUrl)
                .into(detailPhoto)
        }

    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object{
        const val TAG = "ListStoriesActivity"
        const val KEY_ID = "id"
    }
}