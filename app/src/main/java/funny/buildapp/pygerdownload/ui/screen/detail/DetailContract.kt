package funny.buildapp.pygerdownload.ui.screen.detail

data class DetailUiState(
    val isLoading: Boolean = false,
    val appName: String? = null,
    val versionName: String? = null,
    val versionCode: String? = null,
    val buildFileSize: Int? = null,
    val buildCreated: String? = null,
    val iconUrl: String? = null,
    val appKey: String? = null,
    val buildPassword: String? = null,
    val versionHistory: List<VersionInfo>? = null
)

sealed class DetailUiAction {
    object GoBack : DetailUiAction()
    object Download : DetailUiAction()
    data class SelectVersion(val index: Int) : DetailUiAction()
    object FetchData : DetailUiAction()
    data class DownloadVersion(val appKey: String, val password: String) : DetailUiAction()
}

interface DetailUiEffect {
    data class ShowToast(val msg: String) : DetailUiEffect
    data object GoBack : DetailUiEffect
}

data class VersionInfo(
    val versionName: String,
    val versionCode: String,
    val buildCreated: String,
    val buildFileSize: Int,
    val appKey: String? = null,
    val buildPassword: String? = null
)