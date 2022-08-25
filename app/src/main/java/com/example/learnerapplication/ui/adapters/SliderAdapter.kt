package com.example.learnerapplication.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.learnerapplication.R
import com.example.learnerapplication.data.model.SliderData
import com.example.learnerapplication.databinding.SliderLayoutBinding
import com.smarteist.autoimageslider.SliderViewAdapter


class SliderAdapter(context: Context?, sliderDataArrayList: ArrayList<SliderData>) :
    SliderViewAdapter<SliderAdapter.SliderAdapterViewHolder>() {
    private val mSliderItems: List<SliderData>

    init {
        mSliderItems = sliderDataArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup): SliderAdapterViewHolder {
        val binding = SliderLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SliderAdapterViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: SliderAdapterViewHolder, position: Int) {
        val (imgUrl) = mSliderItems[position]
        viewHolder.bind()
        Glide.with(viewHolder.itemView)
            .load(imgUrl)
            .placeholder(R.drawable.ic_baseline_image_24)
            .fitCenter()
            .into(viewHolder.imageViewBackground)
    }

    override fun getCount(): Int {
        return mSliderItems.size
    }

    inner class SliderAdapterViewHolder(private val binding: SliderLayoutBinding) : ViewHolder(binding.root) {
        lateinit var item: View
        lateinit var imageViewBackground: ImageView

        fun bind(){
            binding.apply{
                imageViewBackground = image
                item = binding.root
            }
        }
    }
}