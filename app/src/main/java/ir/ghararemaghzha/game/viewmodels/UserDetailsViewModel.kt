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
import ir.ghararemaghzha.game.models.UserDetailsResponse
import kotlinx.coroutines.launch

class UserDetailsViewModel(
        private val apiRepository: ApiRepository
) : ViewModel() {

    private val _userDetailsResponse: MutableLiveData<Resource<UserDetailsResponse>> = MutableLiveData()
    val userDetailsResponse: LiveData<Resource<UserDetailsResponse>>
        get() = _userDetailsResponse

    fun getUserDetails(token: String,number: String,userId:String) = viewModelScope.launch {
        _userDetailsResponse.value = apiRepository.getUserDetails(token,number,userId)
    }

}