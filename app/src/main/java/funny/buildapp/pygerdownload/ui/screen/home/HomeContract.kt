package funny.buildapp.pygerdownload.ui.screen.home

import funny.buildapp.pygerdownload.model.AppInfo

data class HomeUiState(
    val isRefreshing: Boolean = false,
    val apiKey: String = "955873f76198c4d20e6478e2a9103fc8",
    val groupKey: String = "b86cbab03a5c5b24022dfdfc744cfef6",
    val hasUpdate: Boolean = false,
    val items: List<AppInfo>? = emptyList()
)

sealed class HomeUiAction {
    object ShowUpdate : HomeUiAction()
    object GoDetail : HomeUiAction()
    object Update : HomeUiAction()
    object FetchData : HomeUiAction()
    data class Download(val appKey: String, val password: String) : HomeUiAction()

}


interface HomeUiEffect {
    data class ShowToast(val msg: String) : HomeUiEffect
    data object GoDetail : HomeUiEffect
}