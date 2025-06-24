package dsb.design.repo

import dsb.App
import dsb.R
import dsb.model.Form41
import dsb.model.Me
import dsb.network.api.CommonApi
import dsb.network.api.MeApi
import dsb.util.FormUtil
import kotlinx.coroutines.flow.map
import lib.base.network.createApi
import vector.bindingadapter.GridLayoutSet
import vector.ext.copyFields
import vector.os.dimenRes
import vector.app.util.Screen
import javax.inject.Inject

/**
 * @author yuansui
 * @since 2019/1/21
 */
class MeRepo @Inject constructor() {

    /**
     * 上传头像
     */
    fun uploadAvatar(path: String) =
        createApi<CommonApi>().upload("avatar", path)

    fun fetchData() =
        createApi<MeApi>()
            .list(App.currCity?.id)
            .map { packageList ->
                val me = Me()

                packageList.forEach { form ->
                    when (form.viewType) {
                        3402 -> {
                            me.title = form.title
                        }
                        4201 -> {
                            me.sets = form.list
                                ?.map {
                                    val width = Screen.width - R.dimen.margin.dimenRes.toPx() * 2
                                    val type = GridLayoutSet.LayoutType.average(width)
                                    FormUtil.getGridLayoutSet(it, R.layout.layout_item_4201, type)
                                }
                                ?.toMutableList()
                        }
                        else -> {
                            // 41
                            me.others.add(Form41().apply {
                                copyFields(form)
                            })
                        }
                    }
                }

                me
            }
}