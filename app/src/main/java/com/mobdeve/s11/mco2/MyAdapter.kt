package com.mobdeve.s11.mco2

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
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

        when (currentItem.category) {
            "Food" -> holder.itemLayout.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.bookmark_food)
            "Miscellaneous" -> holder.itemLayout.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.bookmark_misc)
            "Entertainment" -> holder.itemLayout.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.bookmark_ent)
            "Transportation" -> holder.itemLayout.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.bookmark_transpo)
            "Utilities" -> holder.itemLayout.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.bookmark_utilities)
            "Personal Care" -> holder.itemLayout.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.bookmark_pcare)
        }

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, TransactionDetailActivity::class.java).apply {
                putExtra("amount", currentItem.amount)
                putExtra("category", currentItem.category)
                putExtra("name", currentItem.name)
                putExtra("date", currentItem.date)
                putExtra("photoUrl", currentItem.imageUrl)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return transactionList.size
    }

    class MyViewHolder (itemView: View) : RecyclerView.ViewHolder (itemView) {
        val itemLayout: LinearLayout = itemView.findViewById(R.id.transactionItemLayout)
        val name : TextView = itemView.findViewById(R.id.transactionName)
        val amount : TextView = itemView.findViewById(R.id.transactionAmount)
        val category : TextView = itemView.findViewById(R.id.transactionCategory)
        val date : TextView = itemView.findViewById(R.id.transactionDate)
    }

}