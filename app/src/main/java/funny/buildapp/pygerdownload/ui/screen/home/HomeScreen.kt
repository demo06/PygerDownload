package funny.buildapp.pygerdownload.ui.screen.home

import android.app.DownloadManager
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
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
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import funny.buildapp.clauncher.util.click
import funny.buildapp.clauncher.util.loge
import funny.buildapp.clauncher.util.toast
import funny.buildapp.pygerdownload.R
import funny.buildapp.pygerdownload.model.MiniInfo
import funny.buildapp.pygerdownload.route.AppRoute
import funny.buildapp.pygerdownload.route.LocalNavigator
import funny.buildapp.pygerdownload.ui.component.DownLoadButton
import funny.buildapp.pygerdownload.ui.component.DownloadState
import funny.buildapp.pygerdownload.ui.component.Screen
import funny.buildapp.pygerdownload.ui.component.TitleBar
import funny.buildapp.pygerdownload.ui.component.UpgradeDialog
import funny.buildapp.pygerdownload.ui.theme.black333
import funny.buildapp.pygerdownload.ui.theme.gray999
import funny.buildapp.pygerdownload.ui.theme.green07C160
import funny.buildapp.pygerdownload.ui.theme.theme
import funny.buildapp.pygerdownload.ui.theme.white
import funny.buildapp.pygerdownload.ui.theme.whiteF4F5FA
import funny.buildapp.pygerdownload.util.ApkDownloadManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Preview
@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val listSate = rememberLazyListState()
    val dispatch = viewModel::dispatch
    UIEffect(viewModel, listSate, uiState, dispatch)
    Screen(
        modifier = Modifier.background(whiteF4F5FA),
        titleBar = {
            TitleBar(title = "中钢网应用集")
        },
        dialog = {
            UpgradeDialog(
                visible = uiState.hasUpdate,
                isForceUpdate = uiState.isForceUpdate,
                isDownloading = uiState.isDownloading,
                updateContent = uiState.updateContent,
                progress = uiState.updateProcess,
                onDismiss = { dispatch(HomeUiAction.ShowUpdate) },
                onConfirm = {
                    dispatch(HomeUiAction.ShowUpdate)
                    dispatch(HomeUiAction.Update)
                })
        }
    ) {
        Content(
            listSate = listSate,
            miniList = uiState.miniItems ?: emptyList(),
            isRefreshing = uiState.isRefreshing,
            dispatch = viewModel::dispatch,
            header = {
                val items = uiState.items.orEmpty()
                if (items.size >= 3) {
                    Header(topAppCard = { index ->
                        val item = items[index]
                        val appKey = item.appKey ?: ""
                        val downloadInfo = uiState.appDownloadStates[appKey]
                        AppTopCard(
                            modifier = Modifier.weight(1f),
                            index = index,
                            appName = item.buildName ?: "",
                            versionName = item.buildVersion ?: "",
                            createdTime = item.getTime(),
                            iconUrl = item.buildIcon ?: "",
                            downloadState = downloadInfo?.state ?: DownloadState.IDLE,
                            downloadProgress = downloadInfo?.progress ?: 0,
                            onDownloadClick = {
                                dispatch(HomeUiAction.StartAppDownload(appKey, item.buildPassword ?: ""))
                            },
                            onInstallClick = {
                                dispatch(HomeUiAction.InstallApp(appKey))
                            },
                            onItemClick = {
                                dispatch(HomeUiAction.GoDetail(item))
                            })
                    })
                }
            },
            item = { item ->
                Item(
                    appName = item.appName,
                    iconUrl = item.appIcon,
                    description = item.appDescription,
                    onItemClick = {
                        dispatch(HomeUiAction.GoMINIDetail(item))
                    },
                )
            },
        )

    }
}

