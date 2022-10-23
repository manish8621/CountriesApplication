package com.mk.countries.view.adapter

import android.util.Log
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.mk.countries.R

@BindingAdapter("imageUrl")
fun bindImage(imageView: ImageView,srcUrl:String?){
    srcUrl?.let {
        val imgUri = it.toUri().buildUpon().scheme("https").build()
        Glide.with(imageView.context)
            .load(imgUri)
            .apply(RequestOptions()
                .placeholder(R.drawable.loading))
            .error(R.drawable.ic_baseline_broken_image_24)
            .into(imageView)
    }
}