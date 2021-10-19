package com.ankur.gallaryapp.ui

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import com.ankur.gallaryapp.constance.Constants
import com.ankur.gallaryapp.constance.Utill
import com.ankur.gallaryapp.databinding.ActivityEditingBinding
import com.ankur.gallaryapp.feature.Undo
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

class EditingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditingBinding
    private lateinit var undo: Undo
    private lateinit var uri: Uri
    private lateinit var bitmap: Bitmap
    private lateinit var imageBitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditingBinding.inflate(
            layoutInflater
        )
        setContentView(binding.root)

        undo = Undo(this)

        val bundle: Bundle? = intent.extras

        if (bundle != null) {
            try {
                uri = bundle.get(Constants.INTENT_CODE) as Uri
                bitmap = Utill.uriToBitmap(this, uri)
                imageBitmap = bitmap
                Utill.imageBitmap = imageBitmap
                undo.save(imageBitmap)
            } catch (e: Exception) {
                Log.d(Constants.TAG, e.message.toString())
            }

        }
        binding.showImage.setImageURI(uri)
        saveImage(uri)
        back()
        crop(uri)
        rotate(uri)
        performUndo()
    }

    override fun onStart() {
        super.onStart()
    }


    private fun saveImage(savedUri: Uri) {
        binding.save.setOnClickListener {
            val intent = Intent(this@EditingActivity, HomeActivity::class.java)
            Utill.imageBitmap = this.imageBitmap
            saveMediaToStorage(imageBitmap)
            startActivity(intent)
            finish()
        }
    }

    private fun crop(uri: Uri) {
        binding.crop.setOnClickListener {
            pickFromImageView(uri)
            Utill.imageBitmap = this.imageBitmap
        }
    }

    private fun pickFromImageView(uri: Uri) {
        CropImage.activity(uri).start(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result: CropImage.ActivityResult = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                val resultUri: Uri = result.uri
                imageBitmap = Utill.uriToBitmap(this, resultUri)!!
                undo.save(imageBitmap)
                undoMechanism()
                Picasso.with(this).load(resultUri).into(binding.showImage)
            }
        }
    }

    private fun back() {
        binding.back.setOnClickListener {
            undo.clearAll()
        }
    }

    private fun rotate(uri: Uri) {
        var angle: Float = 0F
        binding.rotate.setOnClickListener {
            val matrix = Matrix()
            angle += 90F
            matrix.postRotate(angle)
            try {
                val bitmap: Bitmap = Utill.uriToBitmap(this@EditingActivity, uri)!!
                val rotatedBitmap = Bitmap.createBitmap(
                    bitmap, 0, 0, bitmap.width, bitmap.height,
                    matrix, true
                )
                imageBitmap = rotatedBitmap
                binding.showImage.setImageBitmap(imageBitmap)
                undo.save(imageBitmap)
                Utill.imageBitmap = this.imageBitmap
                undoMechanism()
            } catch (e: Exception) {
                Log.d(Constants.TAG, e.message.toString())
            }

        }
    }

    private fun undoMechanism() {
        if (undo.size() >= 1) {
            binding.undo.visibility = View.VISIBLE
        } else {
            binding.undo.visibility = View.GONE
        }
    }

    private fun performUndo() {
        binding.undo.setOnClickListener {
            try {
                if (undo.size() >= 1) {
                    Utill.imageBitmap = this.imageBitmap
                    undo.deleteRecent()
                    val bit = undo.getRecent().bitmap
                    binding.showImage.setImageBitmap(bit)
                }
            } catch (e: java.lang.Exception) {
                Log.d(Constants.TAG, e.message.toString())
            }

        }
    }


    private fun saveMediaToStorage(bitmap: Bitmap) {
        val filename = "${System.currentTimeMillis()}.jpg"
        var fos: OutputStream? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            this.contentResolver?.also { resolver ->
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                val imageUri: Uri? =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            val imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }

        fos?.use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            Utill.showToast(this, "save")
        }
    }


}