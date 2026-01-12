package funny.buildapp.pygerdownload.viewmodel

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Network
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.content.pm.PackageManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import funny.buildapp.clauncher.util.log
import funny.buildapp.clauncher.util.toast
import funny.buildapp.pygerdownload.model.AppInfo
import funny.buildapp.pygerdownload.net.ApiService
import funny.buildapp.pygerdownload.net.NetWork
import funny.buildapp.pygerdownload.net.NetWork.BASE_URL
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okhttp3.internal.notify


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

    fun resetDownloadUrl() {
        viewModelScope.launch {
            _downloadUrl.value = ""
        }
    }

    fun gotoBrowserDownload(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        context.startActivity(intent)
    }

    fun gotoDownloadManager(context: Context, url: String) {
        // Android 13+ requires POST_NOTIFICATIONS permission for showing download notifications
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) {
                "未授予通知权限，下载将无通知显示".toast(context)
            }
        }

        "开始下载".toast(context)
        url.log()

        val downloadManager =
            context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        val request = DownloadManager.Request(Uri.parse(url)).apply {
            setNotificationVisibility(
                DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
            )
            setTitle("应用下载中")
            setDescription("正在下载更新包")
            setAllowedOverMetered(true)
            setAllowedOverRoaming(true)
            setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                Uri.parse(url).lastPathSegment
            )
        }
        downloadManager.enqueue(request)
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
