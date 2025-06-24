package inject.classname;

import com.squareup.javapoet.ClassName;

public interface AnnotationClassName {
    ClassName NON_NULL = ClassName.get("androidx.annotation", "NonNull");
    ClassName NULLABLE = ClassName.get("androidx.annotation", "Nullable");
}