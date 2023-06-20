package com.example.kooglemaps

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.kooglemaps.databinding.RowBinding
import com.example.kooglemaps.databinding.RowcommentBinding

class SpotDataAdapter(val items:ArrayList<spotData>)
    : RecyclerView.Adapter<SpotDataAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: RowcommentBinding):RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = RowcommentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size   // 데이터 개수 반환
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // textView의 text 정보 받음
        holder.binding.textView.text = items[position].review.toString()
    }
}