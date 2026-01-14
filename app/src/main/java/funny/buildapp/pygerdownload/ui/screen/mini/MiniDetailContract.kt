package funny.buildapp.pygerdownload.ui.screen.mini

import funny.buildapp.pygerdownload.model.MiniInfo

data class MiniDetailUiState(
    val item: MiniInfo = MiniInfo(),
)

sealed class MiniDetailUiAction {
    object GoBack : MiniDetailUiAction()
    object GoPreview : MiniDetailUiAction()
    object GoRelease : MiniDetailUiAction()
    data class FetchData(val item: MiniInfo) : MiniDetailUiAction()
}

interface MiniDetailUiEffect {
    data class ShowToast(val msg: String) : MiniDetailUiEffect
    data object GoBack : MiniDetailUiEffect
    data class GoMini(val isPreview: Boolean) : MiniDetailUiEffect
}