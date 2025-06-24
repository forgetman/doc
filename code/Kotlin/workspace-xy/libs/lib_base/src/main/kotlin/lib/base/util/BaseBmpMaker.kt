package lib.base.util

import vector.app.util.Res


/**
 * 图片url的生成或者图片的生成
 *
 * @author yuansui
 */
abstract class BaseBmpMaker {

    companion object {
        private const val RES_PREFIX = "res:///"

        /**
         * 把res id分离出来
         *
         * @param url
         * @return
         */
        fun splitResId(url: String): String {
            return url.substring(RES_PREFIX.length)
        }
    }

    protected fun getUrl(name: String): String {
        return RES_PREFIX + Res.getIdentifier(name, Res.Type.MIPMAP)
    }
}
