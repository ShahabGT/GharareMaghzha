package ir.ghararemaghzha.game.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.ghararemaghzha.game.data.ApiRepository
import ir.ghararemaghzha.game.data.Resource
import ir.ghararemaghzha.game.models.GeneralResponse
import kotlinx.coroutines.launch

class RegisterViewModel(
        private val apiRepository: ApiRepository
) : ViewModel() {

    private val _registerResponse: MutableLiveData<Resource<GeneralResponse>> = MutableLiveData()
    val registerResponse: LiveData<Resource<GeneralResponse>>
        get() = _registerResponse

    fun register(name:String,number: String) = viewModelScope.launch {
        _registerResponse.value = apiRepository.registerUser(name,number)
    }

}