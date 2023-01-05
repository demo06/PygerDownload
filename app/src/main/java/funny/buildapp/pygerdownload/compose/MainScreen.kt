package funny.buildapp.pygerdownload.compose

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.insets.statusBarsHeight
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import funny.buildapp.pygerdownload.R
import funny.buildapp.pygerdownload.model.AppInfo
import funny.buildapp.pygerdownload.ui.theme.PGYER
import funny.buildapp.pygerdownload.viewmodel.MainViewModel
import funny.buildapp.pygerdownload.viewmodel.ViewAction


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppScaffold(viewModel: MainViewModel, hasInstallPermission: Boolean, goSetting: () -> Unit) {
    Column(
        Modifier
            .background(Color(0xFFF7F7F7))
            .fillMaxHeight()
    ) {
        // 3. 获取状态栏高度并设置占位
        Spacer(
            modifier = Modifier
                .statusBarsHeight()
                .fillMaxWidth()
        )
        TitleBar(title = "蒲公英商店")
        RefreshLayout(viewModel, hasInstallPermission, goSetting)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RefreshLayout(viewModel: MainViewModel, hasInstallPermission: Boolean, goSetting: () -> Unit) {
    val context = LocalContext.current
    val viewState = viewModel.viewStates
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.dispatch(ViewAction.Refreshing)
        viewModel.downloadUrl.collect {
            if (it.isNotEmpty()) {
                if (hasInstallPermission) {
                    viewModel.gotoDownloadManager(context, it)
                } else {
                    viewModel.gotoBrowserDownload(context, it)
                }
                viewModel.resetDownloadUrl()
            }
        }
    }
    SwipeRefresh(state = rememberSwipeRefreshState(isRefreshing = isRefreshing),
        indicator = { state, refreshTrigger ->
            SwipeRefreshIndicator(
                // Pass the SwipeRefreshState + trigger through
                state = state,
                refreshTriggerDistance = refreshTrigger,
                // Change the color and shape
                contentColor = PGYER,
            )
        },
        onRefresh = { viewModel.dispatch(ViewAction.Refreshing) }) {
        Box {
            var showTipState by remember { mutableStateOf(hasInstallPermission) }
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
            .fillMaxWidth()
            .height(59.dp)
            .background(PGYER)
    ) {
        Text(
            text = title, fontSize = 18.sp, color = Color.White, modifier = Modifier.align(
                Alignment.Center
            )
        )
    }
}


@SuppressLint("UnrememberedMutableState")
@Composable
fun AppInfoCard(
    modifier: Modifier = Modifier,
    id: Int,
    appName: String,
    versionName: String,
    versionCode: String,
    buildCreated: String,
    buildFileSize: Int,
    position: Int,
    onClick: () -> Unit
) {
    val pos by remember { mutableStateOf(position) }   //0-上  1-左 2-右
    val leftDp: Dp by derivedStateOf {
        when (pos) {
            0, 1 -> 8.dp
            2 -> 4.dp
            else -> 8.dp
        }
    }
    val rightDp: Dp by derivedStateOf {
        when (pos) {
            0, 2 -> 8.dp
            1 -> 4.dp
            else -> 8.dp
        }
    }
    val topDp: Dp by derivedStateOf {
        when (pos) {
            0 -> 8.dp
            1, 2 -> 4.dp
            else -> 8.dp
        }
    }
    val bottomDp: Dp by derivedStateOf {
        when (pos) {
            0 -> 4.dp
            1, 2 -> 8.dp
            else -> 8.dp
        }
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
            colors = ButtonDefaults.buttonColors(containerColor = PGYER)
        ) {
            Text(
                "安装", modifier = Modifier.padding(start = 16.dp, end = 16.dp), fontSize = 16.sp
            )
        }

    }
}