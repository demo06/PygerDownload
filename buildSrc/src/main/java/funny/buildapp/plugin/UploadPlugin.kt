package funny.buildapp.plugin

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File
import java.io.IOException


class UploadPlugin : Plugin<Project> {
    private val okHttpClient = OkHttpClient()
    val uploadUrl = "https://www.pgyer.com/apiv2/app/upload"
    override fun apply(project: Project) {
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
//                            println(file.name)
                            uploadApk(file, file.name)
                        }
                    }
//                    println("apkFiles =====> done!${files[0].absolutePath}")
                }
            }
//        }.dependsOn("assembleRelease")
        }
    }

    private fun uploadApk(file: File, fileName: String) {
        val body = file.asRequestBody("multipart/form-data".toMediaType())
        val formBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("_api_key", "955873f76198c4d20e6478e2a9103fc8")
            .addFormDataPart("file", fileName, body)
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