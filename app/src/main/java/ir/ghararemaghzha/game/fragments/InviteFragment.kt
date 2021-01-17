package ir.ghararemaghzha.game.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import ir.ghararemaghzha.game.R
import ir.ghararemaghzha.game.classes.MySharedPreference
import ir.ghararemaghzha.game.classes.Utils
import ir.ghararemaghzha.game.data.ApiRepository
import ir.ghararemaghzha.game.data.NetworkApi
import ir.ghararemaghzha.game.data.RemoteDataSource
import ir.ghararemaghzha.game.data.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InviteFragment : Fragment(R.layout.fragment_invite) {
    private lateinit var count: MaterialTextView
    private lateinit var value: MaterialTextView
    private lateinit var progressBar: RoundCornerProgressBar
    private lateinit var loading: ConstraintLayout
    private lateinit var navController: NavController
    private lateinit var act: FragmentActivity
    private lateinit var ctx: Context

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = super.onCreateView(inflater, container, savedInstanceState)
        act = requireActivity()
        ctx = requireContext()
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        init(view)
    }

    private fun init(v: View) {
        act.findViewById<MaterialTextView>(R.id.toolbar_title).setText(R.string.invite_title)
        loading = v.findViewById(R.id.invite_loading)
        count = v.findViewById(R.id.invite_count)
        value = v.findViewById(R.id.invite_info_text1)
        progressBar = v.findViewById(R.id.invite_progress)
        v.findViewById<MaterialTextView>(R.id.invite_code).text = MySharedPreference.getInstance(ctx).getUserCode()
        v.findViewById<MaterialButton>(R.id.invite_share).setOnClickListener {
            Utils.shareCode(ctx, getString(R.string.invite_share, MySharedPreference.getInstance(ctx).getUserCode()), "")
        }
        v.findViewById<MaterialButton>(R.id.invite_contacts).setOnClickListener {
            navController.navigate(R.id.action_inviteFragment_to_contactsFragment)
        }
        loading.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch {
            getData()
        }
    }

    private suspend fun getData() {
        val number = MySharedPreference.getInstance(ctx).getNumber()
        val token = MySharedPreference.getInstance(ctx).getAccessToken()
        if (number.isEmpty() || token.isEmpty()) {
            Utils.logout(act, true)
            return
        }

        when (val res = ApiRepository(RemoteDataSource().getApi(NetworkApi::class.java)).getInvites("Bearer $token", number)) {
            is Resource.Success -> {
                if (res.value.result == "success") {
                    withContext(Dispatchers.Main) {
                        val fCount = res.value.count.toFloat()
                        val remaining = 20 - fCount.toInt()
                        count.text = getString(R.string.invite_star_title, remaining)
                        progressBar.progress = fCount
                        value.text = getString(R.string.invite_info_text1, res.value.value)
                        loading.visibility = View.GONE
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Utils.showInternetError(ctx) {
                            CoroutineScope(Dispatchers.IO).launch {
                                getData()
                            }
                        }
                        Toast.makeText(ctx, getString(R.string.general_error), Toast.LENGTH_SHORT).show()
                    }
                }
            }
            is Resource.Failure -> {
                if (res.isNetworkError) {
                    withContext(Dispatchers.Main) {
                        Utils.showInternetError(ctx) {
                            CoroutineScope(Dispatchers.IO).launch {
                                getData()
                            }
                        }
                        Toast.makeText(ctx, getString(R.string.general_error), Toast.LENGTH_SHORT).show()
                    }
                } else if (res.errorCode == 401) {
                    withContext(Dispatchers.Main) {
                        loading.visibility = View.GONE
                        Utils.logout(act, true)
                    }
                }
            }
        }
    }
}