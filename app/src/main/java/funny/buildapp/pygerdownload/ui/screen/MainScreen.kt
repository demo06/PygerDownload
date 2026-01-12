package funny.buildapp.pygerdownload.ui.screen

import android.R.attr.type
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Applier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import funny.buildapp.clauncher.util.click
import funny.buildapp.pygerdownload.R
import funny.buildapp.pygerdownload.ui.component.CommonDialog
import funny.buildapp.pygerdownload.ui.component.UpgradeDialog
import funny.buildapp.pygerdownload.ui.theme.black333
import funny.buildapp.pygerdownload.ui.theme.gray999
import funny.buildapp.pygerdownload.ui.theme.green07C160
import funny.buildapp.pygerdownload.ui.theme.theme
import funny.buildapp.pygerdownload.ui.theme.white
import funny.buildapp.pygerdownload.ui.theme.whiteF4F5FA
import funny.buildapp.pygerdownload.util.PermissionUtils
import funny.buildapp.pygerdownload.viewmodel.MainViewModel
import funny.buildapp.pygerdownload.viewmodel.ViewAction

@Preview
@Composable
fun AppHome(viewModel: MainViewModel = viewModel()) {
//    val uiState by viewModel.
    val context = LocalContext.current
    val dispatch = viewModel::dispatch

    Screen(
        modifier = Modifier.background(whiteF4F5FA),
        titleBar = {
            TitleBar(title = "蒲公英商店")
        },
        dialog = {
            UpgradeDialog(visible = true,isForceUpdate = false, isDownloading = false)
        }
    ) {
        Content(
            isRefreshing = false,
            dispatch = viewModel::dispatch,
            header = { Header() },
            item = { Item() },
        )

    }


//        RefreshLayout(viewModel) {
//            PermissionUtils.goSettings(context)
//        }
}


@Composable
private fun Content(
    isRefreshing: Boolean,
    header: @Composable () -> Unit,
    item: @Composable () -> Unit = {},
    dispatch: (ViewAction) -> Unit
) {
    val state = rememberPullToRefreshState()
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        state = state,
        onRefresh = {
            dispatch(ViewAction.Refreshing)
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
        LazyColumn(Modifier.fillMaxSize()) {
            item {
                header()
            }
            items(10) {
                item()
            }

        }

    }
}

@Composable
private fun Header(modifier: Modifier = Modifier) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(top = 20.dp, bottom = 12.dp, start = 8.dp, end = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AppTopCard(
            modifier
                .weight(1f)
                .offset(y = (5).dp)
        )
        AppTopCard(
            modifier
                .weight(1f)
                .offset(y = (-10).dp)
        )
        AppTopCard(
            modifier
                .weight(1f)
                .offset(y = (5).dp)
        )
    }
}

@Composable
fun RefreshLayout(viewModel: MainViewModel, goSetting: () -> Unit) {
    val context = LocalContext.current
    val viewState = viewModel.viewStates
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val state = rememberPullToRefreshState()
    val hasPermission = remember { PermissionUtils.haveInstallPermission(context = context) }
    var showTipState by remember { mutableStateOf(hasPermission) }

    LaunchedEffect(Unit) {
        viewModel.dispatch(ViewAction.Refreshing)
        viewModel.downloadUrl.collect {
            if (it.isNotEmpty()) {
                if (hasPermission) {
                    viewModel.gotoDownloadManager(context, it)
                } else {
                    viewModel.gotoBrowserDownload(context, it)
                }
                viewModel.resetDownloadUrl()
            }
        }
    }
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        state = state,
        onRefresh = {
            viewModel.dispatch(ViewAction.Refreshing)
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
        Box {
            if (!showTipState) {
                CommonDialog(onDismiss = { showTipState = true }) {
                    showTipState = true
                    goSetting()
                }
            }
            Column(
                Modifier
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState())
            ) {
                AppInfoCard(
                    modifier = Modifier.weight(1f),
                    id = R.mipmap.zgw,
                    appName = "中钢网",
                    versionName = viewState.zgwAppInfo?.buildVersion ?: "0",
                    versionCode = viewState.zgwAppInfo?.buildVersionNo ?: "0",
                    buildFileSize = viewState.zgwAppInfo?.buildFileSize?.toInt() ?: 0,
                    buildCreated = viewState.zgwAppInfo?.getTime() ?: "0",
                    position = 0
                ) {
                    viewModel.dispatch(
                        ViewAction.Download(
                            viewState.zgwAppInfo?.appKey ?: "",
                            viewState.zgwAppInfo?.buildPassword ?: ""
                        )
                    )
                }
                Row(modifier = Modifier.weight(1f)) {
                    AppInfoCard(
                        modifier = Modifier.weight(1f),
                        id = R.mipmap.qgb,
                        appName = "抢钢宝",
                        versionName = viewState.qgbAppInfo?.buildVersion ?: "0",
                        versionCode = viewState.qgbAppInfo?.buildVersionNo ?: "0",
                        buildFileSize = viewState.qgbAppInfo?.buildFileSize?.toInt() ?: 0,
                        buildCreated = viewState.qgbAppInfo?.getTime() ?: "0",
                        position = 1
                    ) {
                        viewModel.dispatch(
                            ViewAction.Download(
                                viewState.qgbAppInfo?.appKey ?: "",
                                viewState.qgbAppInfo?.buildPassword ?: ""
                            )
                        )

                    }
                    AppInfoCard(
                        modifier = Modifier.weight(1f),
                        id = R.mipmap.wlb,
                        appName = "物流宝",
                        versionName = viewState.wlbAppInfo?.buildVersion ?: "0",
                        versionCode = viewState.wlbAppInfo?.buildVersionNo ?: "0",
                        buildFileSize = viewState.wlbAppInfo?.buildFileSize?.toInt() ?: 0,
                        buildCreated = viewState.wlbAppInfo?.getTime() ?: "0",
                        position = 2
                    ) {
                        viewModel.dispatch(
                            ViewAction.Download(
                                viewState.wlbAppInfo?.appKey ?: "",
                                viewState.wlbAppInfo?.buildPassword ?: ""
                            )
                        )
                    }
                }
            }

        }
    }

}


