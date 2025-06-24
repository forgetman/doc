package pretimmediat.dialog

import android.content.Context
import android.graphics.Color
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.method.LinkMovementMethodCompat
import androidx.databinding.ViewDataBinding
import kotlinx.coroutines.flow.MutableStateFlow
import pretimmediat.R
import pretimmediat.activity.WebViewActivityCreator
import pretimmediat.adapter.PermissionItemBinder
import pretimmediat.databinding.DialogPermissionBinding
import pretimmediat.ext.requireAllPermissions
import pretimmediat.ext.setClickableSpan
import pretimmediat.model.Permission
import pretimmediat.network.URL
import pretimmediat.widget.CenterAlignImageSpan
import vector.app.databinding.dialog.DBDialogEx
import vector.app.os.dimenRes
import vector.bindingadapter.bind.Bind
import vector.ext.getStringForLanguage
import vector.app.os.dp
import vector.util.LayoutParamsFactory
import vector.util.MATCH_PARENT
import vector.app.util.toColor
import vector.widget.scrollable.decoration.Decoration

/**
 * 权限申请弹窗(全部权限)
 */
class PermissionDialog(
    context: Context?,
    private val callback: (allowApply: Boolean) -> Unit
) : DBDialogEx(context) {

    override val params: ViewGroup.LayoutParams
        get() = LayoutParamsFactory.viewGroup(MATCH_PARENT, MATCH_PARENT)

    override val marginStart: Int
        get() = R.dimen.margin_horizontal.dimenRes.toPx(context)

    override val marginEnd: Int
        get() = R.dimen.margin_horizontal.dimenRes.toPx(context)

    override val marginBottom: Int
        get() = 22.dp.toPx(context)

    val data = listOf(
        // 1
        Permission(
            "Enregistrement des appels",
            "Une fois que vous nous aurez autorisés à consulter les journaux d'appels, nous vous inviterons à passer un appel vocal. La raison de la collecte des journaux d'appels est de confirmer que notre application est installée sur votre propre téléphone et que vous recevrez un code d'accès dynamique de notre part, qui sera ensuite vérifié sur votre appareil. Enfin, nous consulterons l'historique de vos appels pour voir un enregistrement de la vérification dynamique du mot de passe envoyée par notre application. Nous ne téléchargerons et ne transférerons le contenu des journaux d'appels vers Prêt immédiat ( https://www.pretimmediatpi.com ) dans un environnement réseau sécurisé qu'avec votre consentement explicite, et nous ne partagerons jamais ces données avec des tiers."
        ),
        // 2
        Permission(
            "Fichier",
            "Afin de vous fournir des applications et des services mobiles, ou pour nous conformer à des obligations légales, nous pouvons avoir besoin de collecter et d'utiliser certaines informations personnelles. Votre fichier privé sera uniquement téléchargé sur le serveur Prêt immédiat ( https://www.pretimmediatpi.com ). En aucun cas nous ne partagerons ces données avec des tiers."
        ),
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
        // 6
        Permission(
            "Images",
            "Lorsque l'utilisateur sélectionne une photo dans l'album photo du téléphone, l'application doit accéder à la galerie dans les fichiers multimédias de l'appareil. Cet accès vous permet de sélectionner et de télécharger facilement les images requises dans votre demande de prêt. Seules les photos sélectionnées manuellement par l'utilisateur seront transférées vers notre application de manière cryptée et les données collectées seront téléchargées en toute sécurité vers le centre de sécurité Prêt immédiat ( https://www.pretimmediatpi.com ) sont stockées de manière cryptée et conservent la date de publication et la date de téléchargement pendant 1 an à compter de la date de publication. Nous ne collecterons jamais vos photos sans votre consentement, si vous souhaitez supprimer les données des photos, vous pouvez envoyer une demande au service clientèle, nous pouvons les supprimer manuellement dans les 3-5 jours ouvrables."
        ),
        // 7
        Permission(
            "Applications installées",
            "Surveillez et collectez le contenu des applications installées : le nom de lapplication, le nom du package d;application, lheure d;installation de lapplication, lheure d;installation de lapplication et les données des applications installées seront cryptées et téléchargées sur notre serveur via le protocole HTTPS. ( https://www.pretimmediatpi.com ) Nous ne partagerons jamais ces données avec des tiers."
        ),
        // 8
        Permission(
            "Téléphone portable",
            "Collectez et surveillez certaines informations sur votre appareil, y compris le nom, le modèle, la région, la langue, les identifiants, les informations sur le matériel et les logiciels, létat, les habitudes d;utilisation, les identifiants uniques (tels que lIMEI et le numéro de série). Il est uniquement utilisé pour identifier votre appareil et s;assurer qu;il ne peut pas être utilisé sans votre autorisation et pour prévenir les fraudes. De plus, nous collecterons une liste détaillée de vos applications installées pour évaluer votre propension à emprunter et votre situation d;endettement, et nous téléchargerons les données collectées sur notre serveur hautement sécurisé Prêt immédiat ( https://www.pretimmediatpi.com )."
        ),
        // 9
        Permission(
            "Place",
            "Collectez et surveillez les informations sur les emplacements des appareils pour lévaluation et la notation des risques. Nous téléchargerons les données de localisation sur notre serveur Prêt immédiat ( https://www.pretimmediatpi.com ), vos données seront protégées et confidentielles."
        ),
        // 10
        Permission(
            "Calendrier",
            "Il sert à vous rappeler la date de remboursement et à éviter limpact d;un retard de remboursement sur votre crédit. Nous téléchargerons les données de localisation sur le serveur pour la comparaison des prêts ( https://www.pretimmediatpi.com ), et vos données seront protégées et gardées confidentielles."
        ),
        // 11
        Permission(
            "Caméra",
            "Veuillez utiliser votre appareil photo pour prendre les documents et/ou les photos nécessaires au processus de candidature et d;évaluation. ( https://www.pretimmediatpi.com ) Vos données seront protégées et gardées confidentielles."
        ),
        // 12
        Permission(
            "Stockage de données",
            "Pour toutes les informations collectées, nous les stockerons sur le serveur de Prêt immédiat ( https://www.pretimmediatpi.com ), qui est hautement protégé et ne sera partagé avec aucun tiers."
        ),
        // 13
        Permission(
            "Stockage",
            "laccès au stockage de votre appareil nécessite votre autorisation, ce qui vous permettra de télécharger des photos et/ou des documents pour remplir le formulaire de demande lors du processus de demande de prêt. Les données seront cryptées et téléchargées sur notre serveur de membre ( https://www.pretimmediatpi.com ) via le protocole HTTPS. Vos données seront protégées et gardées confidentielles. .Nous ne partagerons jamais ces données avec des tiers."
        ),
    )
    val binder = PermissionItemBinder()
    val decoration = Decoration.linear {
        color = Color.TRANSPARENT
        size = 15.dp.toPx(context)
    }

    val protocol = MutableStateFlow<CharSequence?>(null)
    val movementMethod = LinkMovementMethodCompat.getInstance()

    val onRefuseClick = Bind.OnClick {
        callback(false)
        dismiss()
    }

    val onAcceptClick = Bind.OnClick {
        // 申请完后dialog消失, 不用管申请结果如何
        this.context.requireAllPermissions {
            callback(true)
            dismiss()
        }
    }

    override fun createBinding(inflater: LayoutInflater): ViewDataBinding {
        return DialogPermissionBinding.inflate(inflater).apply {
            owner = this@PermissionDialog
        }
    }

    override fun initializeContentView() {
        val partIcon = context.getString(R.string.permission_protocol_part_icon)
        val part1 = context.getStringForLanguage(R.string.permission_protocol_part1)
        val part2 = context.getStringForLanguage(R.string.permission_protocol_part2)
        protocol.value = SpannableStringBuilder().apply {
            append(partIcon)
            append(part1)
            append(part2)

            val span = CenterAlignImageSpan(context, R.drawable.permission_ic_alert)
            setSpan(span, 0, partIcon.length, SpannableString.SPAN_INCLUSIVE_EXCLUSIVE)

            setClickableSpan(
                partIcon.length + part1.length,
                length,
                R.color.blue.toColor(context)
            ) {
                WebViewActivityCreator.create().url(URL.PROTOCOL).titleId(R.string.protocol_title)
                    .start(context)
            }
        }
    }

}