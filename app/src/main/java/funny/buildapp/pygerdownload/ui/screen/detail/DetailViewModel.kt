package funny.buildapp.pygerdownload.ui.screen.detail

import android.net.Uri
import funny.buildapp.clauncher.util.downloadUrl
import funny.buildapp.clauncher.util.downloadVersionUrl
import funny.buildapp.pygerdownload.model.AppInfo
import funny.buildapp.pygerdownload.net.ApiService
import funny.buildapp.pygerdownload.ui.component.DownloadState
import funny.buildapp.pygerdownload.util.BaseMviViewModel
import funny.buildapp.pygerdownload.util.Constants

class DetailViewModel : BaseMviViewModel<DetailUiState, DetailUiAction, DetailUiEffect>(DetailUiState()) {

    override fun handleAction(action: DetailUiAction) {
        when (action) {
            is DetailUiAction.GoBack -> sendEffect { DetailUiEffect.GoBack }
            is DetailUiAction.Download -> {
                // 触发开始下载
                val appKey = _uiState.value.appInfo.appKey ?: ""
                val password = _uiState.value.appInfo.buildPassword ?: ""
                startDownload(appKey, password)
            }
            is DetailUiAction.SelectVersion -> selectVersion(action.index)
            is DetailUiAction.FetchData -> fetchData(action.item)
            is DetailUiAction.DownloadVersion -> downloadVersion(action.appKey, action.password)
            is DetailUiAction.LoadMore -> loadMore()

            // 新增：下载状态控制
            // ... (keep existing actions)
            is DetailUiAction.StartDownload -> startDownload(action.appKey, action.password)
            is DetailUiAction.UpdateDownloadProgress -> updateDownloadProgress(action.progress)
            is DetailUiAction.DownloadCompleted -> downloadCompleted(action.downloadUri)
            is DetailUiAction.DownloadFailed -> downloadFailed()
            is DetailUiAction.Install -> install()

            // 新增：版本历史下载状态控制
            is DetailUiAction.StartVersionDownload -> startVersionDownload(action.index, action.appKey, action.password)
            is DetailUiAction.UpdateVersionDownloadProgress -> updateVersionDownloadProgress(action.index, action.progress)
            is DetailUiAction.VersionDownloadCompleted -> versionDownloadCompleted(action.index, action.downloadUri)
            is DetailUiAction.VersionDownloadFailed -> versionDownloadFailed(action.index)
            is DetailUiAction.InstallVersion -> installVersion(action.index)
        }
    }

    override fun updateLoading() {
        setState { copy(isLoading = loadingCount > 0) }
    }

    private fun fetchData(item: AppInfo) {
        // 重置为第一页
        setState { copy(appInfo = item, page = 1, hasMore = true, versionHistory = null) }
        getAppVersionHistory(1)
    }

    private fun loadMore() {
        val state = _uiState.value
        if (state.isLoading || !state.hasMore) return
        val nextPage = state.page + 1
        getAppVersionHistory(nextPage)
    }

    private fun getAppVersionHistory(page: Int) {
        setState { copy(isLoading = true) }
        request(
            api = {
                ApiService.instance()
                    .versionHistory(Constants.API_KEY, _uiState.value.appInfo.appKey ?: "", page)
            },
            onFailed = { setState { copy(isLoading = false) } },
            onSuccess = { history ->
                setState {
                    val newVersions = history.list
                    val currentVersions = if (page == 1) {
                        emptyList()
                    } else {
                        versionHistory?.list ?: emptyList()
                    }
                    val mergedVersions = currentVersions + newVersions
                    
                    // 简单判断：如果新返回的数据为空，则没有更多了
                    // 或者如果返回的数量少于预期（假定每页至少1条或特定数量），这里只判断是否为空
                    val hasMore = newVersions.isNotEmpty()

                    // 注意：这里我们创建一个新的 AppVersionHistory 对象，因为 AppVersionHistory 是数据类
                    // 假设 AppVersionHistory 只有一个 list 属性
                    val newHistory = history.copy(list = mergedVersions)

                    copy(
                        versionHistory = newHistory,
                        isLoading = false,
                        page = page,
                        hasMore = hasMore
                    )
                }
            }
        )
    }

