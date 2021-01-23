package ir.ghararemaghzha.game.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.ghararemaghzha.game.data.ApiRepository
import ir.ghararemaghzha.game.data.Resource
import ir.ghararemaghzha.game.models.*
import kotlinx.coroutines.launch

class ContactsViewModel(
        private val apiRepository: ApiRepository
) : ViewModel() {

    private val _syncContactsResponse: MutableLiveData<Resource<ContactsResponse>> = MutableLiveData()
    val syncContactsResponse: LiveData<Resource<ContactsResponse>>
        get() = _syncContactsResponse

    fun syncContacts(token: String,body: ContactBody) = viewModelScope.launch {
        _syncContactsResponse.value = apiRepository.syncContacts(token,body)
    }

}