@Composable
fun TitleBar(title: String) {
    Box(
        modifier = Modifier
            .background(theme)
            .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
            .fillMaxWidth()
            .height(49.dp)
            .background(theme)

    ) {
        Text(
            text = title, fontSize = 18.sp, color = Color.White, modifier = Modifier.align(
                Alignment.Center
            )
        )
    }
}

@Composable
fun AppInfoCard(
    modifier: Modifier = Modifier,
    id: Int = 0,
    appName: String = "中钢网",
    versionName: String = "v3.7.1",
    versionCode: String = "172",
    buildCreated: String = "3天前",
    buildFileSize: Int = 84,
    position: Int = 0,
    onClick: () -> Unit = {}
) {
    val pos by remember { mutableIntStateOf(position) }   //0-上  1-左 2-右
    val leftDp: Dp = when (pos) {
        0, 1 -> 8.dp
        2 -> 4.dp
        else -> 8.dp
    }
    val rightDp: Dp = when (pos) {
        0, 2 -> 8.dp
        1 -> 4.dp
        else -> 8.dp
    }
    val topDp: Dp = when (pos) {
        0 -> 8.dp
        1, 2 -> 4.dp
        else -> 8.dp
    }
    val bottomDp: Dp = when (pos) {
        0 -> 4.dp
        1, 2 -> 8.dp
        else -> 8.dp

    }
    Column(
        modifier
            .padding(start = leftDp, end = rightDp, top = topDp, bottom = bottomDp)
            .background(Color.White, RoundedCornerShape(8.dp))
            .fillMaxWidth()
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = id),
            contentDescription = "icon",
            modifier = Modifier
                .padding(8.dp)
                .size(60.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        Text(
            appName,
            color = Color.Black,
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 10.dp)
        )
        Text(
            "版本信息:",
            color = Color(0xFFA1A0A0),
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
        )
        Row {
            Text(versionName, color = Color(0xFF5A5858), fontSize = 14.sp)
            Text(
                " | ",
                color = Color(0xFFA1A0A0),
                fontSize = 14.sp,
            )
            Spacer(modifier = Modifier.background(Color(0xFFA1A0A0)))
            Text(versionCode, color = Color(0xFF5A5858), fontSize = 14.sp)
            Text(
                " | ",
                color = Color(0xFFA1A0A0),
                fontSize = 14.sp,
            )
            Text("${buildFileSize / 1024 / 1024}M", color = Color(0xFF5A5858), fontSize = 14.sp)
        }
        Text(
            "最后更新时间:",
            color = Color(0xFFA1A0A0),
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
        )
        Text(buildCreated, color = Color(0xFF5A5858), fontSize = 14.sp)
        Button(
            onClick = { onClick() },
            modifier = Modifier.padding(top = 10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = theme)
        ) {
            Text(
                "安装", modifier = Modifier.padding(start = 16.dp, end = 16.dp), fontSize = 16.sp
            )
        }

    }
}

@Composable
private fun AppTopCard(
    modifier: Modifier = Modifier,
    appName: String = "中钢网",
    versionName: String = "v3.7.1",
    iconUrl: String = "",
) {

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AsyncImage(
            model = iconUrl,
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
        Text(versionName, color = gray999, fontSize = 14.sp)


        Text(
            "下载",
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

@Preview
@Composable
private fun Item(
    appName: String = "抢钢宝",
    iconUrl: String = "",
    versionName: String = "v1.1.1",
    versionCode: String = "20",
    buildFileSize: Int = 1023213123,
    buildCreated: String = "3分钟前",
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
        AsyncImage(
            model = iconUrl,
            contentDescription = "icon",
            placeholder = painterResource(R.mipmap.qgb),
            error = painterResource(R.mipmap.qgb),
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
                Tag()
                Spacer(Modifier.width(4.dp))
                Tag("MINI", green07C160)
            }
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(versionName, color = Color(0xFF5A5858), fontSize = 14.sp)
                Text(
                    " | ",
                    color = Color(0xFFA1A0A0),
                    fontSize = 14.sp,
                )
                Text(versionCode, color = Color(0xFF5A5858), fontSize = 14.sp)
                Text(
                    " | ",
                    color = Color(0xFFA1A0A0),
                    fontSize = 14.sp,
                )
                Text("${buildFileSize / 1024 / 1024}M", color = Color(0xFF5A5858), fontSize = 14.sp)
            }


        }

        Text(
            "下载",
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
private fun Tag(text: String = "APP", background: Color = theme) {
    Text(
        modifier = Modifier
            .background(background, RoundedCornerShape(4.dp))
            .padding(horizontal = 4.dp, vertical = 2.dp),
        text = text,
        fontSize = 12.sp,
        color = white
    )

}
