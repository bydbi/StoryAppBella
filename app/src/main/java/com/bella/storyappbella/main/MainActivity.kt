package com.bella.storyappbella.main

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bella.storyappbella.R
import com.bella.storyappbella.api.ApiConfig
import com.bella.storyappbella.api.data.LoginPreference
import com.bella.storyappbella.api.data.ViewModelFactory
import com.bella.storyappbella.api.respon.ListStoryItem
import com.bella.storyappbella.api.respon.ListStoryRespon
import com.bella.storyappbella.databinding.ActivityMainBinding
import com.bella.storyappbella.home.WelcomeActivity
import com.bella.storyappbella.login.LoginActivity
import com.bella.storyappbella.main.maps.MapsActivity
import com.bella.storyappbella.main.story.UploadActivity
import com.bella.storyappbella.paging.QuotePagingViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("Setting")

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    private val mainPagingViewModel: QuotePagingViewModel by viewModels {
        val apiService = ApiConfig.getApiService()
        val pref = UserPreference.getInstance(dataStore)
        ViewModelPagingFactory(QuoteRepository(apiService, pref))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Dicoding Stories"

        setViewModel()

        val layoutManager = LinearLayoutManager(this)
        binding.rvStory.layoutManager = layoutManager

        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvStory.addItemDecoration(itemDecoration)

        getData()
    }

    override fun onResume() {
        super.onResume()
        getData()
    }

    private fun getData() {
        val adapter = MainAdapter()
        binding.rvStory.adapter = adapter
        adapter.addLoadStateListener {
            when (it.refresh) {
                is LoadState.Loading -> {
                    showLoading(true)
                }
                is LoadState.Error -> {
                    showLoading(false)
                }
                else -> {
                    showLoading(false)
                }
            }
        }
        binding.rvStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingAdapter {
                adapter.retry()
            }
        )
        mainPagingViewModel.story.observe(this) {
            adapter.submitData(lifecycle, it)
        }
    }

    private fun setViewModel(){
        val pref = LoginPreference.getInstance(dataStore)
        mainViewModel = ViewModelProvider(
            this,
            ViewModelFactory(pref)
        )[MainViewModel::class.java]

        mainViewModel.getLoginUser().observe(this) { user ->
            if (user.isLogin) {
                getData()
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            getData()
        }
    }

    private fun showLoading(isLoading: Boolean){
        if (isLoading){
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.item_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.maps) {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }
        if (id == R.id.upload) {
            val intent = Intent(this, UploadActivity::class.java)
            startActivity(intent)
        }
        if (id == R.id.logout) {
            mainViewModel.logout()
            mainViewModel.deleteToken()
        }
        return true
    }
}