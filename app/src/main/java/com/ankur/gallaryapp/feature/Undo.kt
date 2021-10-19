package com.ankur.gallaryapp.feature

import android.content.Context
import android.graphics.Bitmap
import android.provider.SyncStateContract
import android.util.Log
import com.ankur.gallaryapp.constance.Constants
import com.ankur.gallaryapp.constance.Utill
import com.ankur.gallaryapp.model.ImageBitmap
import java.lang.Exception
import java.lang.RuntimeException
import java.util.*

class Undo(
    context: Context
) : Features {
    private var undoStack: Stack<ImageBitmap> = Stack()


    override fun save(image: Bitmap) {
        undoStack.add(ImageBitmap(image))
    }

    override fun getRecent(): ImageBitmap {
        try {
            return if (size() >=1) {
                undoStack.peek()
            } else {
                ImageBitmap(Utill.imageBitmap!!)
            }

        } catch (e: Exception) {
            throw RuntimeException(e.message.toString())
        }
    }

    override fun deleteRecent() {
        try {
            if (size() > 1) {
                undoStack.pop()
            }
        } catch (e: Exception) {
            Log.d(Constants.TAG, e.message.toString())
        }

    }

    override fun clearAll() {
        try {
            undoStack.clear()
        } catch (e: Exception) {
            Log.d(Constants.TAG, e.message.toString())
        }
    }

    override fun size(): Int {
        try {
            return undoStack.size
        } catch (e: Exception) {
            throw RuntimeException(e.message.toString())
        }

    }

    override fun isEmpty(): Boolean {
        return undoStack.isEmpty()
    }
}