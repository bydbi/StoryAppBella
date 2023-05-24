package com.bella.storyappbella.main.story

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.bella.storyappbella.api.ApiConfig
import com.bella.storyappbella.api.RegistRespon
import com.bella.storyappbella.api.data.LoginPreference
import com.bella.storyappbella.api.data.ViewModelFactory
import com.bella.storyappbella.databinding.ActivityUploadBinding
import com.bella.storyappbella.register.RegistActivity
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("Setting")

class UploadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadBinding
    private lateinit var uploadViewModel: UploadViewModel
    private var getFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Tambah Story"

        setViewModel()

        binding.btnKamera.setOnClickListener { startCameraX() }
        binding.btnGaleri.setOnClickListener { startGallery() }
        binding.btnUpload.setOnClickListener {
            postStories()
        }

        if (!allPermissionGranted()){
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
    }

    private fun setViewModel(){
        uploadViewModel = ViewModelProvider(
            this,
            ViewModelFactory(LoginPreference.getInstance(dataStore))
        )[UploadViewModel::class.java]
    }

    private fun postStories(){
        if (getFile != null){
            val file = reduceFileImage(getFile as File)

            val desc = binding.desc.text.toString().toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )

            uploadViewModel.getUserData().observe(this, {userData ->
                val tokken = userData.token
                val service = ApiConfig.getApiService().postStories("Bearer "+ tokken, imageMultipart, desc)
                service.enqueue(object : Callback<RegistRespon> {
                    override fun onResponse(
                        call: Call<RegistRespon>,
                        response: Response<RegistRespon>
                    ) {
                        if(response.isSuccessful){
                            val responsBody = response.body()
                            if (responsBody!= null && !responsBody.error){
                                Log.d(TAG, responsBody.message)
                                Toast.makeText(this@UploadActivity, responsBody.message, Toast.LENGTH_SHORT).show()
                                finish()
                            }
                        } else {
                            Toast.makeText(this@UploadActivity, response.message(), Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<RegistRespon>, t: Throwable) {
                        Log.e(RegistActivity.TAG, "onFailure: ${t.message.toString()}")
                        Toast.makeText(this@UploadActivity, "Gagal instance Retrofit", Toast.LENGTH_SHORT).show()
                    }
                })
            })

        } else {
            Toast.makeText(this@UploadActivity, "Silakan masukkan gambar dahulu", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startGallery(){
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "pilih gambar")
        launcherIntentGallery.launch(chooser)
    }

    private fun startCameraX() {
        val intent = Intent(this@UploadActivity, KameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                it.data?.getSerializableExtra("picture")
            } else {
                @Suppress("DEPRECATION")
                it.data?.getSerializableExtra("picture")
            } as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean
            getFile = myFile

            myFile?.let { file ->
                rotateFile(file, isBackCamera)
                binding.imgPreview.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@UploadActivity)
            getFile = myFile
            binding.imgPreview.setImageURI(selectedImg)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS){
            if (!allPermissionGranted()){
                Toast.makeText(this@UploadActivity, "Tidak Mendapatkan Permission", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun allPermissionGranted() = Companion.REQUIRED_PERMISSIONS.all{
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        const val TAG = "UploadActivity"
        const val CAMERA_X_RESULT = 200
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
        const val KEY = "key"
    }
}