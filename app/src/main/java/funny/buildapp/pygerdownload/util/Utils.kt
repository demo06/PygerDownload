package funny.buildapp.clauncher.util

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * @author WenBin
 * @version 1.0
 * @description LogUtil
 * @email wenbin@buildapp.fun
 * @date 2022/6/16
 */

private const val TAG = "PgyerDownload===>"

fun Any.log(tag: String = TAG) {
    Log.i(tag, this.toString())
}

fun Any.toast(context: Context) {
    Toast.makeText(context, this.toString(), Toast.LENGTH_SHORT).show()
}

fun String.toTimeStamp() =
    SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(this).time


//endTime: Long = LocalDateTime.of(LocalDate.now(), LocalTime.MIN).atZone(ZoneId.systemDefault())
//.toEpochSecond() * 1000
fun timeDiff(
    startTime: String,
    endTime: Long = System.currentTimeMillis()
): String {
    if (startTime.isNotEmpty()) {
        val start = startTime.toTimeStamp()
        val diff = endTime - start
        val days = diff / (1000 * 60 * 60 * 24)
        val hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)
        val minutes = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60)
        val second =
            (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60) - minutes * (1000 * 60)) / 1000
        return if (days < 1) { //1天内
            if (hours < 1) {//1小时内
                if (minutes < 1) { //1分钟内
                    if (second < 30) {//30秒钟内
                        "刚刚"
                    } else {
                        "$second 秒前"
                    }
                } else { //超过1分钟
                    "$minutes 分钟前"
                }
            } else {//超过1小时
                "$hours 小时前"
            }
        } else { //超过1天
            "$days 天前"
        }
    } else {
        return startTime
    }
}


fun Modifier.click(onClick: () -> Unit): Modifier {
    return this.composed {
        val lastClickTime = remember { mutableLongStateOf(0L) }
        this.clickable(
            enabled = true,
            onClickLabel = null,
            role = null,
            indication = null,
            interactionSource = remember { MutableInteractionSource() },
            onClick = {
                val now = System.currentTimeMillis()
                if (now - lastClickTime.value > 500) {
                    lastClickTime.value = now
                    onClick()
                }
            }
        )
    }
}


fun Any?.loge() {
    Log.e("wenbin========>", this.toString())
}

