package com.ankur.gallaryapp.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat.startActivity
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.ankur.gallaryapp.R
import com.ankur.gallaryapp.constance.Constants
import com.ankur.gallaryapp.constance.Utill
import com.ankur.gallaryapp.ui.EditingActivity
import java.io.File

class GalleryAdapter(
    private val context: Context,
    private val urlList: ArrayList<String>

) : RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder>() {

    inner class GalleryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: AppCompatImageView = view.findViewById(R.id.image)

        fun bind(url: Uri) {
            image.setImageURI(url)
            image.setOnClickListener {
                val intent = Intent(context, EditingActivity::class.java)
                val parseUri = Uri.fromFile(File(url.toString()))
                intent.putExtra(Constants.INTENT_CODE, parseUri)
                context.startActivity(intent)
                (context as AppCompatActivity).finish()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        return GalleryViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.image_layout, parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        holder.bind(urlList[position].toUri())
    }

    override fun getItemCount(): Int {
        return urlList.size
    }
}