package at.sunilson.liveticker.liveticker.domain

import at.sunilson.liveticker.core.filterContents
import at.sunilson.liveticker.core.mapContents
import at.sunilson.liveticker.core.models.LiveTickerEntry
import at.sunilson.liveticker.core.sortContents
import at.sunilson.liveticker.core.sortContentsDescending
import at.sunilson.liveticker.core.usecases.AsyncFlowUseCase
import at.sunilson.liveticker.firebasecore.FirebaseOperationException
import com.github.kittinunf.result.coroutines.map
import kotlinx.coroutines.flow.map

class GetLivetickerEntriesUseCase(private val repository: LivetickerRepository) :
    AsyncFlowUseCase<List<LiveTickerEntry>, FirebaseOperationException, String>() {
    override fun run(params: String) =
        repository.getLivetickerEntries(params).sortContentsDescending { it.time }
}