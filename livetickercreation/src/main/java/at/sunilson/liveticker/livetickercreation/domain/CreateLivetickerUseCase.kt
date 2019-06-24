package at.sunilson.liveticker.livetickercreation.domain

import at.sunilson.liveticker.authentication.IAuthenticationRepository
import at.sunilson.liveticker.core.AsyncUseCase
import at.sunilson.liveticker.core.models.LiveTicker
import at.sunilson.liveticker.core.models.Location
import at.sunilson.liveticker.livetickercreation.data.LivetickerCreationRepository
import com.github.kittinunf.result.Result

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
    AsyncUseCase<Unit, CreateLivetickerParams>() {
    override suspend fun run(params: CreateLivetickerParams): Result<Unit, Exception> {

        val (user, userError) = authenticationRepository.getCurrentUserNow()
        if (userError != null) return Result.error(userError)
        if (user == null) return Result.error(Exception())
        if (user.anonymous) return Result.error(NotAllowed())
        if (params.title.isNullOrEmpty()) return Result.error(TitleInvalid())
        if (params.shortDescription.isNullOrEmpty()) return Result.error(ShortDescriptionInvalid())
        if (params.description.isNullOrEmpty()) return Result.error(DescriptionInvalid())
        if (params.location == null) return Result.error(LocationInvalid())

        val (livetickerResult, livetickerError) = livetickerCreationRepository.createLiveticker(
            LiveTicker(
                authorName = user.name,
                authorId = user.id,
                title = params.title,
                shortDescription = params.shortDescription,
                description = params.description
            )
        )

        if (livetickerError != null) return Result.error(livetickerError)

        return Result.success(Unit)
    }
}

sealed class LivetickerCreationValidationException : Exception()
class NotAllowed : LivetickerCreationValidationException()
class TitleInvalid : LivetickerCreationValidationException()
class ShortDescriptionInvalid : LivetickerCreationValidationException()
class DescriptionInvalid : LivetickerCreationValidationException()
class LocationInvalid : LivetickerCreationValidationException()