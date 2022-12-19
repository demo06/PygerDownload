package funny.buildapp.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class UploadPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.task("upload") { task ->
            task.doLast {
                println("upload =====> done!")
            }
        }.dependsOn("assembleRelease")
    }
}