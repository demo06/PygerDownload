package funny.buildapp.pygerdownload.compose

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import funny.buildapp.pygerdownload.R
import funny.buildapp.pygerdownload.ui.theme.GARY
import funny.buildapp.pygerdownload.ui.theme.PGYER
import funny.buildapp.pygerdownload.util.PermissionUtils
import funny.buildapp.pygerdownload.viewmodel.MainViewModel
import funny.buildapp.pygerdownload.viewmodel.ViewAction

@Preview
@Composable
fun AppScaffold(viewModel: MainViewModel = viewModel()) {
    val context = LocalContext.current
    Column(
        Modifier
            .background(Color(0xFFF7F7F7))
            .fillMaxHeight()
    ) {
        TitleBar(title = "蒲公英商店")
        RefreshLayout(viewModel) {
            PermissionUtils.goSettings(context)
        }
    }
}

@Composable
fun RefreshLayout(viewModel: MainViewModel, goSetting: () -> Unit) {
    val context = LocalContext.current
    val viewState = viewModel.viewStates
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val state =rememberPullToRefreshState()
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
        state=state,
        onRefresh = {
            viewModel.dispatch(ViewAction.Refreshing)
        },
        indicator = {
            Indicator(
                modifier = Modifier.align(Alignment.TopCenter),
                isRefreshing = isRefreshing,
                containerColor = GARY,
                color =PGYER ,
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
            .background(PGYER)
            .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
            .fillMaxWidth()
            .height(49.dp)
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