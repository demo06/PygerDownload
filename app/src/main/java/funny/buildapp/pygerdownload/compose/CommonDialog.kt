package funny.buildapp.pygerdownload.compose

import androidx.compose.foundation.clickable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp

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
                modifier = Modifier.clickable {
                    confirm.invoke()
                },
                fontSize = 15.sp
            )

        }, dismissButton = {
            Text(text = "取消", fontSize = 15.sp, modifier = Modifier.clickable {
                onDismiss.invoke()
            })

        })
}