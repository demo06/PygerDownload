package funny.buildapp.pygerdownload.model

/**
 *
 * Created by htf on 2021/6/24
 * Describe:
 */

data class BaseBean<T>(
    val data: T,
    val code: Int? = null,
    val message: String? = null
)

/**
 * 获取 订单ID 史东东使用的前端老接口、时间紧任务重 总之。。。格式有问题
 */
data class OrderBaseBean<T>(
    val data: T,
    val result: Int,
    val statuscode: Int,
    val msg: String,
    val orderid: String
)


/**
 * PageList，分页泛型集合(总页数+总条数)
 */
data class PageList<T>(
    /**
     * 当前页集合
     */
    val items: List<T>? = null,

    /**
     * 总条数
     */
    val total: Long? = null,

    /**
     * 总页数
     */
    val totalPages: Long? = null
)