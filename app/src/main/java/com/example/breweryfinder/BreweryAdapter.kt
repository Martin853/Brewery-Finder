package com.example.breweryfinder

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.breweryfinder.databinding.ActivityMainBinding
import com.example.breweryfinder.databinding.ItemBreweryBinding

class BreweryAdapter : RecyclerView.Adapter<BreweryAdapter.BreweryViewHolder>() {

    inner class BreweryViewHolder (val binding: ItemBreweryBinding) : RecyclerView.ViewHolder(binding.root)

    private val diffCallback = object : DiffUtil.ItemCallback<Brewery>() {
        override fun areItemsTheSame(oldItem: Brewery, newItem: Brewery): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Brewery, newItem: Brewery): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)
    var breweries : MutableList<Brewery>
        get() = differ.currentList
        set(value) { differ.submitList(value) }

    override fun getItemCount() = breweries.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BreweryViewHolder {
        return BreweryViewHolder(ItemBreweryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }


    override fun onBindViewHolder(holder: BreweryViewHolder, position: Int) {
        holder.binding.apply {
            val brewery = breweries[position]
            tvTitle.text = brewery.name
            tvAddressStreet.text = "${brewery.address_1}, ${brewery.street}"
            tvCityState.text = "${brewery.city}, ${brewery.state}"
        }
    }
}