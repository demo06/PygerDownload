package funny.buildapp.pygerdownload.ui.screen.home

import funny.buildapp.pygerdownload.model.AppInfo

data class HomeUiState(
    val isRefreshing: Boolean = false,
    val hasUpdate: Boolean = false,
    val isForceUpdate: Boolean = false,
    val isDownloading: Boolean = false,
    val updateContent: String = "",
    val updateProcess: Int = 0,
    val items: List<AppInfo>? = emptyList()
)

sealed class HomeUiAction {
    object ShowUpdate : HomeUiAction()
    object GoDetail : HomeUiAction()
    object GoMINIDetail : HomeUiAction()
    object Update : HomeUiAction()
    object FetchData : HomeUiAction()
    data class Download(val appKey: String, val password: String) : HomeUiAction()
    data class UpdateDownloadProgress(val progress: Int) : HomeUiAction()

}


interface HomeUiEffect {
    data class ShowToast(val msg: String) : HomeUiEffect
    data object GoDetail : HomeUiEffect
    data object GoMINIDetail : HomeUiEffect
    data class DownLoadApp(val url: String) : HomeUiEffect
}