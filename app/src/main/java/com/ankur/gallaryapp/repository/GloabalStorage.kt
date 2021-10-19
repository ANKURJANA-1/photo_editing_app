package com.ankur.gallaryapp.repository

import android.net.Uri

object GloabalStorage {

    private val imageUrl: ArrayList<Uri> = ArrayList()

    fun addData(url: Uri) {
        imageUrl.add(url)
    }

    fun getAllData(): ArrayList<Uri> {
        return imageUrl
    }
}