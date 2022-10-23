package com.mk.countries.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mk.countries.databinding.CountryListItemBinding
import com.mk.countries.model.domain.DomainModels.*

class CountriesViewAdapter:ListAdapter<CountryItem,CountriesViewAdapter.ItemViewHolder>(DiffUtilCallBack()){

    //lambda
    private var clickListener:((countryItem:CountryItem)->Unit)? = null

    //viewHolder
    class ItemViewHolder private constructor(private val binding:CountryListItemBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(countryItem:CountryItem,clickListener:((countryItem:CountryItem)->Unit)?)
        {
            binding.countryItem = countryItem

            //if clicklistener is not null
            clickListener?.let {
                binding.root.setOnClickListener{
                        clickListener.invoke(countryItem)
                }
            }

        }
        companion object{
            fun from(parent:ViewGroup):ItemViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = CountryListItemBinding.inflate(layoutInflater,parent,false)
                return ItemViewHolder(binding)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder = ItemViewHolder.from(parent)

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) = holder.bind(getItem(position),clickListener)

    fun setOnclickListener(clickListener:((countryItem:CountryItem)->Unit)){
        this.clickListener = clickListener
    }
}
class DiffUtilCallBack:DiffUtil.ItemCallback<CountryItem>(){
    override fun areItemsTheSame(oldItem: CountryItem, newItem: CountryItem): Boolean = oldItem.name==newItem.name
    override fun areContentsTheSame(oldItem: CountryItem, newItem: CountryItem): Boolean = oldItem==newItem
}
