package pretimmediat.stats

import android.annotation.SuppressLint
import android.app.Application

/**
 * @author yuansui
 * @since 2024/7/18
 */
@SuppressLint("StaticFieldLeak")
object Stats {
    val faceBook = FaceBookStats()
    val firebase = FirebaseStats()
    val flyer = AppsFlyerStats()
    val public = PublicStats()
    val risk = RiskStats()

    fun init(app: Application) {
        faceBook.init(app)
        firebase.init(app)
        flyer.init(app)
        public.init(app)
        risk.init(app)
    }
}