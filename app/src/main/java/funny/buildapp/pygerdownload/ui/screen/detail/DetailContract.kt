package funny.buildapp.pygerdownload.ui.screen.detail

import android.net.Uri
import funny.buildapp.pygerdownload.model.AppInfo
import funny.buildapp.pygerdownload.model.AppVersionHistory
import funny.buildapp.pygerdownload.ui.component.DownloadState

data class DetailUiState(
    val isLoading: Boolean = false,
    val appInfo: AppInfo = AppInfo(),
    val versionHistory: AppVersionHistory? = null,
    // 下载状态
    val downloadState: DownloadState = DownloadState.IDLE,
    val downloadProgress: Int = 0,
    val downloadId: Long = -1,
    // 版本历史下载状态 Map，key 为 versionIndex
    val versionDownloadStates: Map<Int, DownloadState> = emptyMap(),
    val versionDownloadProgress: Map<Int, Int> = emptyMap(),
    val versionDownloadIds: Map<Int, Long> = emptyMap(),
    // 分页
    val page: Int = 1,
    val hasMore: Boolean = true
)

sealed class DetailUiAction {
    object GoBack : DetailUiAction()
    object Download : DetailUiAction()
    data class SelectVersion(val index: Int) : DetailUiAction()
    data class FetchData(val item: AppInfo) : DetailUiAction()
    data class DownloadVersion(val appKey: String, val password: String) : DetailUiAction()
    object LoadMore : DetailUiAction()

    // 新增：下载状态控制
    data class StartDownload(val appKey: String, val password: String) : DetailUiAction()
    data class UpdateDownloadProgress(val progress: Int) : DetailUiAction()
    data class DownloadCompleted(val downloadId: Long) : DetailUiAction()
    object DownloadFailed : DetailUiAction()
    object Install : DetailUiAction()

    // 新增：版本历史下载状态控制
    data class StartVersionDownload(val index: Int, val appKey: String, val password: String) : DetailUiAction()
    data class UpdateVersionDownloadProgress(val index: Int, val progress: Int) : DetailUiAction()
    data class VersionDownloadCompleted(val index: Int, val downloadId: Long) : DetailUiAction()
    data class VersionDownloadFailed(val index: Int) : DetailUiAction()
    data class InstallVersion(val index: Int) : DetailUiAction()
}

interface DetailUiEffect {
    data class ShowToast(val msg: String) : DetailUiEffect
    data object GoBack : DetailUiEffect
    // 新增：开始下载
    data class StartDownload(val appKey: String, val url: String) : DetailUiEffect
    // 新增：安装
    data class Install(val downloadId: Long) : DetailUiEffect
    // 新增：版本下载
    data class StartVersionDownload(val index: Int, val appKey: String, val url: String) : DetailUiEffect
}
