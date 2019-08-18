package at.sunilson.liveticker.liveticker.presentation.liveticker

import android.view.View
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import at.sunilson.liveticker.core.getOrNull
import at.sunilson.liveticker.core.models.Comment
import at.sunilson.liveticker.core.models.LiveTicker
import at.sunilson.liveticker.core.models.LiveTickerEntry
import at.sunilson.liveticker.liveticker.domain.*
import at.sunilson.liveticker.presentation.baseClasses.BaseViewModel
import at.sunilson.liveticker.presentation.handleObservationResults
import at.sunilson.liveticker.sharing.domain.GetEditUrlUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

abstract class LivetickerViewModel : BaseViewModel<LivetickerNavigationEvent>() {
    abstract val liveTicker: MutableLiveData<LiveTicker>
    abstract val entries: MutableLiveData<List<LiveTickerEntry>>
    abstract val comments: MutableLiveData<List<Comment>>

    abstract fun loadLiveticker(id: String)
    abstract fun loadLivetickerFromShareUrl(shareId: String)
    abstract fun postLivetickerEntry(liveTickerEntry: LiveTickerEntry)
    abstract fun loadComments()
    abstract fun showComments(view: View? = null)
    abstract fun showAddCommentDialog(view: View? = null)
    abstract fun addComment(comment: String)
    abstract fun like(view: View? = null)
    abstract fun share(view: View? = null)
}

sealed class LivetickerNavigationEvent {
    object AddComment : LivetickerNavigationEvent()
    object ShowComments : LivetickerNavigationEvent()
    data class Share(val viewUrl: String, val editUrl: String?) : LivetickerNavigationEvent()
}

class LivetickerViewModelImpl(
    private val getLivetickerUseCase: GetLivetickerUseCase,
    private val addCommentUseCase: AddCommentUseCase,
    private val getCommentsUseCase: GetCommentsUseCase,
    private val cheerUseCase: CheerUseCase,
    private val getEditUrlUseCase: GetEditUrlUseCase
) : LivetickerViewModel() {
    private var commentsInitialized = false
    private var livetickerJob: Job? = null
    private var id: String? = null
    override val liveTicker: MutableLiveData<LiveTicker> = MutableLiveData()
    override val entries: MutableLiveData<List<LiveTickerEntry>> = MutableLiveData()
    override val comments: MutableLiveData<List<Comment>> = MutableLiveData()

    override fun loadLiveticker(id: String) {
        this.id = id
        livetickerJob?.cancel()
        livetickerJob = viewModelScope.launch {
            getLivetickerUseCase(GetLivetickerParams(id)).collect { result ->
                result.fold(
                    { liveTicker ->
                        this@LivetickerViewModelImpl.liveTicker.postValue(liveTicker)
                    },
                    {
                        //TODO Error handling
                    }
                )
            }
        }
    }

    override fun loadLivetickerFromShareUrl(shareId: String) {
        livetickerJob?.cancel()
        livetickerJob = viewModelScope.launch {
            getLivetickerUseCase(GetLivetickerParams(livetickerSharingId = shareId)).collect { result ->
                result.fold(
                    { liveTicker ->
                        id = liveTicker.id
                        this@LivetickerViewModelImpl.liveTicker.postValue(liveTicker)
                    },
                    {
                        //TODO Error handling
                    }
                )
            }
        }
    }

    override fun postLivetickerEntry(liveTickerEntry: LiveTickerEntry) {
    }

    override fun loadComments() {
        if (commentsInitialized) return
        commentsInitialized = true

        viewModelScope.launch {
            getCommentsUseCase(id ?: return@launch).collect { result ->
                result.fold(
                    {
                        Timber.d("Got comment Observationresults: $it")
                        launch(Dispatchers.Main) { comments.value = it }
                    },
                    { error ->
                        Timber.e(error, "Error loading comments")
                        //TODO
                    }
                )
            }
        }
    }

    override fun showComments(view: View?) {
        navigationEvents.postValue(LivetickerNavigationEvent.ShowComments)
    }

    override fun showAddCommentDialog(view: View?) {
        Timber.d("Show add Comment dialog clicked")
        navigationEvents.postValue(LivetickerNavigationEvent.AddComment)
    }

    override fun addComment(comment: String) {
        viewModelScope.launch {
            addCommentUseCase(AddCommentParams(id ?: return@launch, comment)).fold(
                {
                    Timber.d("Added comment $comment")
                },
                {
                    Timber.e(it, "Error adding comment!")
                }
            )
        }
    }

    override fun share(view: View?) {
        viewModelScope.launch {
            val editUrl = getEditUrlUseCase(id ?: return@launch).getOrNull()
            navigationEvents.postValue(
                LivetickerNavigationEvent.Share(
                    liveTicker.value?.sharingUrl ?: return@launch,
                    editUrl
                )
            )
        }
    }

    override fun like(view: View?) {
        viewModelScope.launch {
            cheerUseCase(id ?: return@launch).fold(
                {},
                {
                    Timber.e(it, "Error liking liveticker!")
                }
            )
        }
    }
}