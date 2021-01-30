package ir.ghararemaghzha.game.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textview.MaterialTextView
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import io.realm.kotlin.where
import ir.ghararemaghzha.game.R
import ir.ghararemaghzha.game.adapters.IncomingAdapter
import ir.ghararemaghzha.game.classes.Const.GHARAREHMAGHZHA_BROADCAST
import ir.ghararemaghzha.game.databinding.FragmentMessagesBinding
import ir.ghararemaghzha.game.models.MessageModel

class MessagesFragment : Fragment(R.layout.fragment_messages) {
    private lateinit var b: FragmentMessagesBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        b = FragmentMessagesBinding.inflate(layoutInflater, container, false)
        return b.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()
    }

    private fun init() {
        requireActivity().findViewById<MaterialTextView>(R.id.toolbar_title).setText(R.string.message_incoming)
        val db = Realm.getDefaultInstance()
        db.executeTransaction {
            val results: RealmResults<MessageModel> = it.where<MessageModel>().equalTo("sender", "admin").equalTo("read", "0".toInt()).findAll()
            results.setInt("read", 1)
        }
        val data = db.where<MessageModel>().equalTo("sender", "admin").sort("date", Sort.DESCENDING).findAll()
        val intent = Intent()
        intent.action = GHARAREHMAGHZHA_BROADCAST
        requireContext().sendBroadcast(intent)
        b.messageRecycler.layoutManager = LinearLayoutManager(requireContext())
        if (data.isEmpty())
            b.messageEmpty.visibility = View.VISIBLE
        else {
            val adapter = IncomingAdapter(requireContext(), requireView().findNavController(), data)
            b.messageRecycler.adapter = adapter
            adapter.notifyDataSetChanged()
        }
    }
}