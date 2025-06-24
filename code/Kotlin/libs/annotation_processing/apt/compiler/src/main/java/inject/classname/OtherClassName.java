package inject.classname;

import com.squareup.javapoet.ClassName;

/**
 * @author yuansui
 * @since 2019/11/18
 */
public interface OtherClassName {
    ClassName TYPE_TOKEN = ClassName.get("com.google.gson.reflect", "TypeToken");
    ClassName TYPE = ClassName.get("java.lang.reflect", "Type");
    ClassName EXCEPTION = ClassName.get("java.lang", "Exception");
}
