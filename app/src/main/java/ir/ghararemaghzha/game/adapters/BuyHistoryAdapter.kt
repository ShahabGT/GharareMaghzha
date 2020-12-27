package ir.ghararemaghzha.game.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import ir.ghararemaghzha.game.R
import ir.ghararemaghzha.game.classes.DateConverter
import ir.ghararemaghzha.game.classes.Utils
import ir.ghararemaghzha.game.models.BuyHistoryModel

class BuyHistoryAdapter(private val ctx: Context,
                        private val data: List<BuyHistoryModel>) : RecyclerView.Adapter<BuyHistoryAdapter.ViewHolder>() {

    class ViewHolder(v: View) :RecyclerView.ViewHolder(v){
        val title:MaterialTextView = v.findViewById(R.id.buyhistory_row_description)
        val price:MaterialTextView = v.findViewById(R.id.buyhistory_row_price)
        val date:MaterialTextView = v.findViewById(R.id.buyhistory_row_date)
        val influencer:MaterialTextView = v.findViewById(R.id.buyhistory_row_influencer)
        val stat:MaterialTextView = v.findViewById(R.id.buyhistory_row_stat)
        val resCode:MaterialTextView = v.findViewById(R.id.buyhistory_row_rescode)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder=ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_buyhistory, parent, false))

    override fun onBindViewHolder(h: ViewHolder, position: Int) {
        val model = data[position]
        when (model.plan) {
            "1" -> h.title.text = ctx.getString(R.string.buyhistory_row_title, "500")
            "2" -> h.title.text = ctx.getString(R.string.buyhistory_row_title, "1000")
            "3" -> h.title.text = ctx.getString(R.string.buyhistory_row_title, "1500")
            "4" -> h.title.text = ctx.getString(R.string.buyhistory_row_title, "2000")
            "5" -> h.title.text = ctx.getString(R.string.buyhistory_row_title, "2500")
            else -> h.title.text = ctx.getString(R.string.buyhistory_row_title2)
        }
        h.price.text = ctx.getString(R.string.buyhistory_row_price, Utils.moneySeparator(model.amount))
        if (model.influencerCode != null && model.influencerCode.isNotEmpty()) {
            h.influencer.visibility = View.VISIBLE
            h.influencer.text = ctx.getString(R.string.buyhistory_influencer_price, Utils.moneySeparator(model.amount))
        } else h.influencer.visibility = View.GONE
        if (model.valid == "1") {
            h.stat.text = ctx.getString(R.string.buyhistory_row_stat, "موفق")
            h.resCode.visibility = View.VISIBLE
            h.resCode.text = ctx.getString(R.string.buyhistory_row_rescode, model.resCode)
        } else {
            h.stat.text = ctx.getString(R.string.buyhistory_row_stat, "ناموفق")
            h.resCode.visibility = View.GONE
        }
        val date = model.date
        val dateConverter = DateConverter()
        dateConverter.gregorianToPersian(date.substring(0, 4).toInt(), date.substring(5, 7).toInt(), date.substring(8, 10).toInt())
        h.date.text = ctx.getString(R.string.buyhistory_row_date, date.substring(11, 16) + " " + dateConverter.year + "/" + dateConverter.month + "/" + dateConverter.day)

    }

    override fun getItemCount(): Int =data.size
}