@Composable
private fun UIEffect(
    viewModel: HomeViewModel,
    listSate: LazyListState,
    uiState: HomeUiState,
    dispatch: (HomeUiAction) -> Unit
) {
    val context = LocalContext.current
    val navigator = LocalNavigator.current
    val appDownloader = remember { ApkDownloadManager(context) }
    val coroutineScope = rememberCoroutineScope()


    LaunchedEffect(Unit) {
        dispatch(HomeUiAction.FetchData)
    }
    LaunchedEffect(uiState) {
        listSate.animateScrollToItem(0)
    }
    LaunchedEffect(viewModel) {
        viewModel.effect.collect {
            when (it) {
                is HomeUiEffect.ShowToast -> {
                    it.msg.toast(context)
                }

                is HomeUiEffect.GoMINIDetail -> {
                    navigator.push(AppRoute.MiniDetail(it.item))
                }

                is HomeUiEffect.GoDetail -> {
                    navigator.push(AppRoute.Detail(it.item))
                }

                is HomeUiEffect.DownLoadApp -> {
                    val downloadId = appDownloader.download(it.url, "app-release.apk")
                    downloadId.loge()
                    coroutineScope.launch {
                        var isDownloading = true
                        while (isDownloading) {
                            val (progress, status) = appDownloader.queryProgress(downloadId)
                            dispatch(HomeUiAction.UpdateDownloadProgress(progress))

                            // 检查下载状态
                            when (status) {
                                DownloadManager.STATUS_SUCCESSFUL -> {
                                    // 下载完成
                                    dispatch(HomeUiAction.ShowUpdate)
                                    dispatch(HomeUiAction.UpdateDownloadProgress(100))
                                    val downloadUrl = appDownloader.getDownloadedUri(downloadId)
                                    appDownloader.installApk(context, downloadUrl)
                                    isDownloading = false
                                }

                                DownloadManager.STATUS_FAILED -> {
                                    // 下载失败
                                    isDownloading = false
                                }

                                -1 -> {
                                    // 查询失败，可能下载已被取消
                                    isDownloading = false
                                }

                                else -> {
                                    // 继续查询
                                    delay(500) // 每 0.5s 更新一次
                                }
                            }
                        }
                    }

                }

                // 新增：处理单个应用下载
                is HomeUiEffect.StartDownloadApp -> {
                    val appKey = it.appKey
                    val downloadId = appDownloader.download(it.url, "${appKey}.apk")
                    downloadId.loge()
                    coroutineScope.launch {
                        var isDownloading = true
                        while (isDownloading) {
                            val (progress, status) = appDownloader.queryProgress(downloadId)
                            dispatch(HomeUiAction.UpdateAppDownloadProgress(appKey, progress))

                            when (status) {
                                DownloadManager.STATUS_SUCCESSFUL -> {
                                    val downloadUri = appDownloader.getDownloadedUri(downloadId)
                                    dispatch(HomeUiAction.AppDownloadCompleted(appKey, downloadUri))
                                    appDownloader.installApk(context, downloadUri) // 自动安装
                                    isDownloading = false
                                }

                                DownloadManager.STATUS_FAILED -> {
                                    dispatch(HomeUiAction.AppDownloadFailed(appKey))
                                    isDownloading = false
                                }

                                -1 -> {
                                    dispatch(HomeUiAction.AppDownloadFailed(appKey))
                                    isDownloading = false
                                }

                                else -> {
                                    delay(500)
                                }
                            }
                        }
                    }
                }

                // 新增：安装应用
                is HomeUiEffect.InstallApp -> {
                    appDownloader.installApk(context, it.downloadUri)
                }

                // 新增：清除已下载的 APK
                is HomeUiEffect.ClearDownloadedApks -> {
                    val count = appDownloader.clearAllDownloadedApks()
                    "已清除 $count 个已下载的安装包".loge()
                }
            }
        }
    }
}

@Composable
private fun Content(
    miniList: List<MiniInfo> = emptyList(),
    listSate: LazyListState = rememberLazyListState(),
    isRefreshing: Boolean,
    header: @Composable () -> Unit,
    item: @Composable (MiniInfo) -> Unit = {},
    dispatch: (HomeUiAction) -> Unit
) {
    val state = rememberPullToRefreshState()
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        state = state,
        onRefresh = {
            dispatch(HomeUiAction.FetchData)
        },
        indicator = {
            Indicator(
                modifier = Modifier.align(Alignment.TopCenter),
                isRefreshing = isRefreshing,
                containerColor = white,
                color = theme,
                state = state
            )
        },
    ) {
        LazyColumn(
            Modifier.fillMaxSize(),
            state = listSate
        ) {
            item {
                header()
            }
            items(miniList) {
                item(it)
            }

        }

    }
}

