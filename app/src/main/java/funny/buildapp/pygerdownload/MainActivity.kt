package funny.buildapp.pygerdownload

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
import funny.buildapp.pygerdownload.common.DownloadReceiver
import funny.buildapp.pygerdownload.compose.AppInfoCard
import funny.buildapp.pygerdownload.compose.AppScaffold
import funny.buildapp.pygerdownload.compose.RefreshLayout
import funny.buildapp.pygerdownload.compose.TitleBar
import funny.buildapp.pygerdownload.ui.theme.PGYER
import funny.buildapp.pygerdownload.ui.theme.PygerDownloadTheme
import funny.buildapp.pygerdownload.util.PermissionUtils
import funny.buildapp.pygerdownload.viewmodel.MainViewModel
import funny.buildapp.pygerdownload.viewmodel.ViewAction
import kotlinx.coroutines.flow.collect

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerReceiver()
        // 1. 设置状态栏沉浸式
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val viewModel = MainViewModel()
            val context = LocalContext.current
            // 加入ProvideWindowInsets
            ProvideWindowInsets {
                // 2. 设置状态栏颜色
                rememberSystemUiController().setStatusBarColor(PGYER)
                AppScaffold(
                    viewModel = viewModel,
                    hasInstallPermission = PermissionUtils.haveInstallPermission(context = context)
                ) {
                    PermissionUtils.goSettings(context)
                }
            }
        }
    }

    private fun registerReceiver() {
        val receiver = DownloadReceiver()
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }


}




