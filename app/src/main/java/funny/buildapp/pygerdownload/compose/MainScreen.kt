package funny.buildapp.pygerdownload.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import funny.buildapp.pygerdownload.R
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
fun AppInfoCard(modifier: Modifier = Modifier, id: Int, appName: String, versionName: String) {
    Column(
        modifier
            .padding(8.dp)
            .background(Color.White, RoundedCornerShape(8.dp))
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = id),
            contentDescription = "icon",
            modifier = Modifier
                .padding(8.dp)
                .size(60.dp)
        )
        Text(appName, color = Color.Black, fontSize = 22.sp)
        Text("更新时间：$versionName", color = Color(0xFF5A5858), fontSize = 14.sp)
        Row() {
            Text("版本：$versionName", color = Color(0xFF5A5858), fontSize = 14.sp)
            Text(text = "   |  ")
            Text("安装包大小：$versionName", color = Color(0xFF5A5858), fontSize = 14.sp)
        }
        Button(
            onClick = { /*TODO*/ }, modifier = Modifier
                .padding(top = 10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PGYER)
        ) {
            Text("安装", modifier = Modifier.padding(start = 16.dp, end = 16.dp), fontSize = 16.sp)
        }

    }
}