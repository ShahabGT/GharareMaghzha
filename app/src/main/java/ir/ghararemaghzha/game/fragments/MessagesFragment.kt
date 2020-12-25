package ir.ghararemaghzha.game.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import io.realm.kotlin.where
import ir.ghararemaghzha.game.R
import ir.ghararemaghzha.game.adapters.IncomingAdapter
import ir.ghararemaghzha.game.classes.Const.GHARAREHMAGHZHA_BROADCAST
import ir.ghararemaghzha.game.models.MessageModel

class MessagesFragment : Fragment(R.layout.fragment_messages) {
    private lateinit var ctx: Context
    private lateinit var act: FragmentActivity


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ctx = requireContext()
        act = requireActivity()
        init(view)
    }

    private fun init(v: View) {
        (act.findViewById<View>(R.id.toolbar_title) as MaterialTextView).setText(R.string.message_incoming)
        val db = Realm.getDefaultInstance()
        db.executeTransaction {
            val results: RealmResults<MessageModel> = it.where<MessageModel>().equalTo("sender", "admin").equalTo("read", "0".toInt()).findAll()
            results.setInt("read", 1)
        }
        val data = db.where<MessageModel>().equalTo("sender", "admin").sort("date", Sort.DESCENDING).findAll()
        val intent = Intent()
        intent.action = GHARAREHMAGHZHA_BROADCAST
        ctx.sendBroadcast(intent)
        val recyclerView: RecyclerView = v.findViewById(R.id.message_recycler)
        recyclerView.layoutManager = LinearLayoutManager(ctx)
        if (data.isEmpty())
            v.findViewById<View>(R.id.message_empty).visibility = View.VISIBLE
        else {
            val adapter = IncomingAdapter(ctx,view?.findNavController()!!, data)
            recyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
        }
    }
}