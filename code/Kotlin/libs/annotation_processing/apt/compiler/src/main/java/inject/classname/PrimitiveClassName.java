package inject.classname;

import com.squareup.javapoet.ClassName;

/**
 * @author yuansui
 * @since 2017/8/4
 */

public interface PrimitiveClassName {
    ClassName STRING = ClassName.get(String.class);
    ClassName VOID = ClassName.get(Void.class);
    ClassName BOOLEAN = ClassName.get(Boolean.class);
    ClassName BYTE = ClassName.get(Byte.class);
    ClassName SHORT = ClassName.get(Short.class);
    ClassName INT = ClassName.get(Integer.class);
    ClassName LONG = ClassName.get(Long.class);
    ClassName CHAR = ClassName.get(CharSequence.class);
    ClassName FLOAT = ClassName.get(Float.class);
    ClassName DOUBLE = ClassName.get(Double.class);
    ClassName OBJECT = ClassName.get(Object.class);
}
