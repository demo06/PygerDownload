package funny.buildapp.pygerdownload.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import funny.buildapp.pygerdownload.ui.theme.theme
import funny.buildapp.pygerdownload.ui.theme.white


@Preview
@Composable
fun LoadingDialog(
    title: String = "正在加载...",
    canClickOutSide: Boolean = false,
    isLoading: Boolean = true,
    offsetY: Dp = 0.dp,
    outSideClick: () -> Unit = {}
) {
    if (isLoading) {
        Box(
            Modifier
                .fillMaxSize()
                .clickable {
                    if (canClickOutSide) {
                        outSideClick()
                    }
                }) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = offsetY)
                    .clip(RoundedCornerShape(8.dp))
                    .size(120.dp)
                    .background(color = Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(
                        color = theme,
                        strokeWidth = 4.dp,
                        modifier = Modifier.size(50.dp)
                    )
                    Text(title, color = white, modifier = Modifier.padding(top = 12.dp))
                }

            }
        }

    }
}