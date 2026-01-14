package funny.buildapp.pygerdownload.ui.screen.detail

import funny.buildapp.pygerdownload.model.AppInfo
import funny.buildapp.pygerdownload.util.BaseMviViewModel
import funny.buildapp.pygerdownload.util.Constants
import funny.buildapp.pygerdownload.util.Constants.BASE_URL

class DetailViewModel : BaseMviViewModel<DetailUiState, DetailUiAction, DetailUiEffect>(DetailUiState()) {

    override fun handleAction(action: DetailUiAction) {
        when (action) {
            is DetailUiAction.GoBack -> sendEffect { DetailUiEffect.GoBack }
            is DetailUiAction.Download -> downloadApp()
            is DetailUiAction.SelectVersion -> selectVersion(action.index)
            is DetailUiAction.FetchData -> fetchData(action.item)
            is DetailUiAction.DownloadVersion -> downloadVersion(action.appKey, action.password)
        }
    }

    override fun updateLoading() {
        setState { copy(isLoading = loadingCount > 0) }
    }

    private fun fetchData(item: AppInfo) {
        setState { copy(appInfo = item) }
        // 模拟数据获取
//        setState {
//            copy(
//                appName = "抢钢宝",
//                versionName = "v1.1.1",
//                versionCode = "20",
//                buildFileSize = 1023213123,
//                buildCreated = "3分钟前",
//                iconUrl = "",
//                appKey = "example_app_key",
//                buildPassword = "example_password",
//                versionHistory = listOf(
//                    VersionInfo("v1.1.1", "20", "3分钟前", 1023213123, "example_app_key", "example_password"),
//                    VersionInfo(
//                        "v1.1.0",
//                        "19",
//                        "2天前",
//                        1020213123,
//                        "example_app_key_v2",
//                        "example_password_v2"
//                    ),
//                    VersionInfo(
//                        "v1.0.9",
//                        "18",
//                        "1周前",
//                        1019213123,
//                        "example_app_key_v3",
//                        "example_password_v3"
//                    ),
//                    VersionInfo(
//                        "v1.0.8",
//                        "17",
//                        "2周前",
//                        1018213123,
//                        "example_app_key_v4",
//                        "example_password_v4"
//                    )
//                ),
//                isLoading = false
//            )
//        }
    }

    private fun downloadApp() {
//        val appKey = _uiState.value.appKey
//        val password = _uiState.value.buildPassword
//        if (appKey != null && password != null) {
//            downloadVersion(appKey, password)
//        } else {
//            sendEffect { DetailUiEffect.ShowToast("下载信息不完整") }
//        }
    }

    private fun downloadVersion(appKey: String, password: String) {
        val url =
            "${BASE_URL}apiv2/app/install?_api_key=${Constants.API_KEY}&appKey=$appKey&buildPassword=$password"
        // 这里可以添加实际的下载逻辑
        sendEffect { DetailUiEffect.ShowToast("开始下载") }
    }

    private fun selectVersion(index: Int) {
        val versionHistory = _uiState.value.versionHistory
        if (versionHistory != null && index in versionHistory.indices) {
            val selectedVersion = versionHistory[index]
//            setState {
//                copy(
//                    versionName = selectedVersion.versionName,
//                    versionCode = selectedVersion.versionCode,
//                    buildFileSize = selectedVersion.buildFileSize,
//                    buildCreated = selectedVersion.buildCreated,
//                    appKey = selectedVersion.appKey,
//                    buildPassword = selectedVersion.buildPassword
//                )
//            }
        }
    }

}