    /**
     * 开始下载主应用
     */
    private fun startDownload(appKey: String, password: String) {
        setState {
            copy(
                downloadState = DownloadState.DOWNLOADING,
                downloadProgress = 0
            )
        }
        val url = downloadUrl(appKey, password)
        sendEffect { DetailUiEffect.StartDownload(appKey, url) }
    }

    /**
     * 更新下载进度
     */
    private fun updateDownloadProgress(progress: Int) {
        setState {
            copy(downloadProgress = progress)
        }
    }

    /**
     * 下载完成
     */
    private fun downloadCompleted(downloadUri: Uri?) {
        setState {
            copy(
                downloadState = DownloadState.COMPLETED,
                downloadProgress = 100,
                downloadUri = downloadUri
            )
        }
    }

    /**
     * 下载失败
     */
    private fun downloadFailed() {
        setState {
            copy(
                downloadState = DownloadState.IDLE,
                downloadProgress = 0
            )
        }
        sendEffect { DetailUiEffect.ShowToast("下载失败") }
    }

    /**
     * 安装应用
     */
    private fun install() {
        val uri = _uiState.value.downloadUri
        if (uri != null) {
            sendEffect { DetailUiEffect.Install(uri) }
        } else {
            sendEffect { DetailUiEffect.ShowToast("安装文件不存在") }
        }
    }

    /**
     * 开始下载版本历史中的某个版本
     */
    private fun startVersionDownload(index: Int, appKey: String, password: String) {
        setState {
            val newStates = versionDownloadStates.toMutableMap()
            val newProgress = versionDownloadProgress.toMutableMap()
            newStates[index] = DownloadState.DOWNLOADING
            newProgress[index] = 0
            copy(versionDownloadStates = newStates, versionDownloadProgress = newProgress)
        }
        val url = downloadVersionUrl(appKey, password)
        sendEffect { DetailUiEffect.StartVersionDownload(index, appKey, url) }
    }

    /**
     * 更新版本下载进度
     */
    private fun updateVersionDownloadProgress(index: Int, progress: Int) {
        setState {
            val newProgress = versionDownloadProgress.toMutableMap()
            newProgress[index] = progress
            copy(versionDownloadProgress = newProgress)
        }
    }

    /**
     * 版本下载完成
     */
    private fun versionDownloadCompleted(index: Int, downloadUri: Uri?) {
        setState {
            val newStates = versionDownloadStates.toMutableMap()
            val newProgress = versionDownloadProgress.toMutableMap()
            val newUris = versionDownloadUris.toMutableMap()
            newStates[index] = DownloadState.COMPLETED
            newProgress[index] = 100
            newUris[index] = downloadUri
            copy(
                versionDownloadStates = newStates,
                versionDownloadProgress = newProgress,
                versionDownloadUris = newUris
            )
        }
    }

    /**
     * 版本下载失败
     */
    private fun versionDownloadFailed(index: Int) {
        setState {
            val newStates = versionDownloadStates.toMutableMap()
            val newProgress = versionDownloadProgress.toMutableMap()
            newStates[index] = DownloadState.IDLE
            newProgress[index] = 0
            copy(versionDownloadStates = newStates, versionDownloadProgress = newProgress)
        }
        sendEffect { DetailUiEffect.ShowToast("下载失败") }
    }

    /**
     * 安装版本
     */
    private fun installVersion(index: Int) {
        val uri = _uiState.value.versionDownloadUris[index]
        if (uri != null) {
            sendEffect { DetailUiEffect.Install(uri) }
        } else {
            sendEffect { DetailUiEffect.ShowToast("安装文件不存在") }
        }
    }

    private fun downloadVersion(appKey: String, password: String) {
        val url = downloadUrl(appKey, password)
        sendEffect { DetailUiEffect.ShowToast("开始下载") }
    }

    private fun selectVersion(index: Int) {
        // 版本选择逻辑已移到下载按钮点击事件
    }

}