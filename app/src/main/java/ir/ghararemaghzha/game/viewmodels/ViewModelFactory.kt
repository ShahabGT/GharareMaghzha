package ir.ghararemaghzha.game.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ir.ghararemaghzha.game.data.ApiRepository
import ir.ghararemaghzha.game.data.BaseRepository
import java.lang.IllegalArgumentException

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(
        private val baseRepository: BaseRepository
): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when{
            modelClass.isAssignableFrom(LoginViewModel::class.java)-> LoginViewModel(baseRepository as ApiRepository) as T
            modelClass.isAssignableFrom(RegisterViewModel::class.java)-> RegisterViewModel(baseRepository as ApiRepository) as T
            modelClass.isAssignableFrom(VerifyViewModel::class.java)-> VerifyViewModel(baseRepository as ApiRepository) as T
            modelClass.isAssignableFrom(SupportViewModel::class.java)-> SupportViewModel(baseRepository as ApiRepository) as T
            modelClass.isAssignableFrom(HighscoreViewModel::class.java)-> HighscoreViewModel(baseRepository as ApiRepository) as T
            else -> throw IllegalArgumentException("ViewModel Not Found!!!")
        }
    }
}