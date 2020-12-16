package ir.ghararemaghzha.game.adapters

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import io.realm.RealmResults
import ir.ghararemaghzha.game.R
import ir.ghararemaghzha.game.classes.DateConverter
import ir.ghararemaghzha.game.dialogs.IncomingDialog
import ir.ghararemaghzha.game.models.MessageModel

class IncomingAdapter(private val context: Context,
                      private val navController: NavController,
                      private val data: RealmResults<MessageModel>) : RecyclerView.Adapter<IncomingAdapter.ViewHolder>() {


    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val title: MaterialTextView = v.findViewById(R.id.incoming_title)
        val body: MaterialTextView = v.findViewById(R.id.incoming_body)
        val date: MaterialTextView = v.findViewById(R.id.incoming_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_incoming, parent, false))

    override fun onBindViewHolder(h: ViewHolder, position: Int) {
        val model = data[position]
        if (model != null) {
            h.title.text = model.title
            h.body.text = model.message
            val date = model.date
            val dateConverter = DateConverter()
            dateConverter.gregorianToPersian(date.substring(0, 4).toInt(), date.substring(5, 7).toInt(), date.substring(8, 10).toInt())
            h.date.text = "${dateConverter.month}/${dateConverter.day}"
            h.itemView.setOnClickListener {
                val dialog = IncomingDialog(context, navController, model)
                dialog.setCanceledOnTouchOutside(true)
                dialog.setCancelable(true)
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
                dialog.window?.setGravity(Gravity.CENTER)
                dialog.show()
                val window = dialog.window
                window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
            }
        }
    }

    override fun getItemCount(): Int = data.size

}