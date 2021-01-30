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

class ProfileEditViewModel(
        private val apiRepository: ApiRepository
) : ViewModel() {

    private val _removeAvatarResponse: MutableLiveData<Resource<GeneralResponse>> = MutableLiveData()
    val removeAvatarResponse: LiveData<Resource<GeneralResponse>>
        get() = _removeAvatarResponse

    fun removeAvatar(token: String,number: String,avatar:String) = viewModelScope.launch {
        _removeAvatarResponse.value = apiRepository.alterAvatar(token,number,"remove", "", avatar)
    }


    private val _changeAvatarResponse: MutableLiveData<Resource<GeneralResponse>> = MutableLiveData()
    val changeAvatarResponse: LiveData<Resource<GeneralResponse>>
        get() = _changeAvatarResponse

    fun changeAvatar(token: String,number: String,image:String,avatar:String) = viewModelScope.launch {
        _changeAvatarResponse.value = apiRepository.alterAvatar(token,number,"change", image, avatar)
    }

    private val _updateProfileResponse: MutableLiveData<Resource<GeneralResponse>> = MutableLiveData()
    val updateProfileResponse: LiveData<Resource<GeneralResponse>>
        get() = _updateProfileResponse

    fun updateProfile(token: String,number: String,name:String, email:String, bday:String, sex:String, inviteCode:String) = viewModelScope.launch {
        _updateProfileResponse.value = apiRepository.updateProfile(token,number,name,email,bday,sex,inviteCode)
    }

}