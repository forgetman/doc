package logger;

/**
 * java专用Log(临时)
 *
 * @author yuansui
 * @since 2022/3/15
 */
@SuppressWarnings("unused")
public class JvmL {

    public static void d(String msg) {
        L.INSTANCE.d(null, msg);
    }

    public static void d(String tag, String msg) {
        L.INSTANCE.d(tag, msg);
    }

    public static void w(String msg) {
        L.INSTANCE.w(null, msg);
    }

    public static void w(String tag, String msg) {
        L.INSTANCE.w(tag, msg);
    }

    public static void v(String msg) {
        L.INSTANCE.v(null, msg);
    }

    public static void v(String tag, String msg) {
        L.INSTANCE.v(tag, msg);
    }

    public static void www(String msg) {
        L.INSTANCE.www(msg);
    }

    public static void i(String msg) {
        L.INSTANCE.i(null, msg);
    }

    public static void i(String tag, String msg) {
        L.INSTANCE.i(tag, msg);
    }

    public static void e(String msg) {
        L.INSTANCE.e(null, msg);
    }

    public static void e(String tag, String msg, Throwable tr) {
        L.INSTANCE.e(tag, msg, tr);
    }

    public static void e(String tag, String msg) {
        L.INSTANCE.e(tag, msg);
    }

    public static void e(Throwable tr) {
        L.INSTANCE.e(null, tr);
    }

    public static void e(String tag, Throwable tr) {
        L.INSTANCE.e(tag, tr);
    }
}
