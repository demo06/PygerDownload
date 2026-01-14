package funny.buildapp.pygerdownload.ui.screen.detail

import funny.buildapp.pygerdownload.model.AppInfo
import funny.buildapp.pygerdownload.model.AppVersionHistory

data class DetailUiState(
    val isLoading: Boolean = false,
    val appInfo: AppInfo = AppInfo(),
    val versionHistory: AppVersionHistory? = null
)

sealed class DetailUiAction {
    object GoBack : DetailUiAction()
    object Download : DetailUiAction()
    data class SelectVersion(val index: Int) : DetailUiAction()
    data class FetchData(val item: AppInfo) : DetailUiAction()
    data class DownloadVersion(val appKey: String, val password: String) : DetailUiAction()
}

interface DetailUiEffect {
    data class ShowToast(val msg: String) : DetailUiEffect
    data object GoBack : DetailUiEffect
}
