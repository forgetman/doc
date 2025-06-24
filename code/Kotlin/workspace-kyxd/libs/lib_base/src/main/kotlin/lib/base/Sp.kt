package lib.base

import lib.base.model.User
import vector.store.SpEx

object Sp : SpEx() {

    private const val KEY_USER: String = "user"
    private const val KEY_TOKEN: String = "token"
    private const val KEY_DID = "did" // 从服务器获取的唯一id

    private const val DEFAULT_TOKEN = "YjQ3YWRlZDZiMDQ2MDFkNTQ1ZjllNDZjZmFkODcyOWE="

    override val fileName: String
        get() = "sp_user"

    fun putUser(user: User?) {
        if (user == null) return
        put(KEY_USER, user)
        put(KEY_TOKEN, user.token ?: DEFAULT_TOKEN)
    }

    fun clearUser() {
        remove(KEY_USER)
        remove(KEY_TOKEN)
    }

    fun getUser(): User? = getObject(KEY_USER)

    fun getToken(): String = getString(KEY_TOKEN) ?: DEFAULT_TOKEN

    fun getDid(): String? = getString(KEY_DID)

    fun putDid(did: String) {
        put(KEY_DID, did)
    }

    @JvmStatic
    fun isSignIn() = getToken() != DEFAULT_TOKEN
}