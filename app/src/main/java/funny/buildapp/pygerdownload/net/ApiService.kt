package funny.buildapp.pygerdownload.net

import funny.buildapp.clauncher.net.NetWork
import funny.buildapp.pygerdownload.model.BaseBean
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * @author WenBin
 * @version 1.0
 * @description 网络接口
 * @email wenbin@buildapp.fun
 * @date 2022/6/16
 */
interface ApiService {

    @GET("api/AndroidApi/getAppData")
    suspend fun getAppData(@Query("data_type") id: String): BaseBean

    companion object {
        fun instance(): ApiService {
            return NetWork.createService(ApiService::class.java)
        }
    }
}