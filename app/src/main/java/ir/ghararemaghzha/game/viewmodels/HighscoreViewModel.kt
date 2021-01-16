package ir.ghararemaghzha.game.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.ghararemaghzha.game.data.ApiRepository
import ir.ghararemaghzha.game.data.Resource
import ir.ghararemaghzha.game.models.GeneralResponse
import ir.ghararemaghzha.game.models.HighscoreResponse
import kotlinx.coroutines.launch

class HighscoreViewModel(
        private val apiRepository: ApiRepository
) : ViewModel() {

    private val _highscoreResponse: MutableLiveData<Resource<HighscoreResponse>> = MutableLiveData()
    val highscoreResponse: LiveData<Resource<HighscoreResponse>>
        get() = _highscoreResponse

    fun getHighscoreList(token: String,number: String) = viewModelScope.launch {
        _highscoreResponse.value = apiRepository.getHighscoreList(token,number)
    }

}