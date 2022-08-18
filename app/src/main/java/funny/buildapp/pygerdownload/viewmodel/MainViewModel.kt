package funny.buildapp.pygerdownload.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Network
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import funny.buildapp.pygerdownload.model.AppInfo
import funny.buildapp.pygerdownload.net.ApiService
import funny.buildapp.pygerdownload.net.NetWork
import funny.buildapp.pygerdownload.net.NetWork.BASE_URL
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


/**
 * @author WenBin
 * @version 1.0
 * @description ViewModel for MainActivity
 * @email wenbin@buildapp.fun
 * @date 2022/6/16
 */
class MainViewModel : ViewModel() {


    private val api = ApiService.instance()
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean>
        get() = _isRefreshing.asStateFlow()
    private val _downloadUrl = MutableStateFlow("")
    val downloadUrl: StateFlow<String>
        get() = _downloadUrl.asStateFlow()
    var viewStates by mutableStateOf(MainViewState())
        private set


    companion object {
        const val apiKey = "955873f76198c4d20e6478e2a9103fc8"
        const val groupKey = "b86cbab03a5c5b24022dfdfc744cfef6"
    }

    fun dispatch(action: ViewAction) {
        when (action) {
            is ViewAction.Refreshing -> fetchData()
            is ViewAction.StopRefreshing -> stopRefresh()
            is ViewAction.FetchData -> fetchData()
            is ViewAction.Download -> downloadApp(action.appKey, action.password)
        }
    }


    private fun fetchData() {
        viewModelScope.launch {
            _isRefreshing.emit(true)
            flow {
                emit(api.appGroup(apiKey, groupKey))
            }.map { it ->
                if (it.code == 0) {
                    if (it.data != null) {
//                        HttpResult.Success(it.data)
                        val groupInfo = it.data
                        val zgw = groupInfo.apps?.first { info ->
                            info?.buildName == "中钢网"
                        }
                        val wlb = groupInfo.apps?.first { info ->
                            info?.buildName == "物流宝"
                        }
                        val qgb = groupInfo.apps?.first { info ->
                            info?.buildName == "抢钢宝"
                        }
                        viewStates = viewStates.copy(
                            zgwAppInfo = zgw, wlbAppInfo = wlb, qgbAppInfo = qgb
                        )
                    } else {
                        throw Exception("the result of remote's request is null")
                    }
                } else {
                    throw Exception(it.message)
                }
                stopRefresh()
            }.catch {
                stopRefresh()
            }.collect()
        }
    }


    private fun downloadApp(appkey: String, password: String) {
        val url =
            "${BASE_URL}apiv2/app/install?_api_key=${apiKey}&appKey=${appkey}&buildPassword=${password}"
        viewModelScope.launch {
            _downloadUrl.emit(url)
        }
    }

    private fun stopRefresh() {
        viewModelScope.launch {
            _isRefreshing.emit(false)
        }
    }

}


data class MainViewState(
    val zgwAppInfo: AppInfo? = null,
    val wlbAppInfo: AppInfo? = null,
    val qgbAppInfo: AppInfo? = null,
)


sealed class ViewAction {
    object Refreshing : ViewAction()
    object StopRefreshing : ViewAction()
    object FetchData : ViewAction()
    data class Download(val appKey: String, val password: String) : ViewAction()
}


