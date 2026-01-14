package funny.buildapp.plugin

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File
import java.io.IOException



open class PgyerConfig {
    var updateDescription: String = "版本更新"
    var apiKey: String = "955873f76198c4d20e6478e2a9103fc8"
}

class PgyerUpload : Plugin<Project> {
    private val okHttpClient = OkHttpClient()
    val uploadUrl = "https://www.pgyer.com/apiv2/app/upload"

    override fun apply(project: Project) {
        val config = project.extensions.create("pgyer", PgyerConfig::class.java)

        project.task("upload") { task ->
            task.doLast {
                val apkPath =
                    "${project.buildDir.absolutePath}${File.separator}outputs${File.separator}apk${File.separator}release"
                val path = File(apkPath)
                if (path.exists() && path.isDirectory) {
                    val files = path.listFiles { dir, name ->
                        name.endsWith("apk")
                    }
                    if (files != null) {
                        if (files.isNotEmpty()) {
                            val file = files[0]
                            uploadApk(file, file.name, config)
                        }
                    }
                }
            }.dependsOn("assembleRelease")

        }
    }


    private fun uploadApk(file: File, fileName: String, config: PgyerConfig) {
        val body = file.asRequestBody("multipart/form-data".toMediaType())
        val formBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("_api_key", config.apiKey)
            .addFormDataPart("file", fileName, body)
            .addFormDataPart(
                "buildUpdateDescription", config.updateDescription
            )
            .build()
        val request = Request.Builder()
            .url(uploadUrl)
            .post(formBody)
            .build()
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("上传失败")
            }

            override fun onResponse(call: Call, response: Response) {
                println("msg:${response.message}code:${response.code}")
            }

        })
    }
}