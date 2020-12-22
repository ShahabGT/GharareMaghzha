package ir.ghararemaghzha.game.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.textview.MaterialTextView
import ir.ghararemaghzha.game.R
import ir.ghararemaghzha.game.adapters.ChatAdapter.MeViewHolder
import ir.ghararemaghzha.game.models.ContactsModel

class ContactsAdapter(private val ctx: Context, private val data: List<ContactsModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPE_HEADER = 0
    private val TYPE_OTHER = 1

    class OtherViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val name: MaterialTextView = v.findViewById(R.id.highscore_row_name)
        val score: MaterialTextView = v.findViewById(R.id.highscore_row_score)
        val rank: MaterialTextView = v.findViewById(R.id.highscore_row_rank)
        val avatar: ImageView = v.findViewById(R.id.highscore_row_avatar)
    }

    class HeaderViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val title: MaterialTextView = v.findViewById(R.id.row_contacts_header_title)
    }

    override fun getItemViewType(position: Int): Int =
            if (data[position].type == 0)
                TYPE_HEADER
            else
                TYPE_OTHER


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
         if (viewType == TYPE_HEADER)
             HeaderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_contact_header, parent, false))
        else
             OtherViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_highscore, parent, false))




    override fun getItemCount(): Int = data.size
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(getItemViewType(position)==TYPE_OTHER) {
            val h = holder as OtherViewHolder
            val model = data[position]
            h.name.text = model.name
            Glide.with(ctx)
                    .load(ctx.getString(R.string.avatar_url, model.avatar))
                    .circleCrop()
                    .placeholder(R.drawable.placeholder)
                    .into(h.avatar)

        }else{
            val h = holder as HeaderViewHolder
            val model = data[position]
            h.title.text=model.id
        }
    }
}