package funny.buildapp.pygerdownload.model

import funny.buildapp.clauncher.util.timeDiff

/**
 * 组信息
 * @property appCreated
 * @property appGroupCount
 * @property appGroupDescription
 * @property appGroupKey
 * @property appGroupName
 * @property appGroupShortcutURL
 */
data class GroupInfo(
    val appCreated: String? = null,
    val appGroupCount: String? = null,
    val appGroupDescription: String? = null,
    val appGroupKey: String? = null,
    val appGroupName: String? = null,
    val appGroupShortcutURL: String? = null,
    val apps: List<AppInfo>? = emptyList()
)

data class AppInfo(
    val appId: String? = null,
    val appKey: String? = null,
    val buildBuildVersion: String? = null,
    var buildCreated: String? = null,
    val buildDescription: String? = null,
    val buildFileKey: String? = null,
    val buildFileName: String? = null,
    val buildFileSize: String? = null,
    val buildIcon: String? = null,
    val buildIdentifier: String? = null,
    val buildKey: String? = null,
    val buildLauncherActivity: String? = null,
    val buildName: String? = null,
    val buildPassword: String? = null,
    val buildScreenshots: String? = null,
    val buildType: String? = null,
    val buildUpdateDescription: String? = null,
    val buildVersion: String? = null,
    val buildVersionNo: String? = null
) {
    fun getTime(): String = timeDiff(this.buildCreated ?: "")

}