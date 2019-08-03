package at.sunilson.liveticker.liveticker.presentation.liveticker

import android.view.View
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import at.sunilson.liveticker.core.models.Comment
import at.sunilson.liveticker.core.models.LiveTicker
import at.sunilson.liveticker.core.models.LiveTickerEntry
import at.sunilson.liveticker.liveticker.domain.*
import at.sunilson.liveticker.presentation.baseClasses.BaseViewModel
import at.sunilson.liveticker.presentation.baseClasses.NavigationEvent
import at.sunilson.liveticker.presentation.handleObservationResults
import com.github.kittinunf.result.coroutines.success
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

abstract class LivetickerViewModel : BaseViewModel() {
    abstract val liveTicker: MutableLiveData<LiveTicker>
    abstract val entries: ObservableList<LiveTickerEntry>
    abstract val comments: ObservableList<Comment>

    abstract fun loadLiveticker(id: String)
    abstract fun postLivetickerEntry(liveTickerEntry: LiveTickerEntry)
    abstract fun loadComments()
    abstract fun showAddCommentDialog(view: View? = null)
    abstract fun addComment(comment: String)
    abstract fun like(view: View? = null)
    abstract fun share(view: View? = null)

    object AddComment : NavigationEvent
    object Share: NavigationEvent
}

class LivetickerViewModelImpl(
    private val getLivetickerUseCase: GetLivetickerUseCase,
    private val addCommentUseCase: AddCommentUseCase,
    private val getCommentsUseCase: GetCommentsUseCase,
    private val cheerUseCase: CheerUseCase
) : LivetickerViewModel() {

    private var commentsInitialized = false
    private var livetickerJob: Job? = null
    private var id: String? = null
    override val liveTicker: MutableLiveData<LiveTicker> = MutableLiveData()
    override val entries: ObservableList<LiveTickerEntry> = ObservableArrayList()
    override val comments: ObservableList<Comment> = ObservableArrayList()

    override fun loadLiveticker(id: String) {
        this.id = id
        livetickerJob?.cancel()
        livetickerJob = viewModelScope.launch {
            getLivetickerUseCase(id).success { flow ->
                flow.collect { liveTicker ->
                    liveTicker.fold(
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
    }

    override fun postLivetickerEntry(liveTickerEntry: LiveTickerEntry) {

    }

    override fun loadComments() {
        if (commentsInitialized) return
        commentsInitialized = true

        viewModelScope.launch {
            getCommentsUseCase(id ?: return@launch).success { flow ->
                flow.collect { result ->
                    result.fold(
                        { observationResults ->
                            Timber.d("Got comment Observationresults: $observationResults")
                            launch(Dispatchers.Main) { comments.handleObservationResults(observationResults) }
                        },
                        { error ->
                            Timber.e(error, "Error loading comments")
                            //TODO
                        }
                    )
                }
            }
        }
    }

    override fun showAddCommentDialog(view: View?) {
        Timber.d("Show add Comment dialog clicked")
        navigationEvents.postValue(AddComment)
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
        navigationEvents.postValue(Share)
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