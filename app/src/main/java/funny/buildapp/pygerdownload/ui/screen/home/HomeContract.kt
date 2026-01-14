package funny.buildapp.pygerdownload.ui.screen.home

import android.net.Uri
import funny.buildapp.pygerdownload.model.AppInfo
import funny.buildapp.pygerdownload.model.MiniInfo
import funny.buildapp.pygerdownload.ui.component.DownloadState

/**
 * 单个应用的下载信息
 */
data class AppDownloadInfo(
    val appKey: String,
    val state: DownloadState = DownloadState.IDLE,
    val progress: Int = 0,
    val downloadUri: Uri? = null
)

data class HomeUiState(
    val isRefreshing: Boolean = false,
    val hasUpdate: Boolean = false,
    val isForceUpdate: Boolean = false,
    val isDownloading: Boolean = false,
    val updateContent: String = "",
    val updateProcess: Int = 0,
    val items: List<AppInfo>? = emptyList(),
    val miniItems: List<MiniInfo>? = emptyList(),
    // 每个应用的下载状态 Map，key 为 appKey
    val appDownloadStates: Map<String, AppDownloadInfo> = emptyMap()
)

sealed class HomeUiAction {
    object ShowUpdate : HomeUiAction()
    data class GoDetail(val item: AppInfo) : HomeUiAction()
    data class GoMINIDetail(val item: MiniInfo) : HomeUiAction()
    object Update : HomeUiAction()
    object FetchData : HomeUiAction()
    data class Download(val appKey: String, val password: String) : HomeUiAction()
    data class UpdateDownloadProgress(val progress: Int) : HomeUiAction()

    // 新增：应用下载相关 Action
    data class StartAppDownload(val appKey: String, val password: String) : HomeUiAction()
    data class UpdateAppDownloadProgress(val appKey: String, val progress: Int) : HomeUiAction()
    data class AppDownloadCompleted(val appKey: String, val downloadUri: Uri?) : HomeUiAction()
    data class AppDownloadFailed(val appKey: String) : HomeUiAction()
    data class InstallApp(val appKey: String) : HomeUiAction()
}


interface HomeUiEffect {
    data class ShowToast(val msg: String) : HomeUiEffect
    data class GoDetail(val item: AppInfo) : HomeUiEffect
    data class GoMINIDetail(val item: MiniInfo) : HomeUiEffect
    data class DownLoadApp(val url: String) : HomeUiEffect
    // 新增：开始下载单个应用
    data class StartDownloadApp(val appKey: String, val password: String, val url: String) : HomeUiEffect
    // 新增：安装应用
    data class InstallApp(val downloadUri: Uri) : HomeUiEffect
    // 新增：清除已下载的 APK
    data object ClearDownloadedApks : HomeUiEffect
}