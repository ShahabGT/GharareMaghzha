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
import ir.ghararemaghzha.game.models.SliderResponse
import kotlinx.coroutines.launch

class StartViewModel(
        private val apiRepository: ApiRepository
) : ViewModel() {

    private val _sliderResponse: MutableLiveData<Resource<SliderResponse>> = MutableLiveData()
    val sliderResponse: LiveData<Resource<SliderResponse>>
        get() = _sliderResponse

    fun getSlider() = viewModelScope.launch {
        _sliderResponse.value = apiRepository.getSlider()
    }

}