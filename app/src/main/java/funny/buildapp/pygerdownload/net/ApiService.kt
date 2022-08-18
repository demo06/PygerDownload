package funny.buildapp.pygerdownload.net

import funny.buildapp.pygerdownload.model.BaseBean
import funny.buildapp.pygerdownload.model.GroupInfo
import retrofit2.http.*

/**
 * @author WenBin
 * @version 1.0
 * @description 网络接口
 * @email wenbin@buildapp.fun
 * @date 2022/6/16
 */
interface ApiService {
    companion object {
        fun instance(): ApiService {
            return NetWork.createService(ApiService::class.java)
        }
    }

    @FormUrlEncoded
    @POST("apiv2/appGroup/view")
    suspend fun appGroup(
        @Field("_api_key") appKey: String,
        @Field("appGroupKey") appGroupKey: String,
    ): BaseBean<GroupInfo>


    @GET("apiv2/app/install")
    suspend fun install(
        @Query("_api_key") apiKey: String,
        @Query("appKey") appKey: String,
        @Query("buildPassword") buildPassword: String
    ): BaseBean<GroupInfo>


}