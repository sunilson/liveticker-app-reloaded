package at.sunilson.liveticker.liveticker.presentation

import android.view.View
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import at.sunilson.liveticker.core.models.Comment
import at.sunilson.liveticker.core.models.LiveTicker
import at.sunilson.liveticker.core.models.LiveTickerEntry
import at.sunilson.liveticker.firebasecore.ObservationResult
import at.sunilson.liveticker.liveticker.domain.AddCommentParams
import at.sunilson.liveticker.liveticker.domain.AddCommentUseCase
import at.sunilson.liveticker.liveticker.domain.GetCommentsUseCase
import at.sunilson.liveticker.liveticker.domain.GetLivetickerUseCase
import at.sunilson.liveticker.presentation.baseClasses.BaseViewModel
import at.sunilson.liveticker.presentation.baseClasses.NavigationEvent
import at.sunilson.liveticker.presentation.removeWithId
import at.sunilson.liveticker.presentation.updateWithId
import com.github.kittinunf.result.coroutines.success
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
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

    object AddComment : NavigationEvent
}

class LivetickerViewModelImpl(
    private val getLivetickerUseCase: GetLivetickerUseCase,
    private val addCommentUseCase: AddCommentUseCase,
    private val getCommentsUseCase: GetCommentsUseCase
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
            getLivetickerUseCase(id).success {
                for (liveTicker in it) {
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

        viewModelScope.launch(Dispatchers.Default) {
            getCommentsUseCase(id ?: return@launch).success {
                for (result in it) {
                    result.fold(
                        { observationResult ->
                            Timber.d("Got comment Observationresult: $observationResult")
                            launch(Dispatchers.Main) {
                                when (observationResult) {
                                    is ObservationResult.Added -> comments.add(observationResult.data)
                                    is ObservationResult.Modified -> comments.updateWithId(observationResult.data)
                                    is ObservationResult.Deleted -> comments.removeWithId(observationResult.data)
                                }
                            }
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
}