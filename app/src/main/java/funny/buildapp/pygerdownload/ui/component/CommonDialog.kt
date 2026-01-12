package funny.buildapp.pygerdownload.ui.component

import android.opengl.Visibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Backup
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import funny.buildapp.clauncher.util.click
import funny.buildapp.pygerdownload.ui.theme.black
import funny.buildapp.pygerdownload.ui.theme.black333
import funny.buildapp.pygerdownload.ui.theme.gray666
import funny.buildapp.pygerdownload.ui.theme.gray999
import funny.buildapp.pygerdownload.ui.theme.halfblack
import funny.buildapp.pygerdownload.ui.theme.orangeFF7300
import funny.buildapp.pygerdownload.ui.theme.theme
import funny.buildapp.pygerdownload.ui.theme.white
import funny.buildapp.pygerdownload.ui.theme.whiteF4F5FA
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CommonDialog(onDismiss: () -> Unit, confirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(text = "安装提示", fontSize = 20.sp)
        }, text = {
            Text(text = "需要打开安装未知应用权限", fontSize = 16.sp)
        }, confirmButton = {
            Text(
                text = "去设置",
                color = theme,
                modifier = Modifier.clickable {
                    confirm.invoke()
                },
                fontSize = 15.sp
            )

        }, dismissButton = {
            Text(text = "取消", fontSize = 15.sp, color = theme, modifier = Modifier.clickable {
                onDismiss.invoke()
            })

        })
}


//@Preview
@Composable
fun ConfirmDialog(
    show: Boolean = true,
    showCancelButton: Boolean = true,
    title: String = "标题",
    content: String = "内容",
    cancelText: String = "取消",
    confirmText: String = "确定",
    onDismiss: () -> Unit = {},
    onConfirm: () -> Unit = {}
) {
    if (show) {
        Column(
            Modifier
                .fillMaxSize()
                .background(halfblack)
                .click {},
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth()
                    .background(white, RoundedCornerShape(5.dp)),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                if (title.isNotEmpty()) {
                    Text(
                        title,
                        fontSize = 18.sp,
                        color = black333,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp, start = 12.dp, end = 12.dp)
                    )
                    Spacer(Modifier.height(28.dp))
                }

                Text(
                    content,
                    fontSize = 14.sp,
                    color = gray666,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                )
                Spacer(Modifier.height(28.dp))

                Column(Modifier.fillMaxWidth()) {
                    Spacer(
                        Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(whiteF4F5FA)
                    )
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (showCancelButton) {
                            Text(
                                cancelText,
                                fontSize = 15.sp,
                                color = black333,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        onDismiss()
                                    }
                            )
                            Spacer(
                                Modifier
                                    .width(1.dp)
                                    .fillMaxHeight()
                                    .background(whiteF4F5FA)
                            )
                        }

                        Text(
                            confirmText,
                            fontSize = 15.sp,
                            color = orangeFF7300,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    onConfirm()
                                    onDismiss()
                                }
                        )
                    }
                }


            }
        }


    }
}

@Preview
@Composable
fun UpgradeDialog(
    visible: Boolean = false,
    isForceUpdate: Boolean = false,
    isDownloading: Boolean = false,
    updateContent: String = "这是一次跨版本更新",
    onDismiss: () -> Unit = {},
    onConfirm: () -> Unit = {}
) {
    var currentProgress by remember { mutableFloatStateOf(0f) }
    val scope = rememberCoroutineScope()

    suspend fun loadProgress(updateProgress: (Float) -> Unit) {
        for (i in 1..100) {
            updateProgress(i.toFloat() / 100)
            delay(100)
        }
    }
    if (visible) {
        Box(
            Modifier
                .fillMaxSize()
                .background(black.copy(0.6f))
                .click { },
            contentAlignment = Alignment.Center
        ) {
            Column(
                Modifier
                    .width(310.dp)
                    .background(white, RoundedCornerShape(8.dp))
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Rounded.Backup,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape),
                    contentDescription = null,
                    tint = theme
                )
                Text(
                    "发现新版本", color = black333, fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = buildString {
                        append("更新内容:\n")
                        append(updateContent)

                    },
                    color = gray999,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 16.dp)
                )

                if (isDownloading) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LinearProgressIndicator(
                            progress = { currentProgress },
                            modifier = Modifier
                                .height(12.dp)
                                .weight(1f),
                            color = theme,
                            trackColor = theme.copy(0.2f),
                        )
                        Text(
                            "${(currentProgress * 100).toInt()}%",
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .width(35.dp)
                        )
                    }
//                Button({
//                    scope.launch {
//                        loadProgress { progress ->
//                            currentProgress = progress
//                        }
//                    }
//                }) {
//                    Text("更新")
//                }

                } else {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (!isForceUpdate) {
                            Text(
                                "下次在说",
                                fontSize = 14.sp,
                                color = theme,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .weight(1f)
                                    .border(1.dp, theme, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                    .click(onDismiss)
                            )
                            Spacer(Modifier.width(20.dp))
                        }
                        Text(
                            "立即更新",
                            fontSize = 14.sp,
                            color = white,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .weight(1f)
                                .background(theme, RoundedCornerShape(4.dp))
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .click{
                                    onDismiss()
                                    onConfirm()
                                }
                        )
                    }
                }
            }
        }
    }


}