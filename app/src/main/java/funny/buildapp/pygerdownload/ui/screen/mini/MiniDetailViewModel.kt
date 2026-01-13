package funny.buildapp.pygerdownload.ui.screen.mini

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

class MiniDetailViewModel : BaseMviViewModel<MiniDetailUiState, MiniDetailUiAction, MiniDetailUiEffect>(MiniDetailUiState()) {

    override fun handleAction(action: MiniDetailUiAction) {
        when (action) {
            is MiniDetailUiAction.GoBack -> sendEffect { MiniDetailUiEffect.GoBack }
            is MiniDetailUiAction.Download -> downloadMainApp()
            is MiniDetailUiAction.FetchData -> fetchData()
            is MiniDetailUiAction.DownloadMiniApp -> downloadMiniApp(action.appKey, action.password, action.appName)
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
                appName = "环球钢材",
                appIcon = "",
                miniApps = listOf(
                    MiniAppInfo(
                        name = "钢市通",
                        description = "钢材市场行情分析工具",
                        qrCodeUrl = "https://example.com/qr1",
                        appKey = "steel_market_key",
                        password = "steel_market_pass",
                        versionName = "v1.2.0",
                        versionCode = "120"
                    ),
                    MiniAppInfo(
                        name = "钢价计算",
                        description = "钢材价格计算器",
                        qrCodeUrl = "https://example.com/qr2",
                        appKey = "steel_calc_key",
                        password = "steel_calc_pass",
                        versionName = "v1.1.5",
                        versionCode = "115"
                    ),
                    MiniAppInfo(
                        name = "库存管理",
                        description = "钢材库存管理系统",
                        qrCodeUrl = "https://example.com/qr3",
                        appKey = "inventory_key",
                        password = "inventory_pass",
                        versionName = "v2.0.1",
                        versionCode = "201"
                    ),
                    MiniAppInfo(
                        name = "订单跟踪",
                        description = "钢材订单状态跟踪",
                        qrCodeUrl = "https://example.com/qr4",
                        appKey = "order_track_key",
                        password = "order_track_pass",
                        versionName = "v1.0.8",
                        versionCode = "108"
                    )
                ),
                isLoading = false
            )
        }
    }

    private fun downloadMainApp() {
        // 处理主应用下载逻辑
        sendEffect { MiniDetailUiEffect.ShowToast("主应用下载功能待实现") }
    }

    private fun downloadMiniApp(appKey: String, password: String, appName: String) {
        val url = "${NetWork.BASE_URL}apiv2/app/install?_api_key=${R.attr.apiKey}&appKey=$appKey&buildPassword=$password"
        sendEffect { MiniDetailUiEffect.ShowToast("开始下载 $appName") }
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