package com.ankur.gallaryapp.ui

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Images
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.ankur.gallaryapp.R
import com.ankur.gallaryapp.databinding.ActivityMainBinding
import com.ankur.gallaryapp.constance.Constants
import com.ankur.gallaryapp.constance.Utill
import com.ankur.gallaryapp.repository.GloabalStorage
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class CameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(
            layoutInflater
        )
        setContentView(binding.root)

        takePermission()
        outputDirectory = getOutputDirectory()

        binding.cameraCaptureButton.setOnClickListener {
            takePhoto()
        }
    }

    private fun startCamera() {
        val cameraProvider = ProcessCameraProvider
            .getInstance(this)

        cameraProvider.addListener({
            val cameraProcessProvider: ProcessCameraProvider = cameraProvider.get()
            val preview: Preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                cameraProcessProvider.unbindAll()
                cameraProcessProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (e: Exception) {
                Log.d(Constants.TAG, e.message.toString())
            }

        }, ContextCompat.getMainExecutor(this))

    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(
                Constants.FILE_NAME_FORMAT, Locale.US
            ).format(System.currentTimeMillis()) + ".jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()


        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(Constants.TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    val saveBitmap = Utill.uriToBitmap(this@CameraActivity, savedUri)
                    Log.d("bit", saveBitmap.toString())
                    val intent = Intent(this@CameraActivity, EditingActivity::class.java)
                    intent.putExtra(Constants.INTENT_CODE, savedUri)
                    startActivity(intent)
                    finish()

                }
            })
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }


    private fun takePermission() {
        Dexter.withContext(this@CameraActivity)
            .withPermissions(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    report?.let {
                        if (report.areAllPermissionsGranted()) {
                            startCamera()
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }
            })
            .withErrorListener {
                Utill.showToast(this@CameraActivity, it.name)
            }
            .check()
    }

    private fun addImageToGallery(filePath: String?, context: Context) {
        val values = ContentValues()
        values.put(Images.Media.DATE_TAKEN, System.currentTimeMillis())
        values.put(Images.Media.MIME_TYPE, "image/jpeg")
        values.put(MediaStore.MediaColumns.DATA, filePath)
        context.contentResolver.insert(Images.Media.EXTERNAL_CONTENT_URI, values)
    }


}

