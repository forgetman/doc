package pretimmediat.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import pretimmediat.R
import pretimmediat.adapter.PermissionItemBinder
import pretimmediat.databinding.DialogPermissionSecondBinding
import pretimmediat.ext.requireAllPermissions
import pretimmediat.model.Permission
import vector.app.databinding.dialog.DBDialogEx
import vector.app.os.dimenRes
import vector.bindingadapter.bind.Bind
import vector.app.os.dp
import vector.util.LayoutParamsFactory
import vector.util.MATCH_PARENT
import vector.widget.scrollable.decoration.Decoration

/**
 * 二次申请权限弹窗(可能是部分权限)
 */
class PermissionSecondDialog(
    context: Context?,
    private val callback: (allowApply: Boolean, applyResult: Boolean) -> Unit
) :
    DBDialogEx(context) {

    override val params: ViewGroup.LayoutParams
        get() = LayoutParamsFactory.viewGroup(MATCH_PARENT, MATCH_PARENT)

    override val marginStart: Int
        get() = R.dimen.margin_horizontal.dimenRes.toPx(context)

    override val marginEnd: Int
        get() = R.dimen.margin_horizontal.dimenRes.toPx(context)

    override val marginBottom: Int
        get() = 22.dp.toPx(context)

    val data = listOf(
        // 3
        Permission(
            "SMS",
            "Nous collectons toutes vos données SMS, en particulier nous surveillons le nom, la description et le montant de la transaction de lexpéditeur pour lévaluation du risque de crédit. Cela permet des remboursements de prêt de plus en plus rapides. Les données personnelles SMS sont lues, stockées ou partagées. Nous téléchargerons les données SMS sur le serveur Prêt immédiat ( https://www.pretimmediatpi.com ), vos données seront protégées et confidentielles."
        ),
        // 4
        Permission(
            "Contacts",
            "Nous collectons tous les contacts de votre annuaire téléphonique, y compris le nom de votre contact téléphonique, le numéro de téléphone, la date d'ajout du contact.Ce qui précède vous permettra de sélectionner, dans votre demande de prêt, vos contacts de référence ainsi que de nous permettre de les contacter afin de valider l'identité (du client et de la référence), d'éviter la fraude, de suivre le recouvrement. De cette façon, nous serons en mesure de mieux évaluer votre profil de crédit et de gérer nos risques d'identification et de gestion du portefeuille de crédit. Nous téléchargerons vos coordonnées sur le serveur de Prêt immédiat ( https://www.pretimmediatpi.com ) et vos données seront protégées et resteront confidentielles."
        ),
        // 5
        Permission(
            "Compte principal",
            "Permet au djai225 de télécharger les principales autorisations dinformation de compte de lutilisateur afin deffectuer des évaluations de contrôle des risques et de vous fournir des préférences de prêt préapprouvées. Crypter et télécharger en externe ( https://www.pretimmediatpi.com ) Identifier et analyser leur comportement et les risques, ce qui aidera à prévenir la fraude."
        ),
    )
    val binder = PermissionItemBinder()
    val decoration = Decoration.linear {
        size = 15.dp.toPx(context)
    }

    val onRefuseClick = Bind.OnClick {
        callback(false, false)
        dismiss()
    }

    val onAcceptClick = Bind.OnClick {
        // 申请完后dialog消失, 需要关注结果
        this.context.requireAllPermissions { result ->
            callback(true, result)
            dismiss()
        }
    }

    override fun createBinding(inflater: LayoutInflater): ViewDataBinding {
        return DialogPermissionSecondBinding.inflate(inflater).apply {
            owner = this@PermissionSecondDialog
        }
    }
}