package funny.buildapp.pygerdownload.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import funny.buildapp.pygerdownload.model.AppInfo
import funny.buildapp.pygerdownload.ui.theme.PGYER

@Composable
fun TitleBar(title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(59.dp)
            .background(PGYER)
    ) {
        Text(
            text = title, fontSize = 18.sp, color = Color.White, modifier = Modifier.align(
                Alignment.Center
            )
        )
    }
}


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