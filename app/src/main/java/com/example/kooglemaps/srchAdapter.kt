package com.example.kooglemaps

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kooglemaps.databinding.RowBinding

class srchAdapter(val items: ArrayList<spotData>):RecyclerView.Adapter<srchAdapter.ViewHolder>(){

    interface OnItemClickListener{
        fun OnItemClick(position: Int, spot:spotData)
    }
    var itemClickListener:OnItemClickListener?=null

    inner class ViewHolder(val binding: RowBinding):RecyclerView.ViewHolder(binding.root){
        init{
            binding.title.setOnClickListener {
                itemClickListener?.OnItemClick(adapterPosition, items[adapterPosition])
            }
            binding.desc.setOnClickListener {
                itemClickListener?.OnItemClick(adapterPosition, items[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = RowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.title.text = items[position].title
        holder.binding.desc.text = items[position].desc
    }
}