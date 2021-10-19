package com.ankur.gallaryapp.constance

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore.Images
import android.widget.Toast
import java.io.*


object Utill {

    var imageBitmap: Bitmap? = null

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }


    @Throws(FileNotFoundException::class, IOException::class)
    fun uriToBitmap(context: Context, url: Uri): Bitmap {
        val input: InputStream = context.contentResolver.openInputStream(url)!!
        val bitmap = BitmapFactory.decodeStream(input)
        input.close()
        return bitmap
    }

    fun getImageUri(context: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = Images.Media.insertImage(context.contentResolver, inImage, "Title", null)
        return Uri.fromFile(File(path.toString()))
    }

}