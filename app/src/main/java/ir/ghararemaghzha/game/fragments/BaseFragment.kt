package ir.ghararemaghzha.game.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import ir.ghararemaghzha.game.data.ApiRepository
import ir.ghararemaghzha.game.data.BaseRepository
import ir.ghararemaghzha.game.data.NetworkApi
import ir.ghararemaghzha.game.data.RemoteDataSource
import ir.ghararemaghzha.game.viewmodels.ViewModelFactory

abstract class BaseFragment<VM : ViewModel, B : ViewBinding> : Fragment() {

    protected lateinit var b: B
    protected lateinit var viewModel: VM
    private val remoteDataSource = RemoteDataSource()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        b = getFragmentBinding(inflater, container)
        val factory = ViewModelFactory(getFragmentRepository())
        viewModel = ViewModelProvider(this, factory).get(getViewModel())
        return b.root
    }

    abstract fun getViewModel(): Class<VM>

    abstract fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): B

    private fun getFragmentRepository() = ApiRepository(remoteDataSource.getApi(NetworkApi::class.java))

}