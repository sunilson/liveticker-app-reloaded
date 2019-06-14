package at.sunilson.liveticker.home.presentation.home

import at.sunilson.liveticker.core.models.LiveTicker

sealed class LivetickerSelectedAction
data class LivetickerClicked(val liveticker: LiveTicker) : LivetickerSelectedAction()
data class ShareClicked(val liveticker: LiveTicker) : LivetickerSelectedAction()
data class DeleteClicked(val liveticker: LiveTicker) : LivetickerSelectedAction()