package funny.buildapp.pygerdownload.ui.screen.home

import funny.buildapp.clauncher.util.downloadUrl
import funny.buildapp.pygerdownload.BuildConfig
import funny.buildapp.pygerdownload.net.ApiService
import funny.buildapp.pygerdownload.util.BaseMviViewModel
import funny.buildapp.pygerdownload.util.Constants

/**
 * @author WenBin
 * @version 1.0
 * @description ViewModel for MainActivity
 * @email wenbin@buildapp.fun
 * @date 2022/6/16
 */
class HomeViewModel : BaseMviViewModel<HomeUiState, HomeUiAction, HomeUiEffect>(HomeUiState()) {

    init {
        checkUpdate()
    }

    override fun handleAction(action: HomeUiAction) {
        when (action) {
            is HomeUiAction.GoDetail -> sendEffect { HomeUiEffect.GoDetail }
            is HomeUiAction.GoMINIDetail -> sendEffect { HomeUiEffect.GoMINIDetail }
            is HomeUiAction.ShowUpdate -> changeDownLoadDialogState()
            is HomeUiAction.Update -> {
                setState { copy(isDownloading = true) }
                downloadApp(Constants.PGYER_KEY, "")
            }

            is HomeUiAction.UpdateDownloadProgress -> {
                setState { copy(updateProcess = action.progress) }
            }

            is HomeUiAction.FetchData -> fetchData()
            is HomeUiAction.Download -> downloadApp(action.appKey, action.password)
        }
    }

    override fun updateLoading() {
    }

    /**
     * 获取最新数据
     */
    private fun fetchData() {
        getAppGroup()
    }

    private fun getAppGroup() {
        setState { copy(isRefreshing = true) }
        request(
            api = { ApiService.instance().appGroup(Constants.API_KEY, Constants.GROUP_KEY) },
            onFailed = { setState { copy(isRefreshing = false) } },
            onSuccess = {
                setState { copy(items = it.apps ?: emptyList(), isRefreshing = false) }
            }
        )
    }


    /**
     * 检查更新
     */
    private fun checkUpdate() {
        request(
            api = { ApiService.instance().check(Constants.API_KEY, Constants.PGYER_KEY) },
            onSuccess = {
                setState {
                    copy(
                        hasUpdate = (it.buildVersionNo?.toInt() ?: 0) > BuildConfig.VERSION_CODE,
                        isForceUpdate = it.buildUpdateDescription?.split("===")[1]?.contains("isForceUpdate=true")
                            ?: false,
                        updateContent = it.buildUpdateDescription?.split("===")[0] ?: ""
                    )
                }
            }
        )
    }


    private fun downloadApp(appKey: String, password: String) {
        val url = downloadUrl(appKey, password)
        sendEffect { HomeUiEffect.DownLoadApp(url) }
    }


    private fun changeDownLoadDialogState() {
        setState { copy(hasUpdate = !hasUpdate) }
    }


}