package com.mobdeve.s11.mco2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyAdapter (private val transactionList : ArrayList<Transaction>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    override fun onCreateViewHolder (parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.transaction_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder (holder: MyViewHolder, position: Int) {
        val currentItem = transactionList[position]
        holder.name.text = currentItem.name
        holder.amount.text = currentItem.amount
        holder.category.text = currentItem.category
        holder.date.text = currentItem.date
    }

    override fun getItemCount(): Int {
        return transactionList.size
    }

    class MyViewHolder (itemView: View) : RecyclerView.ViewHolder (itemView) {
        val name : TextView = itemView.findViewById(R.id.transactionName)
        val amount : TextView = itemView.findViewById(R.id.transactionAmount)
        val category : TextView = itemView.findViewById(R.id.transactionCategory)
        val date : TextView = itemView.findViewById(R.id.transactionDate)
    }

}