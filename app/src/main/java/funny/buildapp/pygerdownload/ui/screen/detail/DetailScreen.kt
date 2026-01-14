package funny.buildapp.pygerdownload.ui.screen.detail

import android.R.attr.versionCode
import android.R.attr.versionName
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import funny.buildapp.pygerdownload.route.LocalNavigator
import funny.buildapp.pygerdownload.ui.component.Screen
import funny.buildapp.pygerdownload.ui.component.TitleBar
import funny.buildapp.pygerdownload.ui.screen.home.Tag
import funny.buildapp.pygerdownload.ui.theme.black333
import funny.buildapp.pygerdownload.ui.theme.gray999
import funny.buildapp.pygerdownload.ui.theme.green07C160
import funny.buildapp.pygerdownload.ui.theme.theme
import funny.buildapp.pygerdownload.ui.theme.white
import funny.buildapp.pygerdownload.ui.theme.whiteF4F5FA

@Preview
@Composable
fun DetailScreen(item: AppInfo = AppInfo(), viewModel: DetailViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val dispatch = viewModel::dispatch
    UiEffect(viewModel, item)
    Screen(
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
                    iconUrl = uiState.appInfo.buildIcon ?: "112312",
                    onDownloadClick = { dispatch(DetailUiAction.Download) }
                )
            },
            versionHistory = {
                VersionHistoryCard(
                    versionHistory = uiState.versionHistory.orEmpty(),
                    onItemClick = { index -> dispatch(DetailUiAction.SelectVersion(index)) }
                )
            }
        )
    }
}

@Composable
private fun UiEffect(viewModel: DetailViewModel, item: AppInfo) {
    val context = LocalContext.current
    val navigator = LocalNavigator.current
    val dispatch = viewModel::dispatch

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
    buildCreated: String = "3分钟前",
    iconUrl: String = "12312",
    onDownloadClick: () -> Unit = {}
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
                        Spacer(modifier = Modifier.width(4.dp))
                        Tag("MINI", green07C160)
                    }
                }
            }

            Text(
                "下载",
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .background(theme, RoundedCornerShape(30.dp))
                    .click(onDownloadClick)
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                color = Color.White,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Column {
            InfoRow("版本", versionName)
            InfoRow("版本号(Build)", versionCode)
            InfoRow("大小", "${buildFileSize / 1024 / 1024}M")
            InfoRow("更新时间", buildCreated)
        }
    }
}

@Composable
private fun VersionHistoryCard(
    versionHistory: List<VersionInfo> = emptyList(),
    onItemClick: (Int) -> Unit = {}
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
                versionName = version.versionName,
                versionCode = version.versionCode,
                buildCreated = version.buildCreated,
                buildFileSize = version.buildFileSize,
                isLatest = index == 0,
                onItemClick = { onItemClick(index) }
            )
            if (index < versionHistory.size - 1) {
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun VersionItem(
    versionName: String = "v1.1.0",
    versionCode: String = "19",
    buildCreated: String = "2天前",
    buildFileSize: Int = 1020213123,
    isLatest: Boolean = false,
    onItemClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isLatest) Color(0xFFF0F9FF) else Color.Transparent, RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    versionName,
                    color = black333,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                if (isLatest) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Tag("最新", green07C160)
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
            Text(
                "下载",
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .background(theme, RoundedCornerShape(20.dp))
                    .click(onItemClick)
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                color = Color.White,
                fontSize = 14.sp
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

