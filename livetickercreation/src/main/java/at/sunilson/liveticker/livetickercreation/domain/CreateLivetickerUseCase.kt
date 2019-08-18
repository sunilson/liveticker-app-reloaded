package at.sunilson.liveticker.livetickercreation.domain

import at.sunilson.liveticker.authentication.IAuthenticationRepository
import at.sunilson.liveticker.core.usecases.AsyncUseCase
import at.sunilson.liveticker.core.models.LiveTicker
import at.sunilson.liveticker.core.models.Location
import at.sunilson.liveticker.livetickercreation.data.LivetickerCreationRepository
import com.github.kittinunf.result.coroutines.SuspendableResult

data class CreateLivetickerParams(
    val title: String?,
    val shortDescription: String?,
    val description: String?,
    val location: Location?
)

class CreateLivetickerUseCase(
    private val livetickerCreationRepository: LivetickerCreationRepository,
    private val authenticationRepository: IAuthenticationRepository
) :
    AsyncUseCase<Unit, Exception, CreateLivetickerParams>() {
    override suspend fun run(params: CreateLivetickerParams): SuspendableResult<Unit, Exception> {

        val (user, userError) = authenticationRepository.getCurrentUserNow()
        if (userError != null) return SuspendableResult.error(userError)
        if (user == null) return SuspendableResult.error(Exception())
        if (user.anonymous) return SuspendableResult.error(NotAllowed())
        if (params.title.isNullOrEmpty()) return SuspendableResult.error(TitleInvalid())
        if (params.shortDescription.isNullOrEmpty()) return SuspendableResult.error(ShortDescriptionInvalid())
        if (params.description.isNullOrEmpty()) return SuspendableResult.error(DescriptionInvalid())
        if (params.location == null) return SuspendableResult.error(LocationInvalid())

        val (livetickerResult, livetickerError) = livetickerCreationRepository.createLiveticker(
            LiveTicker(
                authorName = user.name,
                authorId = user.id,
                title = params.title,
                shortDescription = params.shortDescription,
                description = params.description
            )
        )

        if (livetickerError != null) return SuspendableResult.error(livetickerError)

        return SuspendableResult.Success(Unit)
    }
}

sealed class LivetickerCreationValidationException : Exception()
class NotAllowed : LivetickerCreationValidationException()
class TitleInvalid : LivetickerCreationValidationException()
class ShortDescriptionInvalid : LivetickerCreationValidationException()
class DescriptionInvalid : LivetickerCreationValidationException()
class LocationInvalid : LivetickerCreationValidationException()