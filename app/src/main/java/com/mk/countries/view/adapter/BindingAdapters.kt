package com.mk.countries.view.adapter

import android.util.Log
import android.view.View
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
                .placeholder(R.drawable.loading_animation))
            .error(R.drawable.ic_baseline_broken_image_24)
            .into(imageView)
    }
}
@BindingAdapter("visiblityStatus")
fun setStatusVisibility(view: View, visible:Boolean){
    view.visibility =if (visible)  View.VISIBLE else  View.INVISIBLE
}