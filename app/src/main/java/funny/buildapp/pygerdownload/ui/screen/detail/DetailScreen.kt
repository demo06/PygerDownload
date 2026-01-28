package funny.buildapp.pygerdownload.ui.screen.detail

import android.app.DownloadManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import funny.buildapp.clauncher.util.click
import funny.buildapp.clauncher.util.toast
import funny.buildapp.pygerdownload.R
import funny.buildapp.pygerdownload.model.AppInfo
import funny.buildapp.pygerdownload.model.Version
import funny.buildapp.pygerdownload.route.LocalNavigator
import funny.buildapp.pygerdownload.ui.component.DownLoadButton
import funny.buildapp.pygerdownload.ui.component.DownloadState
import funny.buildapp.pygerdownload.ui.component.Screen
import funny.buildapp.pygerdownload.ui.component.TitleBar
import funny.buildapp.pygerdownload.ui.screen.home.Tag
import funny.buildapp.pygerdownload.ui.theme.black333
import funny.buildapp.pygerdownload.ui.theme.gray999
import funny.buildapp.pygerdownload.ui.theme.green07C160
import funny.buildapp.pygerdownload.ui.theme.orangeFF7400
import funny.buildapp.pygerdownload.ui.theme.theme
import funny.buildapp.pygerdownload.ui.theme.white
import funny.buildapp.pygerdownload.ui.theme.whiteF4F5FA
import funny.buildapp.pygerdownload.util.ApkDownloadManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Preview
@Composable
fun DetailScreen(item: AppInfo = AppInfo(), viewModel: DetailViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val dispatch = viewModel::dispatch
    UiEffect(viewModel, item, dispatch)
    Screen(
        isLoading = uiState.isLoading,
        modifier = Modifier.background(whiteF4F5FA),
        titleBar = {
            TitleBar(
                title = "应用详情",
                onBack = { dispatch(DetailUiAction.GoBack) })
        }
    ) {
        Content(
            dispatch = dispatch,
            appInfo = {
                AppInfoCard(
                    appName = uiState.appInfo.buildName ?: "",
                    versionName = uiState.appInfo.buildVersion ?: "v1.0.0",
                    versionCode = uiState.appInfo.buildBuildVersion ?: "1",
                    buildFileSize = uiState.appInfo.buildFileSize?.toInt() ?: 1023213123,
                    buildCreated = uiState.appInfo.getTime(),
                    packageName = uiState.appInfo.buildIdentifier ?: "",
                    isPreview = uiState.appInfo.buildUpdateDescription?.contains("测试包") == true,
                    iconUrl = uiState.appInfo.buildIcon ?: "112312",
                    downloadState = uiState.downloadState,
                    downloadProgress = uiState.downloadProgress,
                    onDownloadClick = { dispatch(DetailUiAction.Download) },
                    onInstallClick = { dispatch(DetailUiAction.Install) }
                )
            },
            versionHistory = {
                VersionHistoryCard(
                    versionHistory = uiState.versionHistory?.list.orEmpty(),
                    versionDownloadStates = uiState.versionDownloadStates,
                    versionDownloadProgress = uiState.versionDownloadProgress,
                    hasMore = uiState.hasMore,
                    onLoadMore = { dispatch(DetailUiAction.LoadMore) },
                    onDownloadClick = { index ->
                        val version = uiState.versionHistory?.list?.getOrNull(index)
                        if (version != null) {
                            dispatch(
                                DetailUiAction.StartVersionDownload(
                                    index,
                                    version.buildKey ?: "",
                                    "" // Version doesn't have buildPassword
                                )
                            )
                        }
                    },
                    onInstallClick = { index ->
                        dispatch(DetailUiAction.InstallVersion(index))
                    }
                )
            }
        )
    }
}

