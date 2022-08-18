package funny.buildapp.pygerdownload

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModel
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.statusBarsHeight
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import funny.buildapp.pygerdownload.compose.AppInfoCard
import funny.buildapp.pygerdownload.compose.TitleBar
import funny.buildapp.pygerdownload.ui.theme.PGYER
import funny.buildapp.pygerdownload.ui.theme.PygerDownloadTheme
import funny.buildapp.pygerdownload.viewmodel.MainViewModel
import funny.buildapp.pygerdownload.viewmodel.ViewAction
import kotlinx.coroutines.flow.collect

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 1. 设置状态栏沉浸式
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val viewModel = MainViewModel()
            // 加入ProvideWindowInsets
            ProvideWindowInsets {
                // 2. 设置状态栏颜色
                rememberSystemUiController().setStatusBarColor(PGYER)
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
                    RefreshLayout(viewModel)
                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RefreshLayout(viewModel: MainViewModel) {
    val context = LocalContext.current
    val viewState = viewModel.viewStates
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.dispatch(ViewAction.Refreshing)
        viewModel.downloadUrl.collect {
            if (it.isNotEmpty()) {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(it)
                context.startActivity(intent)
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