@Composable
private fun Header(topAppCard: @Composable RowScope.(Int) -> Unit = {}) {
    val displayOrder = listOf(1, 0, 2)
    Row(
        Modifier
            .fillMaxWidth()
            .padding(top = 20.dp, bottom = 12.dp, start = 8.dp, end = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        displayOrder.forEach {
            topAppCard(it)
        }
    }
}


@Composable
private fun AppTopCard(
    index: Int = 0,
    modifier: Modifier = Modifier,
    appName: String = "中钢网",
    versionName: String = "v3.7.1",
    createdTime: String = "3小时前",
    iconUrl: String = "",
    downloadState: DownloadState = DownloadState.IDLE,
    downloadProgress: Int = 0,
    onDownloadClick: () -> Unit = {},
    onInstallClick: () -> Unit = {},
    onItemClick: () -> Unit = {}
) {
    val imgUrl =
        "https://cdn-app-icon2.pgyer.com/${iconUrl[0]}/${iconUrl[1]}/${iconUrl[2]}/${iconUrl[3]}/${iconUrl[4]}/$iconUrl?x-oss-process=image/resize,m_lfit,h_120,w_120/format,jpg"
    Column(
        modifier = modifier
            .offset(y = if (index == 0) (-10).dp else (5).dp)
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(8.dp)
            .click(onItemClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AsyncImage(
            model = imgUrl,
            contentDescription = "icon",
            placeholder = painterResource(R.mipmap.zgw),
            error = painterResource(R.mipmap.zgw),
            modifier = Modifier
                .padding(8.dp)
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        Text(
            appName,
            color = black333,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Text("v${versionName}", color = gray999, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(6.dp))
        Text(createdTime, color = gray999, fontSize = 14.sp)
        DownLoadButton(
            state = downloadState,
            progress = downloadProgress,
            onDownloadClick = onDownloadClick,
            onInstallClick = onInstallClick
        )
    }
}

@Composable
private fun Item(
    appName: String = "抢钢宝",
    @DrawableRes iconUrl: Int,
    description: String = "简介",
    onItemClick: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .padding(bottom = 6.dp, start = 8.dp, end = 8.dp)
            .fillMaxWidth()
            .background(white, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .click { onItemClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            painter = painterResource(iconUrl),
            contentDescription = "icon",
            modifier = Modifier
                .padding(8.dp)
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        Column(
            Modifier
                .padding(end = 16.dp)
                .weight(1f)
                .padding(start = 8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    appName,
                    color = black333,
                    fontSize = 16.sp,
                )
                Spacer(Modifier.width(8.dp))
                Tag("小程序", green07C160)
            }
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(description, color = Color(0xFF5A5858), fontSize = 14.sp)
            }


        }

        Text(
            "查看",
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .padding(top = 8.dp)
                .background(theme, RoundedCornerShape(30.dp))
                .padding(horizontal = 18.dp, vertical = 4.dp),
            color = Color.White,
            fontSize = 14.sp
        )

    }


}


@Composable
fun Tag(text: String = "APP", background: Color = theme) {
    Box(
        modifier = Modifier
            .background(background, RoundedCornerShape(4.dp))
            .height(20.dp) // 1. 强制设定一个高度 (根据你的 12.sp 字体，20-22dp 比较合适)
            .padding(horizontal = 4.dp), // 2. 只保留水平 padding，垂直方向靠居中
        contentAlignment = Alignment.Center // 3. 让文字绝对居中
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            color = white,
            // 4. (可选) 移除文字默认的上下留白，让居中更精确
            style = TextStyle(
                platformStyle = PlatformTextStyle(
                    includeFontPadding = false
                )
            )
        )
    }
}
