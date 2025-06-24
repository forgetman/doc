package lib.um.stats

import android.content.Context
import com.umeng.analytics.MobclickAgent
import vector.util.StatsOption


/**
 * @author yuansui
 */
class UMStats : StatsOption {

    override fun onActivityResume(context: Context, tag: String) {
        MobclickAgent.onPageStart(tag)
        MobclickAgent.onResume(context)
    }

    override fun onActivityPause(context: Context, tag: String) {
        MobclickAgent.onPageEnd(tag)
        MobclickAgent.onPause(context)
    }

    override fun onFragmentVisible(context: Context, tag: String) {
        MobclickAgent.onPageStart(tag)
    }

    override fun onFragmentInvisible(context: Context, tag: String) {
        MobclickAgent.onPageEnd(tag)
    }

    override fun onEvent(context: Context?, eventId: String, map: HashMap<String, String>?) {
        MobclickAgent.onEvent(context, eventId, map)
    }

    override fun onEventStart(context: Context?, eventId: String, map: HashMap<String, String>?) {
    }

    override fun onEventEnd(context: Context?, eventId: String, map: HashMap<String, String>?) {
    }
}
