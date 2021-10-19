package com.ankur.gallaryapp.ui

import android.Manifest
import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ankur.gallaryapp.adapter.GalleryAdapter
import com.ankur.gallaryapp.constance.Utill
import com.ankur.gallaryapp.databinding.ActivityGallaryBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.File


class GalleryActivity : AppCompatActivity() {

    lateinit var binding: ActivityGallaryBinding
    lateinit var galleryImageUrls: ArrayList<String>
    lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGallaryBinding.inflate(
            layoutInflater
        )
        setContentView(binding.root)
        takePermission()
        fetchGalleryImages()
        recyclerView = binding.imageList
        setDataToList(galleryImageUrls)
    }

    override fun onStart() {
        super.onStart()
    }

    private fun fetchGalleryImages(): ArrayList<String> {
        val ExternalStorageDirectoryPath = Environment
            .getExternalStorageDirectory()
            .absolutePath
        val targetPath =
            "$ExternalStorageDirectoryPath/Android/media/com.ankur.gallaryapp/Gallary App"
        galleryImageUrls = ArrayList<String>()
        val targetDirector = File(targetPath)

        val files = targetDirector.listFiles()
        for (file in files) {
            galleryImageUrls.add(file.absolutePath)
        }
        return galleryImageUrls
    }

    private fun setDataToList(galleryImageUrls: ArrayList<String>) {
        recyclerView.apply {
            adapter = GalleryAdapter(this@GalleryActivity, galleryImageUrls)
            layoutManager = GridLayoutManager(this@GalleryActivity, 3)
            hasFixedSize()
            smoothScrollToPosition(50)
        }
    }

    private fun takePermission() {
        Dexter.withContext(this@GalleryActivity)
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
                            Utill.showToast(this@GalleryActivity, "done")
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
                Utill.showToast(this@GalleryActivity, it.name)
            }
            .check()
    }
}