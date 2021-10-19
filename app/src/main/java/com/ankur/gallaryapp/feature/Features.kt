package com.ankur.gallaryapp.feature

import android.graphics.Bitmap
import com.ankur.gallaryapp.model.ImageBitmap

interface Features {
    fun save(image: Bitmap)
    fun getRecent(): ImageBitmap
    fun deleteRecent()
    fun clearAll()
    fun size(): Int
    fun isEmpty(): Boolean
}