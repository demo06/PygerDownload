package funny.buildapp.pygerdownload.ui.screen.detail

import android.R
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.core.content.ContextCompat
import funny.buildapp.clauncher.util.log
import funny.buildapp.clauncher.util.toast
import funny.buildapp.pygerdownload.net.NetWork
import funny.buildapp.pygerdownload.util.BaseMviViewModel

class DetailViewModel : BaseMviViewModel<DetailUiState, DetailUiAction, DetailUiEffect>(DetailUiState()) {

    override fun handleAction(action: DetailUiAction) {
        when (action) {
            is DetailUiAction.GoBack -> sendEffect { DetailUiEffect.GoBack }
            is DetailUiAction.Download -> downloadApp()
            is DetailUiAction.SelectVersion -> selectVersion(action.index)
            is DetailUiAction.FetchData -> fetchData()
            is DetailUiAction.DownloadVersion -> downloadVersion(action.appKey, action.password)
        }
    }

    override fun updateLoading() {
        setState { copy(isLoading = loadingCount > 0) }
    }

    private fun fetchData() {
        setState { copy(isLoading = true) }
        // 模拟数据获取
        setState {
            copy(
                appName = "抢钢宝",
                versionName = "v1.1.1",
                versionCode = "20",
                buildFileSize = 1023213123,
                buildCreated = "3分钟前",
                iconUrl = "",
                appKey = "example_app_key",
                buildPassword = "example_password",
                versionHistory = listOf(
                    VersionInfo("v1.1.1", "20", "3分钟前", 1023213123, "example_app_key", "example_password"),
                    VersionInfo("v1.1.0", "19", "2天前", 1020213123, "example_app_key_v2", "example_password_v2"),
                    VersionInfo("v1.0.9", "18", "1周前", 1019213123, "example_app_key_v3", "example_password_v3"),
                    VersionInfo("v1.0.8", "17", "2周前", 1018213123, "example_app_key_v4", "example_password_v4")
                ),
                isLoading = false
            )
        }
    }

    private fun downloadApp() {
        val appKey = _uiState.value.appKey
        val password = _uiState.value.buildPassword
        if (appKey != null && password != null) {
            downloadVersion(appKey, password)
        } else {
            sendEffect { DetailUiEffect.ShowToast("下载信息不完整") }
        }
    }

    private fun downloadVersion(appKey: String, password: String) {
        val url = "${NetWork.BASE_URL}apiv2/app/install?_api_key=${R.attr.apiKey}&appKey=$appKey&buildPassword=$password"
        // 这里可以添加实际的下载逻辑
        sendEffect { DetailUiEffect.ShowToast("开始下载") }
    }

    private fun selectVersion(index: Int) {
        val versionHistory = _uiState.value.versionHistory
        if (versionHistory != null && index in versionHistory.indices) {
            val selectedVersion = versionHistory[index]
            setState {
                copy(
                    versionName = selectedVersion.versionName,
                    versionCode = selectedVersion.versionCode,
                    buildFileSize = selectedVersion.buildFileSize,
                    buildCreated = selectedVersion.buildCreated,
                    appKey = selectedVersion.appKey,
                    buildPassword = selectedVersion.buildPassword
                )
            }
        }
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
}