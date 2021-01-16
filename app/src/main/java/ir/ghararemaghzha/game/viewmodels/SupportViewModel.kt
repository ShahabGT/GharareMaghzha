package ir.ghararemaghzha.game.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.ghararemaghzha.game.data.ApiRepository
import ir.ghararemaghzha.game.data.Resource
import ir.ghararemaghzha.game.models.ChatResponse
import ir.ghararemaghzha.game.models.TimeResponse
import kotlinx.coroutines.launch

class SupportViewModel(
        private val apiRepository: ApiRepository
) : ViewModel() {

    private val _sendMessageResponse: MutableLiveData<Resource<TimeResponse>> = MutableLiveData()
    val sendMessageResponse: LiveData<Resource<TimeResponse>>
        get() = _sendMessageResponse

    fun sendMessage(token: String,number: String,message: String) = viewModelScope.launch {
        _sendMessageResponse.value = apiRepository.sendMessage(token,number,message)
    }


    private val _getMessageResponse: MutableLiveData<Resource<ChatResponse>> = MutableLiveData()
    val getMessageResponse: LiveData<Resource<ChatResponse>>
        get() = _getMessageResponse

    fun getMessage(token: String,number: String,lastUpdate: String) = viewModelScope.launch {
        _getMessageResponse.value = apiRepository.getMessages(token,number,lastUpdate)
    }

}