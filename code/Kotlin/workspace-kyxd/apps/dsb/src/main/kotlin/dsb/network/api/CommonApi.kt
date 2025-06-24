package dsb.network.api

import dsb.model.AppUpdate
import dsb.network.LIMIT
import dsb.network.PAGE
import eth.annotation.method.Post
import eth.annotation.method.Upload
import eth.annotation.param.File
import eth.annotation.param.Query
import kotlinx.coroutines.flow.Flow
import lib.base.model.Form
import lib.base.model.Page

interface CommonApi {
    @Post("v2/home/show")
    fun home(
        @Query("city_id") cityId: String?,
        @Query(PAGE) page: Int,
        @Query(LIMIT) limit: Int = Page.LIMIT
    ): Flow<MutableList<Form>>

    @Upload("v4/upload/uploadImg")
    fun upload(@Query("type") type: String, @File("image") image: String): Flow<String>

    @Post("v4/device/VersionUpdate")
    fun checkVersion(): Flow<AppUpdate>
}