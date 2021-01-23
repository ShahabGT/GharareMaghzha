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

class BuyHistoryViewModel(
        private val apiRepository: ApiRepository
) : ViewModel() {

    private val _buyHistoryResponse: MutableLiveData<Resource<BuyHistoryResponse>> = MutableLiveData()
    val buyHistoryResponse: LiveData<Resource<BuyHistoryResponse>>
        get() = _buyHistoryResponse

    fun getBuyHistory(token: String,number: String) = viewModelScope.launch {
        _buyHistoryResponse.value = apiRepository.getBuyHistory(token,number)
    }

}