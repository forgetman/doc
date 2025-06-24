package inject.classname;

import com.squareup.javapoet.ClassName;

public interface AndroidClassName {
    ClassName ACTIVITY = ClassName.get("android.app", "Activity");
    ClassName FRAGMENT = ClassName.get("androidx.fragment.app", "Fragment");
    ClassName VIEW = ClassName.get("android.view", "View");
    ClassName CONTEXT = ClassName.get("android.content", "Context");
    ClassName INTENT = ClassName.get("android.content", "Intent");
    ClassName BUNDLE = ClassName.get("android.os", "Bundle");
    ClassName CONTEXT_COMPAT = ClassName.get("androidx.core.content", "ContextCompat");
    ClassName BUNDLE_KT = ClassName.get("androidx.core.os", "BundleKt");
    ClassName JOB_INFO = ClassName.get("android.app.job", "JobInfo");
    ClassName JOB_INFO_BUILDER = ClassName.get("android.app.job.JobInfo", "Builder");
    ClassName ACTIVITY_OPTIONS_COMPAT = ClassName.get("androidx.core.app", "ActivityOptionsCompat");
}