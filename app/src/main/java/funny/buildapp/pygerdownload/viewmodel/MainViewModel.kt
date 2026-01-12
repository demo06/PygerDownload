package funny.buildapp.pygerdownload.viewmodel

import android.Manifest
import android.R.attr.apiKey
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
import funny.buildapp.pygerdownload.ui.screen.home.HomeUiAction
import funny.buildapp.pygerdownload.ui.screen.home.HomeUiEffect
import funny.buildapp.pygerdownload.ui.screen.home.HomeUiState
import funny.buildapp.pygerdownload.util.BaseMviViewModel
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
class MainViewModel : BaseMviViewModel<HomeUiState, HomeUiAction, HomeUiEffect>(HomeUiState()) {


    override fun handleAction(action: HomeUiAction) {
        when (action) {
            is HomeUiAction.GoDetail -> sendEffect { HomeUiEffect.GoDetail }
            is HomeUiAction.ShowUpdate -> changeDownLoadDialogState()
            is HomeUiAction.Update -> {}
            is HomeUiAction.FetchData -> fetchData()
            is HomeUiAction.Download -> downloadApp(action.appKey, action.password)
        }
    }

    override fun updateLoading() {
    }


    private fun fetchData() {
        setState { copy(isRefreshing = true) }
        request(
            api = { ApiService.instance().appGroup(_uiState.value.apiKey, _uiState.value.groupKey) },
            onFailed = { setState { copy(isRefreshing = false) } },
            onSuccess = {
                setState { copy(items = it.apps ?: emptyList(), isRefreshing = false) }
            }
        )
    }


    private fun downloadApp(appKey: String, password: String) {
        val url =
            "${BASE_URL}apiv2/app/install?_api_key=${apiKey}&appKey=${appKey}&buildPassword=${password}"
    }


    private fun changeDownLoadDialogState() {
        setState { copy(hasUpdate = !hasUpdate) }
    }

    fun gotoBrowserDownload(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        context.startActivity(intent)
    }

    fun gotoDownloadManager(context: Context, url: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
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


}

