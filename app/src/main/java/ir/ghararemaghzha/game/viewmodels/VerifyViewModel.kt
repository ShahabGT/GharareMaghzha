package ir.ghararemaghzha.game.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.ghararemaghzha.game.data.ApiRepository
import ir.ghararemaghzha.game.data.Resource
import ir.ghararemaghzha.game.models.GeneralResponse
import ir.ghararemaghzha.game.models.QuestionResponse
import kotlinx.coroutines.launch

class VerifyViewModel(
        private val apiRepository: ApiRepository
) : ViewModel() {

    private val _verificationResponse: MutableLiveData<Resource<GeneralResponse>> = MutableLiveData()
    val verificationResponse: LiveData<Resource<GeneralResponse>>
        get() = _verificationResponse

    fun verify(number:String,code: String,fbToken: String) = viewModelScope.launch {
        _verificationResponse.value = apiRepository.verification(number,code,fbToken)
    }


    private val _resendResponse: MutableLiveData<Resource<GeneralResponse>> = MutableLiveData()
    val resendResponse: LiveData<Resource<GeneralResponse>>
        get() = _resendResponse

    fun resend(number:String) = viewModelScope.launch {
        _resendResponse.value = apiRepository.resend(number)
    }

    private val _questionResponse: MutableLiveData<Resource<QuestionResponse>> = MutableLiveData()
    val questionResponse: LiveData<Resource<QuestionResponse>>
        get() = _questionResponse

    fun getQuestions(token:String,number:String) = viewModelScope.launch {
        _questionResponse.value = apiRepository.getQuestions(token,number)
    }

}