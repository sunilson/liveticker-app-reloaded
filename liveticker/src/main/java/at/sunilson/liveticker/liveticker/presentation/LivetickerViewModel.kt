package at.sunilson.liveticker.liveticker.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import at.sunilson.liveticker.core.models.Comment
import at.sunilson.liveticker.core.models.LiveTicker
import at.sunilson.liveticker.core.models.LiveTickerEntry
import at.sunilson.liveticker.liveticker.data.LivetickerRepository
import at.sunilson.liveticker.presentation.baseClasses.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

abstract class LivetickerViewModel : BaseViewModel() {
    abstract val liveTicker: MutableLiveData<LiveTicker>

    abstract fun loadLiveticker(id: String)
    abstract fun postLivetickerEntry(liveTickerEntry: LiveTickerEntry)
    abstract fun loadComments()
    abstract fun addComment(comment: Comment)
}

class LivetickerViewModelImpl(private val livetickerRepository: LivetickerRepository) : LivetickerViewModel() {

    private var livetickerJob: Job? = null

    override val liveTicker: MutableLiveData<LiveTicker> = MutableLiveData()

    override fun loadLiveticker(id: String) {
        livetickerJob?.cancel()
        livetickerJob = viewModelScope.launch {
            for (liveTicker in livetickerRepository.getLiveTicker(id)) {
                this@LivetickerViewModelImpl.liveTicker.postValue(liveTicker)
            }
        }
    }

    override fun postLivetickerEntry(liveTickerEntry: LiveTickerEntry) {

    }

    override fun loadComments() {

    }

    override fun addComment(comment: Comment) {

    }
}