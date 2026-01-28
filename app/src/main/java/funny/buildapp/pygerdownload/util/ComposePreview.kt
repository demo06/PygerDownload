package funny.buildapp.pygerdownload.util

import androidx.compose.ui.tooling.preview.Preview

@Preview(device = "spec:width=1080px,height=2400px,dpi=480")
annotation class VIVO_Z6

@Preview(device = "spec:width=1080px,height=2340px,dpi=480")
annotation class HUAWEI_P40

@Preview(device = "spec:width=1080px,height=2400px,dpi=440")
annotation class XIAOMI11_LITE

@Preview(device = "spec:width=1440px,height=3200px,dpi=560")
annotation class REDMI_K60


@REDMI_K60
@XIAOMI11_LITE
@HUAWEI_P40
@VIVO_Z6
annotation class AllTestDevices
