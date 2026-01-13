package funny.buildapp.pygerdownload.net

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import funny.buildapp.clauncher.util.log
import funny.buildapp.pygerdownload.util.Constants.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * @author WenBin
 * @version 1.0
 * @description  retrofit 工具类
 * @email wenbin@buildapp.fun
 * @date 2022/6/16
 */
object NetWork {
    private val loggingInterceptor = HttpLoggingInterceptor {
        it.log("Retrofit===>")
    }.setLevel(HttpLoggingInterceptor.Level.BODY)


    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()


    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(
            MoshiConverterFactory.create(Moshi.Builder().add(KotlinJsonAdapterFactory()).build())
        ) //添加转换器
        .build()

    fun <T> createService(serviceClass: Class<T>): T {
        return retrofit.create(serviceClass)
    }

}