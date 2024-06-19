import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.monaapp.R
import com.dicoding.monaapp.data.response.TransactionResponse
import java.text.NumberFormat
import java.util.*

class TransactionAdapter(private val transactionList: List<TransactionResponse>) :
    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    inner class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categories2: TextView = itemView.findViewById(R.id.categories2)
        val dateMonth2: TextView = itemView.findViewById(R.id.date_month2)
        val minusPrice2: TextView = itemView.findViewById(R.id.minus_price2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactionList[position]
        holder.categories2.text = transaction.title
        holder.dateMonth2.text = transaction.date

        // Format the amount with thousand separators using Indonesian locale
        val localeID = Locale("in", "ID")
        val formattedAmount = NumberFormat.getNumberInstance(localeID).format(transaction.amount)
        holder.minusPrice2.text = "-Rp $formattedAmount"
    }

    override fun getItemCount(): Int = transactionList.size
}
