package ir.ghararemaghzha.game.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.ghararemaghzha.game.data.ApiRepository
import ir.ghararemaghzha.game.data.Resource
import ir.ghararemaghzha.game.models.BuyHistoryResponse
import ir.ghararemaghzha.game.models.GeneralResponse
import ir.ghararemaghzha.game.models.HighscoreResponse
import kotlinx.coroutines.launch

class InviteViewModel(
        private val apiRepository: ApiRepository
) : ViewModel() {

    private val _invitesResponse: MutableLiveData<Resource<GeneralResponse>> = MutableLiveData()
    val invitesResponse: LiveData<Resource<GeneralResponse>>
        get() = _invitesResponse

    fun getInvites(token: String,number: String) = viewModelScope.launch {
        _invitesResponse.value = apiRepository.getInvites(token,number)
    }

}