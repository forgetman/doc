package dsb.design.repo

import dsb.model.*
import dsb.network.api.CommonApi
import kotlinx.coroutines.flow.map
import lib.base.model.Form
import lib.base.model.Page
import lib.base.network.createApi
import vector.bindingadapter.GridLayoutSet
import vector.app.util.Screen
import javax.inject.Inject

/**
 * @author yuansui
 * @since 2019/1/31
 */
class FormRepo @Inject constructor() {

    private fun getTestData(): List<Form> {
        val forms = mutableListOf<Form>()
        forms.add(Form0().apply { viewType = 0 })
        forms.add(Form38().apply { viewType = 38 })
        forms.add(Form33().apply { viewType = 33 })
        forms.add(Form39().apply { viewType = 39 })
        forms.add(Form50().apply { viewType = 50 })
        forms.add(Form40().apply { viewType = 40 })
        forms.add(Form51().apply { viewType = 51 })
        forms.add(Form51().apply { viewType = 52 })
        forms.add(Form51().apply { viewType = 52 })
        forms.add(Form51().apply { viewType = 52 })
        return forms
    }

    fun fetchData(cityId: String?, page: Page) =
        createApi<CommonApi>()
            .home(cityId, page.num)
            .map { packageList ->
                // 去掉最外层多余的list标签, 重新组合一个list
//                val forms = mutableListOf<Form>()

//                packageList.filter {
//                    val list = it.list
//                    list != null && list.isNotEmpty()
//                }.flatMap {
//                    it.list ?: mutableListOf()
//                }.forEach { form ->
//                    val realForm = when (form.viewType) {
//
//                        19 -> Form19().apply {
//                            copyFields(form)
//
//                            sets = form.list?.map {
//                                FormUtil.getGridLayoutSet(
//                                    it,
//                                    R.layout.layout_item_19,
//                                    getGridLayoutSetType()
//                                )
//                            }
//                        }
//
//                        33 -> Form33().apply {
//                            copyFields(form)
//
//                            texts = form.list?.filter {
//                                !it.title.isNullOrEmpty()
//                            }?.map {
//                                it.title.orEmpty()
//                            }?.toTypedArray()
//                        }
//
//                        35 -> Form35().apply {
//                            copyFields(form)
//
//                            sets = form.list?.map {
//                                FormUtil.getGridLayoutSet(
//                                    it,
//                                    R.layout.layout_item_35,
//                                    getGridLayoutSetType()
//                                )
//                            }
//                        }
//
//                        38 -> Form38().apply {
//                            copyFields(form)
//
//                            data = form.list?.map {
//                                getBannerSet(it)
//                            }
//                        }
//
//                        40 -> Form40().apply {
//                            copyFields(form)
//
//                            data = form.list?.filter {
//                                !it.icon.isNullOrEmpty()
//                            }?.map {
//                                getBannerSet(it)
//                            }
//                        }
//
//                        else -> Form0().apply {
//                            copyFields(form)
//                        }
//                    }
//                    forms.add(realForm)
//                }
//                forms
                getTestData()
            }

    private fun getGridLayoutSetType() = GridLayoutSet.LayoutType.average(Screen.width)

    private fun getBannerSet(form: Form) =
        Banner().apply {
            icon = form.icon
            title = form.title
            subTitle = form.subTitle
            content = form.content
            date = form.date
            type = form.viewType
            url = form.url
            needLogin = form.needLogin
        }
}