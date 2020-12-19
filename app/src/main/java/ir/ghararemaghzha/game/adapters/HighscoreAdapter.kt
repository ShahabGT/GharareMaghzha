package ir.ghararemaghzha.game.adapters

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.textview.MaterialTextView
import ir.ghararemaghzha.game.R
import ir.ghararemaghzha.game.dialogs.UserDetailsDialog
import ir.ghararemaghzha.game.models.HighscoreModel
import ir.ghararemaghzha.game.models.UserRankModel

class HighscoreAdapter(private val context: FragmentActivity,
                       private val data: List<HighscoreModel>,
                       private val user: UserRankModel,
                       private val showUser: Boolean) : RecyclerView.Adapter<HighscoreAdapter.ViewHolder>() {

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val name: MaterialTextView = v.findViewById(R.id.highscore_row_name)
        val score: MaterialTextView = v.findViewById(R.id.highscore_row_score)
        val rank: MaterialTextView = v.findViewById(R.id.highscore_row_rank)
        val avatar: ImageView = v.findViewById(R.id.highscore_row_avatar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_highscore, parent, false))

    override fun onBindViewHolder(h: ViewHolder, position: Int) {
        if (position < data.size) {
            val (_, scoreCount, userId, userName, _, _, userAvatar) = data[position]
            h.name.text = userName
            if (scoreCount == "-1") h.score.text = "0" else h.score.text = scoreCount
            h.rank.text = (position + 1).toString()
            h.itemView.setBackgroundColor(context.resources.getColor(R.color.white))
            Glide.with(context)
                    .load(context.getString(R.string.avatar_url, userAvatar))
                    .circleCrop()
                    .placeholder(R.drawable.placeholder)
                    .into(h.avatar)
            h.itemView.setOnClickListener { showDetailsDialog(userId) }
            if (!showUser && userId == user.userId) {
                h.itemView.setBackgroundColor(context.resources.getColor(R.color.alpha4))
                h.name.text = "شما"
            }
        } else if (showUser) {
            h.name.text = "شما"
            h.score.text = user.scoreCount
            h.rank.text = user.userRank
            h.itemView.setBackgroundColor(context.resources.getColor(R.color.alpha4))
            Glide.with(context)
                    .load(context.getString(R.string.avatar_url, user.userAvatar))
                    .circleCrop()
                    .placeholder(R.drawable.placeholder)
                    .into(h.avatar)
            h.itemView.setOnClickListener { showDetailsDialog(user.userId) }
        }
    }

    override fun getItemCount(): Int {
        return if (!showUser) data.size else data.size + 1
    }

    private fun showDetailsDialog(userId: String) {
        val dialog = UserDetailsDialog(context, userId)
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