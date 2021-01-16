package ir.ghararemaghzha.game.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.ghararemaghzha.game.data.ApiRepository
import ir.ghararemaghzha.game.data.Resource
import ir.ghararemaghzha.game.models.GeneralResponse
import kotlinx.coroutines.launch

class LoginViewModel(
        private val apiRepository: ApiRepository
) : ViewModel() {

    private val _loginResponse: MutableLiveData<Resource<GeneralResponse>> = MutableLiveData()
    val loginResponse: LiveData<Resource<GeneralResponse>>
        get() = _loginResponse

    fun login(number: String) = viewModelScope.launch {
        _loginResponse.value = apiRepository.login(number)
    }

}