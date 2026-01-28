package funny.buildapp.pygerdownload.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import funny.buildapp.clauncher.util.click
import funny.buildapp.pygerdownload.ui.theme.theme

/**
 * 下载状态枚举
 */
enum class DownloadState {
    IDLE,       // 默认状态：显示"下载"
    DOWNLOADING, // 下载中：显示进度
    COMPLETED   // 下载完成：显示"安装"
}

/**
 * 下载按钮组件
 * @param state 下载状态
 * @param progress 下载进度 (0-100)
 * @param onDownloadClick 点击下载回调
 * @param onInstallClick 点击安装回调
 */
@Composable
fun DownLoadButton(
    state: DownloadState = DownloadState.IDLE,
    progress: Int = 0,
    onDownloadClick: () -> Unit = {},
    onInstallClick: () -> Unit = {}
) {
    val buttonWidth = 72.dp
    val buttonHeight = 28.dp
    val cornerRadius = 30.dp

    when (state) {
        DownloadState.IDLE -> {
            // 默认状态：背景为 theme 色，显示"下载"
            Box(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .width(buttonWidth)
                    .height(buttonHeight)
                    .clip(RoundedCornerShape(cornerRadius))
                    .background(theme)
                    .click(onDownloadClick),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "下载",
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
        }

        DownloadState.DOWNLOADING -> {
            // 下载中状态：背景底色为 theme.copy(alpha=0.4f)，进度色为 theme
            Box(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .width(buttonWidth)
                    .height(buttonHeight)
                    .clip(RoundedCornerShape(cornerRadius))
                    .background(theme.copy(alpha = 0.4f)),
                contentAlignment = Alignment.CenterStart
            ) {
                // 进度条填充
                Box(
                    modifier = Modifier
                        .width(buttonWidth * (progress.coerceIn(0, 100) / 100f))
                        .height(buttonHeight)
                        .background(theme)
                )
                // 进度文字
                Text(
                    "${progress}%",
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
        }

        DownloadState.COMPLETED -> {
            // 下载完成状态：背景为 theme 色，显示"安装"
            Box(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .width(buttonWidth)
                    .height(buttonHeight)
                    .clip(RoundedCornerShape(cornerRadius))
                    .background(theme)
                    .click(onInstallClick),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "安装",
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
        }
    }
}