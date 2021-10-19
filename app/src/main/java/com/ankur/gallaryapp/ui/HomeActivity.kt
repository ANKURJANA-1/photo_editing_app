package com.ankur.gallaryapp.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.ankur.gallaryapp.R
import com.ankur.gallaryapp.databinding.ActivityHomeBinding
import com.ankur.gallaryapp.constance.Constants
import com.ankur.gallaryapp.constance.Utill
import java.lang.Exception

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(
            layoutInflater
        )
        setContentView(binding.root)

        try {
            val image = Utill.imageBitmap
            if (image != null)
                binding.image.setImageBitmap(Utill.imageBitmap)
        } catch (e: Exception) {
            Log.d(Constants.TAG, e.message.toString())
        }




        binding.camera.setOnClickListener {
            startActivity(Intent(this, CameraActivity::class.java))
        }
        binding.gallary.setOnClickListener {
            startActivity(Intent(this, GalleryActivity::class.java))
        }
    }
}