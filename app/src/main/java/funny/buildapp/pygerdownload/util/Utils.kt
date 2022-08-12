package funny.buildapp.clauncher.util

import android.util.Log
import androidx.compose.ui.graphics.Color

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

val String.color
    get() = Color(android.graphics.Color.parseColor(this))




