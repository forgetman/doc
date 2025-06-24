package lib.base.util

import android.widget.Toast
import lib.base.BaseApp
import lib.base.R
import vector.app.util.Res


object ToastUtil {

    private var toastKeeper: Toast? = null
    private var currType = ToastType.NONE

    enum class ToastType {
        NONE,
        EXIT_TIPS,
        CHARGING,
        NOT_CHARGING,
        CUSTOM,
        LUNAR_END_FIRST,
        ELunarEndLast,
        ESkinError,
        ESkinInstallSucceed,
        ESkinInstallFailed,
        ESkinDelSucceed,
        ESkinUsing,
        ESkinSwitchSucceed,
        ESkinVersionCannotInstall,
        ENetworkInvalid
    }

    fun makeToast(type: ToastType) {

        if (currType != type) {

            currType = type

            when (type) {
                ToastType.EXIT_TIPS -> toastKeeper = Toast.makeText(
                    BaseApp.context,
                    Res.getString(R.string.toast_prepare_exit),
                    Toast.LENGTH_SHORT
                )
                ToastType.CHARGING -> toastKeeper = Toast.makeText(
                    BaseApp.context,
                    Res.getString(R.string.toast_charging),
                    Toast.LENGTH_LONG
                )
                ToastType.NOT_CHARGING -> toastKeeper = Toast.makeText(
                    BaseApp.context,
                    Res.getString(R.string.toast_not_charging),
                    Toast.LENGTH_SHORT
                )
                ToastType.LUNAR_END_FIRST -> toastKeeper = Toast.makeText(
                    BaseApp.context,
                    Res.getString(R.string.lunar_end_first),
                    Toast.LENGTH_SHORT
                )
                ToastType.ELunarEndLast -> toastKeeper = Toast.makeText(
                    BaseApp.context,
                    Res.getString(R.string.lunar_end_last),
                    Toast.LENGTH_SHORT
                )
                ToastType.ESkinError -> toastKeeper = Toast.makeText(
                    BaseApp.context,
                    Res.getString(R.string.skin_error),
                    Toast.LENGTH_SHORT
                )
                ToastType.ESkinInstallSucceed -> toastKeeper = Toast.makeText(
                    BaseApp.context,
                    Res.getString(R.string.skin_install_succeed),
                    Toast.LENGTH_SHORT
                )
                ToastType.ESkinInstallFailed -> toastKeeper = Toast.makeText(
                    BaseApp.context,
                    Res.getString(R.string.skin_install_failed),
                    Toast.LENGTH_SHORT
                )
                ToastType.ESkinDelSucceed -> toastKeeper = Toast.makeText(
                    BaseApp.context,
                    Res.getString(R.string.skin_del_succeed),
                    Toast.LENGTH_SHORT
                )
                ToastType.ESkinUsing -> toastKeeper = Toast.makeText(
                    BaseApp.context,
                    Res.getString(R.string.skin_using),
                    Toast.LENGTH_SHORT
                )
                ToastType.ESkinSwitchSucceed -> toastKeeper = Toast.makeText(
                    BaseApp.context,
                    Res.getString(R.string.skin_switch_succeed),
                    Toast.LENGTH_SHORT
                )
                ToastType.ESkinVersionCannotInstall -> toastKeeper = Toast.makeText(
                    BaseApp.context,
                    Res.getString(R.string.skin_version_invalid),
                    Toast.LENGTH_SHORT
                )
                ToastType.ENetworkInvalid -> toastKeeper = Toast.makeText(
                    BaseApp.context,
                    Res.getString(R.string.network_invalid),
                    Toast.LENGTH_SHORT
                )
                else -> {
                }
            }
        }

        toastKeeper?.show()
    }

    fun makeToast(string: String) {
        currType = ToastType.CUSTOM
        toastKeeper = Toast.makeText(BaseApp.context, string, Toast.LENGTH_SHORT)
        toastKeeper?.show()
    }
}
