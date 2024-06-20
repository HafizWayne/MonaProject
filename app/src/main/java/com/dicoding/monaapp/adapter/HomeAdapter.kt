package com.dicoding.monaapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.monaapp.R
import com.dicoding.monaapp.data.response.TransactionResponse
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class HomeAdapter(private val transactionList: List<TransactionResponse>) :
    RecyclerView.Adapter<HomeAdapter.TransactionViewHolder>() {

    inner class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categories2: TextView = itemView.findViewById(R.id.r7cqca9z3p4a)
        val dateMonth2: TextView = itemView.findViewById(R.id.rnbiyd3oxc8t)
        val minusPrice2: TextView = itemView.findViewById(R.id.r6riou9opo9)
        val categories: TextView = itemView.findViewById(R.id.category)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_home, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactionList[position]
        holder.categories2.text = transaction.title
        holder.categories.text = transaction.category
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        val date: Date = inputFormat.parse(transaction.date)
        val formattedDate: String = outputFormat.format(date)
        holder.dateMonth2.text = formattedDate

        val localeID = Locale("in", "ID")
        val formattedAmount = NumberFormat.getNumberInstance(localeID).format(transaction.amount)

        if (transaction.action == "expense") {
            holder.minusPrice2.text = "-Rp $formattedAmount"
            holder.minusPrice2.setTextColor(holder.itemView.context.getColor(R.color.red))
        } else {
            holder.minusPrice2.text = "Rp $formattedAmount"
            holder.minusPrice2.setTextColor(holder.itemView.context.getColor(R.color.teal))
        }
    }

    override fun getItemCount(): Int = transactionList.size
}
