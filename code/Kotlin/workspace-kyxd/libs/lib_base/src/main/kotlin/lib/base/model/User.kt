package lib.base.model

import com.google.gson.annotations.SerializedName
import lib.base.Sp

/**
 * @author yuansui
 * @since 2019/1/17
 */
class User {

    companion object {
        private const val WAIT_LOGIN = "点击登录"

        private var instance: User? = null
            get() {
                if (field == null) {
                    field = Sp.getUser()
                }
                return field
            }

        @JvmStatic
        fun get(): User {
            return instance ?: User()
        }

        fun update(user: User) {
            instance = user
            Sp.putUser(user)
        }

        /**
         * 归档
         */
        fun archive() {
            Sp.putUser(instance)
        }

        fun clear() {
            Sp.clearUser()
            instance = null
        }
    }

    var avatar: String? = null

    var mobile: String? = null
        get() = if (field == null) WAIT_LOGIN else field

    @SerializedName("user_token")
    var token: String? = null

    @SerializedName("wxuid")
    var wxUid: String? = null
}