package funny.buildapp.pygerdownload

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.statusBarsHeight
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import funny.buildapp.pygerdownload.compose.AppInfoCard
import funny.buildapp.pygerdownload.compose.TitleBar
import funny.buildapp.pygerdownload.ui.theme.PGYER
import funny.buildapp.pygerdownload.ui.theme.PygerDownloadTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 1. 设置状态栏沉浸式
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
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
                    Column(Modifier.fillMaxHeight()) {
                        AppInfoCard(
                            modifier = Modifier.weight(1f),
                            id = R.mipmap.zgw,
                            appName = "中钢网",
                            versionName = "v6",
                            position = 0
                        )
                        Row(modifier = Modifier.weight(1f)) {
                            AppInfoCard(
                                modifier = Modifier.weight(1f),
                                id = R.mipmap.qgb,
                                appName = "抢钢宝" +
                                        "",
                                versionName = "v6",
                                position = 1
                            )
                            AppInfoCard(
                                modifier = Modifier.weight(1f),
                                id = R.mipmap.wlb,
                                appName = "物流宝",
                                versionName = "v6",
                                position = 2
                            )
                        }
                    }

                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PygerDownloadTheme {
        Greeting("Android")
    }
}