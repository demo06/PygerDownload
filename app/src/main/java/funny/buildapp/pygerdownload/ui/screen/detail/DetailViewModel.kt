package funny.buildapp.pygerdownload.ui.screen.detail

import funny.buildapp.pygerdownload.model.AppInfo
import funny.buildapp.pygerdownload.net.ApiService
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
        getAppVersionHistory()
    }


    private fun getAppVersionHistory() {
        setState { copy(isLoading = true) }
        request(
            api = {
                ApiService.instance()
                    .versionHistory(Constants.API_KEY, _uiState.value.appInfo.appKey ?: "", 1)
            },
            onFailed = { setState { copy(isLoading = false) } },
            onSuccess = {
                setState { copy(versionHistory = it, isLoading = false) }

            }
        )
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
//        val versionHistory = _uiState.value.versionHistory
//        if (versionHistory != null && index in versionHistory.indices) {
//            val selectedVersion = versionHistory[index]
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
//        }
    }

}