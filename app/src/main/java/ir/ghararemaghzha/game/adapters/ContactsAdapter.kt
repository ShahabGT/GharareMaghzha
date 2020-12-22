package ir.ghararemaghzha.game.adapters

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*
import android.widget.ImageView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.textview.MaterialTextView
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter
import ir.ghararemaghzha.game.R
import ir.ghararemaghzha.game.classes.MySharedPreference
import ir.ghararemaghzha.game.classes.Utils
import ir.ghararemaghzha.game.dialogs.UserDetailsDialog
import ir.ghararemaghzha.game.models.ContactsModel

class ContactsAdapter(private val ctx: FragmentActivity, data: OrderedRealmCollection<ContactsModel>) : RealmRecyclerViewAdapter<ContactsModel, RecyclerView.ViewHolder>(data, true) {

    private val typeHeader = 0
    private val typeOther = 1

    override fun getItemId(position: Int): Long = data?.get(position)?.contactId?.toLong()
            ?: 0.toLong()


    class OtherViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val name: MaterialTextView = v.findViewById(R.id.contact_row_name)
        val number: MaterialTextView = v.findViewById(R.id.contact_row_number)
        val avatar: ImageView = v.findViewById(R.id.contact_row_avatar)
        val invite: MaterialTextView = v.findViewById(R.id.contact_row_invite)
    }

    class HeaderViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val title: MaterialTextView = v.findViewById(R.id.row_contacts_header_title)
    }

    override fun getItemViewType(position: Int): Int =
            if (data?.get(position)?.type == 0)
                typeHeader
            else
                typeOther


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            if (viewType == typeHeader)
                HeaderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_contact_header, parent, false))
            else
                OtherViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_contact, parent, false))


    override fun getItemCount(): Int = data?.size ?: 0
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == typeOther) {
            val h = holder as OtherViewHolder
            val model = data?.get(position)
            if (model != null) {
                if (model.id == "0")
                    h.invite.visibility=View.VISIBLE
                else
                    h.invite.visibility=View.GONE
                h.name.text = model.name
                h.number.text = model.number
                Glide.with(ctx)
                        .load(ctx.getString(R.string.avatar_url, model.avatar))
                        .circleCrop()
                        .placeholder(R.drawable.placeholder)
                        .into(h.avatar)
                h.itemView.setOnClickListener {
                    if (model.id == "0")
                        Utils.shareCode(ctx,
                                ctx.getString(R.string.invite_share, MySharedPreference.getInstance(ctx).getUserCode()), model.number)
                    else
                        showDetailsDialog(model.id)
                }

            }

        } else {
            val h = holder as HeaderViewHolder
            val model = data?.get(position)
            if (model != null) {
                h.title.text = model.id
            }
        }
    }

    private fun showDetailsDialog(userId: String) {
        val dialog = UserDetailsDialog(ctx, userId)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(true)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.CENTER)
        dialog.show()
        val window = dialog.window
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
    }
}