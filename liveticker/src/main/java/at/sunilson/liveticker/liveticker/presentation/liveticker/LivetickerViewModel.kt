package at.sunilson.liveticker.liveticker.presentation.liveticker

import android.net.Uri
import android.util.Log
import android.view.View
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import at.sunilson.liveticker.authentication.IAuthenticationRepository
import at.sunilson.liveticker.core.getOrNull
import at.sunilson.liveticker.core.models.Comment
import at.sunilson.liveticker.core.models.LiveTicker
import at.sunilson.liveticker.core.models.LiveTickerEntry
import at.sunilson.liveticker.liveticker.domain.*
import at.sunilson.liveticker.presentation.baseClasses.BaseViewModel
import at.sunilson.liveticker.presentation.handleObservationResults
import at.sunilson.liveticker.sharing.domain.GetEditUrlUseCase
import com.github.kittinunf.result.coroutines.SuspendableResult
import com.github.kittinunf.result.coroutines.success
import com.github.kittinunf.result.success
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File

abstract class LivetickerViewModel : BaseViewModel<LivetickerNavigationEvent>() {
    abstract val liveTicker: MutableLiveData<LiveTicker>
    abstract val entries: MutableLiveData<List<LiveTickerEntry>>
    abstract val images: MutableLiveData<List<Uri>>
    abstract val comments: MutableLiveData<List<Comment>>
    abstract val isAuthor: MutableLiveData<Boolean>
    abstract val entryText: MutableLiveData<String>

    abstract fun loadLiveticker(id: String)
    abstract fun loadLivetickerFromShareUrl(shareId: String)
    abstract fun postTextLivetickerEntry(view: View? = null)
    abstract fun postLivetickerEntry(liveTickerEntry: LiveTickerEntry)
    abstract fun loadComments()
    abstract fun showComments(view: View? = null)
    abstract fun showAddDialog(view: View? = null)
    abstract fun showAddCommentDialog(view: View? = null)
    abstract fun addComment(comment: String)
    abstract fun like(view: View? = null)
    abstract fun share(view: View? = null)
    abstract fun toggleNotifications(view: View? = null)
    abstract fun takePicture(view: View? = null)
    abstract fun loadImages()
}

sealed class LivetickerNavigationEvent {
    object AddComment : LivetickerNavigationEvent()
    object ShowComments : LivetickerNavigationEvent()
    object AddEntry : LivetickerNavigationEvent()
    data class TakePicture(val imageUri: Uri) : LivetickerNavigationEvent()
    data class Share(val viewUrl: String, val editUrl: String?) : LivetickerNavigationEvent()
}

class LivetickerViewModelImpl(
    private val getLivetickerUseCase: GetLivetickerUseCase,
    private val addCommentUseCase: AddCommentUseCase,
    private val getCommentsUseCase: GetCommentsUseCase,
    private val cheerUseCase: CheerUseCase,
    private val getEditUrlUseCase: GetEditUrlUseCase,
    private val setNotificationUseCase: SetNotificationUseCase,
    private val getLocalImagesUseCase: GetLocalImagesUseCase,
    private val createImageFileUseCase: CreateImageFileUseCase,
    private val postLivetickerTextEntry: PostLivetickerTextEntry,
    private val postLivetickerImageEntry: PostLivetickerImageEntry,
    private val getLivetickerEntriesUseCase: GetLivetickerEntriesUseCase
) : LivetickerViewModel() {
    private var commentsInitialized = false

    private var livetickerJob: Job? = null
    private var entryJob: Job? = null

    private var id: String? = null
    override val liveTicker: MutableLiveData<LiveTicker> = MutableLiveData()
    override val entries: MutableLiveData<List<LiveTickerEntry>> = MutableLiveData()
    override val comments: MutableLiveData<List<Comment>> = MutableLiveData()
    override val isAuthor: MutableLiveData<Boolean> = MutableLiveData()
    override val images: MutableLiveData<List<Uri>> = MutableLiveData()
    override val entryText: MutableLiveData<String> = MutableLiveData()

    override fun loadLiveticker(id: String) {
        this.id = id
        getLivetickerUpdates(id)
        getLivetickerEntryUpdates(id)
    }

    override fun loadLivetickerFromShareUrl(shareId: String) {
        getLivetickerUpdates(livetickerSharingId = shareId)
        getLivetickerEntryUpdates(livetickerSharingId = shareId)
    }

    private fun getLivetickerUpdates(id: String = "", livetickerSharingId: String = "") {
        livetickerJob?.cancel()
        livetickerJob = viewModelScope.launch {
            if (id.isNotEmpty()) {
                getLivetickerUseCase(GetLivetickerParams(id)).collect { handleLivetickerResult(it) }
            } else {
                getLivetickerUseCase(GetLivetickerParams(livetickerSharingId = livetickerSharingId))
                    .collect { handleLivetickerResult(it) }
            }
        }
    }

    private fun getLivetickerEntryUpdates(
        id: String = "",
        livetickerSharingId: String = ""
    ) {
        entryJob?.cancel()
        entryJob = viewModelScope.launch {
            getLivetickerEntriesUseCase(id).collect {
                it.fold(
                    { entries.value = it },
                    {}
                )
            }
        }
    }

    override fun postTextLivetickerEntry(view: View?) {
        val text = entryText.value ?: return
        val id = this.id ?: return
        viewModelScope.launch {
            postLivetickerTextEntry(PostLivetickerTextEntry.Params(text, id)).fold(
                { entryText.value = null },
                { Timber.e(it) }
            )
        }
    }

    override fun postLivetickerEntry(liveTickerEntry: LiveTickerEntry) {}

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

    override fun toggleNotifications(view: View?) {
        viewModelScope.launch {
            setNotificationUseCase(
                SetNotificationsParams(true, liveTicker.value?.id ?: return@launch)
            ).fold(
                {},
                {}
            )
        }
    }

    override fun showComments(view: View?) {
        navigationEvents.postValue(LivetickerNavigationEvent.ShowComments)
    }

    override fun showAddDialog(view: View?) {
        navigationEvents.postValue(LivetickerNavigationEvent.AddEntry)
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
                { Timber.e(it, "Error liking liveticker!") }
            )
        }
    }

    override fun takePicture(view: View?) {
        createImageFileUseCase(Unit).success {
            navigationEvents.postValue(LivetickerNavigationEvent.TakePicture(it))
        }
    }

    override fun loadImages() {
        viewModelScope.launch {
            getLocalImagesUseCase(Unit).success { images.value = it }
        }
    }

    private suspend fun handleLivetickerResult(result: SuspendableResult<GetLivetickerResult, GetLivetickerException>) {
        result.fold(
            {
                id = it.liveticker.id
                this.liveTicker.value = it.liveticker
                this.isAuthor.value = it.isAuthor
            },
            {
                //TODO Error handling
            }
        )
    }
}