package aop.util

import aop.AspectConst
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.Signature
import org.aspectj.lang.reflect.CodeSignature
import org.aspectj.lang.reflect.MethodSignature
import java.util.*

/**
 * @author yuansui
 * @since 2018/6/19
 */
internal object Util {
    /**
     * 根据分隔符将List转换为String
     *
     * @param list
     * @param separator
     * @return
     */
    fun listToString(list: List<String?>?, separator: String? = ","): String {
        if (list == null || list.isEmpty()) {
            return ""
        }

        val sb = StringBuilder()
        for (i in list.indices) {
            sb.append(list[i]).append(separator)
        }
        return sb.toString().substring(0, sb.toString().length - 1)
    }

    fun getClassName(cls: Class<*>): String {
        return if (cls.isAnonymousClass) {
            getClassName(Objects.requireNonNull(cls.enclosingClass))
        } else cls.simpleName
    }

    /**
     * 获取方法的描述信息
     *
     * @param joinPoint
     * @return
     */
    fun getMethodDescribeInfo(joinPoint: ProceedingJoinPoint): String {
        val codeSignature = joinPoint.signature as CodeSignature
        val cls = codeSignature.declaringType //方法所在类
        val methodName = codeSignature.name //方法名
        return getClassName(cls) + AspectConst.ARROW + methodName
    }

    /**
     * 获取简约的方法名
     *
     * @param joinPoint
     * @return
     */
    fun getMethodName(joinPoint: ProceedingJoinPoint): String {
        val codeSignature = joinPoint.signature as CodeSignature
        val cls = codeSignature.declaringType //方法所在类
        val methodName = codeSignature.name //方法名
        return getClassName(cls) + "." + methodName
    }

    /**
     * 方法是否有返回值
     *
     * @param signature
     * @return
     */
    fun isHasReturnType(signature: Signature): Boolean {
        return signature is MethodSignature && signature.returnType != Void.TYPE
    }
}