package ir.ghararemaghzha.game.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.ghararemaghzha.game.data.ApiRepository
import ir.ghararemaghzha.game.data.Resource
import ir.ghararemaghzha.game.models.GeneralResponse
import ir.ghararemaghzha.game.models.HighscoreResponse
import ir.ghararemaghzha.game.models.NitroResponse
import kotlinx.coroutines.launch

class NitroViewModel(
        private val apiRepository: ApiRepository
) : ViewModel() {

    private val _nitroResponse: MutableLiveData<Resource<NitroResponse>> = MutableLiveData()
    val nitroResponse: LiveData<Resource<NitroResponse>>
        get() = _nitroResponse

    fun getNitro(token: String,number: String) = viewModelScope.launch {
        _nitroResponse.value = apiRepository.getNitro(token,number)
    }


    private val _initBuyResponse: MutableLiveData<Resource<GeneralResponse>> = MutableLiveData()
    val initBuyResponse: LiveData<Resource<GeneralResponse>>
        get() = _initBuyResponse

    fun initBuy(token: String, number: String, plan: String, influencerId: String, influencerAmount: String, amount: String) = viewModelScope.launch {
        _initBuyResponse.value = apiRepository.initBuy(token,number, plan, influencerId, influencerAmount, amount)
    }
}