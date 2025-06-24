package lib.base.util.festival;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * A thread safe wrapper to SimpleDateFormat
 *
 * @author Administrator
 */
public class ThreadSafeSimpleDateFormat {
    private ThreadLocal<SimpleDateFormat> dateFormatLocal;

    public ThreadSafeSimpleDateFormat(final String format) {
        dateFormatLocal = new ThreadLocal<SimpleDateFormat>() {
            protected SimpleDateFormat initialValue() {
                return new SimpleDateFormat(format);
            }
        };
    }

    public ThreadSafeSimpleDateFormat(final String format, final Locale locale) {
        dateFormatLocal = new ThreadLocal<SimpleDateFormat>() {
            protected SimpleDateFormat initialValue() {
                return new SimpleDateFormat(format, locale);
            }
        };
    }

    public ThreadSafeSimpleDateFormat(final String format, final TimeZone timeZone) {
        dateFormatLocal = new ThreadLocal<SimpleDateFormat>() {
            protected SimpleDateFormat initialValue() {
                SimpleDateFormat sdf = new SimpleDateFormat(format);
//				sdf.setTimeZone(timeZone);
                return sdf;
            }
        };
    }

    public String format(Date date) {
        if (date == null) {
            return "";
        }
        return dateFormatLocal.get().format(date);
    }

    public Date parse(String source) throws ParseException {
        if (source == null) {
            return null;
        }
        return dateFormatLocal.get().parse(source);
    }
}