@Composable
private fun UiEffect(viewModel: DetailViewModel, item: AppInfo, dispatch: (DetailUiAction) -> Unit) {
    val context = LocalContext.current
    val navigator = LocalNavigator.current
    val appDownloader = remember { ApkDownloadManager(context) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        dispatch(DetailUiAction.FetchData(item))
    }

    LaunchedEffect(viewModel) {
        viewModel.effect.collect {
            when (it) {
                is DetailUiEffect.GoBack -> {
                    navigator.pop()
                }

                is DetailUiEffect.ShowToast -> {
                    it.msg.toast(context)
                }

                is DetailUiEffect.StartDownload -> {
                    val downloadId = appDownloader.download(it.url, "${it.appKey}.apk")
                    coroutineScope.launch {
                        var isDownloading = true
                        while (isDownloading) {
                            val (progress, status) = appDownloader.queryProgress(downloadId)
                            dispatch(DetailUiAction.UpdateDownloadProgress(progress))

                            when (status) {
                                DownloadManager.STATUS_SUCCESSFUL -> {
                                    val downloadUri = appDownloader.getDownloadedUri(downloadId)
                                    dispatch(DetailUiAction.DownloadCompleted(downloadUri))
                                    appDownloader.installApk(context, downloadUri) // 自动安装
                                    isDownloading = false
                                }

                                DownloadManager.STATUS_FAILED, -1 -> {
                                    dispatch(DetailUiAction.DownloadFailed)
                                    isDownloading = false
                                }

                                else -> {
                                    delay(500)
                                }
                            }
                        }
                    }
                }

                is DetailUiEffect.Install -> {
                    appDownloader.installApk(context, it.downloadUri)
                }

                is DetailUiEffect.StartVersionDownload -> {
                    val index = it.index
                    val downloadId = appDownloader.download(it.url, "${it.appKey}_v${index}.apk")
                    coroutineScope.launch {
                        var isDownloading = true
                        while (isDownloading) {
                            val (progress, status) = appDownloader.queryProgress(downloadId)
                            dispatch(DetailUiAction.UpdateVersionDownloadProgress(index, progress))

                            when (status) {
                                DownloadManager.STATUS_SUCCESSFUL -> {
                                    val downloadUri = appDownloader.getDownloadedUri(downloadId)
                                    dispatch(DetailUiAction.VersionDownloadCompleted(index, downloadUri))
                                    appDownloader.installApk(context, downloadUri) // 自动安装
                                    isDownloading = false
                                }

                                DownloadManager.STATUS_FAILED, -1 -> {
                                    dispatch(DetailUiAction.VersionDownloadFailed(index))
                                    isDownloading = false
                                }

                                else -> {
                                    delay(500)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Content(
    dispatch: (DetailUiAction) -> Unit,
    appInfo: @Composable () -> Unit,
    versionHistory: @Composable () -> Unit
) {
    LazyColumn(Modifier.fillMaxSize()) {
        item {
            appInfo()
        }
        item {
            versionHistory()
        }
    }
}


@Composable
private fun AppInfoCard(
    appName: String = "抢钢宝",
    versionName: String = "v1.1.1",
    versionCode: String = "20",
    buildFileSize: Int = 1023213123,
    packageName: String = "funny.buildapp.pgyerdownloader",
    buildCreated: String = "3分钟前",
    isPreview: Boolean = false,
    iconUrl: String = "12312",
    downloadState: DownloadState = DownloadState.IDLE,
    downloadProgress: Int = 0,
    onDownloadClick: () -> Unit = {},
    onInstallClick: () -> Unit = {}
) {
    val imgUrl = if (iconUrl.isEmpty()) {
        "https://cdn-app-icon2.pgyer.com/8/3/b/4/4/83b445401ad8ba82180b599f71fc8109?x-oss-process=image/resize,m_lfit,h_120,w_120/format,jpg"
    } else {
        "https://cdn-app-icon2.pgyer.com/${iconUrl[0]}/${iconUrl[1]}/${iconUrl[2]}/${iconUrl[3]}/${iconUrl[4]}/$iconUrl?x-oss-process=image/resize,m_lfit,h_120,w_120/format,jpg"
    }
    Column(
        modifier = Modifier
            .padding(top = 8.dp, start = 8.dp, end = 8.dp)
            .fillMaxWidth()
            .background(white, RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = imgUrl,
                    contentDescription = "icon",
                    placeholder = painterResource(R.mipmap.qgb),
                    error = painterResource(R.mipmap.qgb),
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        appName,
                        color = black333,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Tag()
                        Spacer(Modifier.width(8.dp))
                        if (isPreview) {
                            Tag("测试线", orangeFF7400)
                        } else {
                            Tag("正式线", green07C160)
                        }
                    }
                }
            }
            DownLoadButton(
                state = downloadState,
                progress = downloadProgress,
                onDownloadClick = onDownloadClick,
                onInstallClick = onInstallClick
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Column {
            InfoRow("包名", packageName)
            InfoRow("版本", "v${versionName}")
            InfoRow("版本号(Build)", versionCode)
            InfoRow("大小", "${buildFileSize / 1024 / 1024}M")
            InfoRow("更新时间", buildCreated)
        }
    }
}

@Composable
private fun VersionHistoryCard(
    versionHistory: List<Version> = emptyList(),
    versionDownloadStates: Map<Int, DownloadState> = emptyMap(),
    versionDownloadProgress: Map<Int, Int> = emptyMap(),
    hasMore: Boolean = false,
    onLoadMore: () -> Unit = {},
    onDownloadClick: (Int) -> Unit = {},
    onInstallClick: (Int) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .background(white, RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Text(
            "版本历史",
            color = black333,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        versionHistory.forEachIndexed { index, version ->
            VersionItem(
                versionName = version.buildVersion ?: "v1.0.0",
                versionCode = version.buildBuildVersion ?: "",
                buildCreated = version.getTime(),
                buildFileSize = version.buildFileSize?.toInt() ?: 96022313,
                isLatest = index == 0,
                isPreview = version.buildUpdateDescription?.contains("测试包") == true,
                downloadState = versionDownloadStates[index] ?: DownloadState.IDLE,
                downloadProgress = versionDownloadProgress[index] ?: 0,
                onDownloadClick = { onDownloadClick(index) },
                onInstallClick = { onInstallClick(index) }
            )
            if (index < versionHistory.size - 1) {
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        if (hasMore) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .click { onLoadMore() }
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("点击加载更多", color = theme, fontSize = 14.sp)
            }
        } else if (versionHistory.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("没有更多了", color = gray999, fontSize = 12.sp)
            }
        }
    }
}

@Preview
@Composable
private fun VersionItem(
    versionName: String = "v1.1.0",
    versionCode: String = "19",
    buildCreated: String = "2天前",
    buildFileSize: Int = 1020213123,
    isLatest: Boolean = false,
    isPreview: Boolean = false,
    downloadState: DownloadState = DownloadState.IDLE,
    downloadProgress: Int = 0,
    onDownloadClick: () -> Unit = {},
    onInstallClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isLatest) Color(0xFFF0F9FF) else Color.Transparent, RoundedCornerShape(8.dp))
            .padding(start = 12.dp, top = 12.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "v$versionName",
                    color = black333,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                if (isLatest) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Tag("最新", green07C160)
                } else {
                    Spacer(modifier = Modifier.width(8.dp))
                    if (isPreview) {
                        Tag("测试版", orangeFF7400)
                    } else {
                        Tag("正式版", green07C160)
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "版本号: $versionCode | 大小: ${buildFileSize / 1024 / 1024}M | $buildCreated",
                color = gray999,
                fontSize = 14.sp
            )
        }

        if (!isLatest) {
            DownLoadButton(
                state = downloadState,
                progress = downloadProgress,
                onDownloadClick = onDownloadClick,
                onInstallClick = onInstallClick
            )
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            color = gray999,
            fontSize = 16.sp
        )
        Text(
            value,
            color = black333,
            fontSize = 16.sp
        )
    }
}

