package funny.buildapp.pygerdownload.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import funny.buildapp.pygerdownload.route.AppRoute
import funny.buildapp.pygerdownload.route.LocalNavigator
import funny.buildapp.pygerdownload.ui.component.Screen
import funny.buildapp.pygerdownload.ui.component.TitleBar
import funny.buildapp.pygerdownload.ui.component.UpgradeDialog
import funny.buildapp.pygerdownload.ui.screen.detail.DetailUiEffect
import funny.buildapp.pygerdownload.ui.theme.black333
import funny.buildapp.pygerdownload.ui.theme.gray999
import funny.buildapp.pygerdownload.ui.theme.green07C160
import funny.buildapp.pygerdownload.ui.theme.theme
import funny.buildapp.pygerdownload.ui.theme.white
import funny.buildapp.pygerdownload.ui.theme.whiteF4F5FA

@Preview
@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val dispatch = viewModel::dispatch
    UIEffect(viewModel, dispatch)
    Screen(
        modifier = Modifier.background(whiteF4F5FA),
        titleBar = {
            TitleBar(title = "蒲公英商店")
        },
        dialog = {
            UpgradeDialog(
                visible = uiState.hasUpdate,
                isForceUpdate = false,
                isDownloading = false,
                onDismiss = { dispatch(HomeUiAction.ShowUpdate) },
                onConfirm = {
                    dispatch(HomeUiAction.ShowUpdate)
                    dispatch(HomeUiAction.Update)
                })
        }
    ) {
        Content(
            isRefreshing = uiState.isRefreshing,
            dispatch = viewModel::dispatch,
            header = {
                val items = uiState.items.orEmpty()
                if (items.size >= 3) {
                    Header(topAppCard = { index ->
                        val item = items[index]
                        AppTopCard(
                            modifier = Modifier.weight(1f),
                            index = index,
                            appName = item.buildName ?: "",
                            versionName = item.buildVersion ?: "",
                            createdTime = item.getTime(),
                            iconUrl = item.buildIcon ?: "",
                            onDownloadClick = {},
                            onItemClick = {
                                dispatch(HomeUiAction.GoDetail)
                            })
                    })
                }
            },
            item = {
                Item(onItemClick = {
                    dispatch(HomeUiAction.GoMINIDetail)
                })
            },
        )

    }
}

@Composable
private fun UIEffect(viewModel: HomeViewModel, dispatch: (HomeUiAction) -> Unit) {
    val context = LocalContext.current
    val navigator = LocalNavigator.current

    LaunchedEffect(Unit) {
        dispatch(HomeUiAction.FetchData)
    }
    LaunchedEffect(viewModel) {
        viewModel.effect.collect {
            when (it) {
                is HomeUiEffect.ShowToast -> {
                    it.msg.toast(context)
                }

                is HomeUiEffect.GoMINIDetail -> {
                    navigator.push(AppRoute.MiniDetail(1))
                }

                is HomeUiEffect.GoDetail -> {
                    navigator.push(AppRoute.Detail(1))
                }
            }
        }
    }
}

@Composable
private fun Content(
    isRefreshing: Boolean,
    header: @Composable () -> Unit,
    item: @Composable () -> Unit = {},
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
    onDownloadClick: () -> Unit = {},
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
        Text(versionName, color = gray999, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(6.dp))
        Text(createdTime, color = gray999, fontSize = 14.sp)
        Text(
            "下载",
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .padding(top = 8.dp)
                .background(theme, RoundedCornerShape(30.dp))
                .click(onDownloadClick)
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
fun Tag(text: String = "APP", background: Color = theme) {
    Text(
        modifier = Modifier
            .background(background, RoundedCornerShape(4.dp))
            .padding(horizontal = 4.dp, vertical = 2.dp),
        text = text,
        fontSize = 12.sp,
        color = white
    )

}
