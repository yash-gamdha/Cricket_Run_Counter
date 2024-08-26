package com.yash.cricketruncounter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyAdapter(val activity: Context, val data: MutableList<Score>) :
RecyclerView.Adapter<MyAdapter.MyVH>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyVH {
        val view = LayoutInflater.from(activity).inflate(R.layout.score,parent,false)
        return MyVH(view)
    }

    override fun onBindViewHolder(holder: MyVH, position: Int) {
        holder.over.text = data[position].over.toString()
        holder.score.text = data[position].score
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyVH(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val over : TextView = itemView.findViewById(R.id.overs)
        val score : TextView = itemView.findViewById(R.id.score)
    }
}