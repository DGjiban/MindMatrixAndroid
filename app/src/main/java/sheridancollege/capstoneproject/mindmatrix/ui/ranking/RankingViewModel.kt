package sheridancollege.capstoneproject.mindmatrix.ui.ranking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RankingViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Ranking Fragment"
    }
    val text: LiveData<String> = _text
}