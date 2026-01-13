package funny.buildapp.pygerdownload.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import funny.buildapp.pygerdownload.ui.theme.theme


@Preview
@Composable
fun TitleBar(title: String = "标题", onBack: (() -> Unit)? = null) {
    Box(
        modifier = Modifier
            .background(theme)
            .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
            .fillMaxWidth()
            .height(49.dp)
            .background(theme)
    ) {
        if (onBack != null) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .align(Alignment.CenterStart)
            ) {
                Icon(
                    modifier = Modifier.padding(start = 8.dp),
                    imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
        }
        Text(
            text = title,
            fontSize = 18.sp,
            color = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}