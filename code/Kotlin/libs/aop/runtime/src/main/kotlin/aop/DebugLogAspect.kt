package aop

import android.os.Looper
import android.os.Trace
import android.util.Log
import aop.annotation.DebugLog
import aop.util.Util
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.CodeSignature
import java.util.concurrent.TimeUnit

@Aspect
class DebugLogAspect {

    @Pointcut("within(@aop.annotation.DebugLog *)")
    fun withinAnnotatedClass() {
    }

    @Pointcut(AspectConst.START_SYNTHETIC + "withinAnnotatedClass()")
    fun methodInsideAnnotatedType() {
    }

    @Pointcut("execution(!synthetic *.new(..)) && withinAnnotatedClass()")
    fun constructorInsideAnnotatedType() {
    }

    /**
     * 方法切入点
     */
    @Pointcut(AspectConst.START + "DebugLog * *(..)) || methodInsideAnnotatedType()")
    fun method() {
    }

    /**
     * /构造器切入点
     */
    @Pointcut(AspectConst.START + "DebugLog *.new(..)) || constructorInsideAnnotatedType()")
    fun constructor() {
    }

    @Around("(method() || constructor()) && @annotation(debugLog)")
    @Throws(Throwable::class)
    fun logAndExecute(joinPoint: ProceedingJoinPoint, debugLog: DebugLog): Any? {
        enterMethod(joinPoint)
        val startNanos = System.nanoTime()
        val result = joinPoint.proceed()
        val stopNanos = System.nanoTime()
        val lengthMillis = TimeUnit.NANOSECONDS.toMillis(stopNanos - startNanos)
        exitMethod(joinPoint, result, lengthMillis)
        return result
    }

    /**
     * 方法执行前切入
     *
     * @param joinPoint
     */
    private fun enterMethod(joinPoint: ProceedingJoinPoint) {
        val codeSignature = joinPoint.signature as CodeSignature
        val cls = codeSignature.declaringType //方法所在类
        val methodName = codeSignature.name //方法名
        val parameterNames = codeSignature.parameterNames //方法参数名集合
        val parameterValues = joinPoint.args //方法参数集合

        //记录并打印方法的信息
        val builder = getMethodLogInfo(methodName, parameterNames, parameterValues)
        val message = builder.toString()
        Log.d(Util.getClassName(cls), message)
        val section = message.substring(2)
        Trace.beginSection(section)
    }

    /**
     * 获取方法的日志信息
     *
     * @param methodName      方法名
     * @param parameterNames  方法参数名集合
     * @param parameterValues 方法参数值集合
     * @return
     */
    private fun getMethodLogInfo(
        methodName: String?,
        parameterNames: Array<String?>,
        parameterValues: Array<Any?>
    ): StringBuilder {
        val builder = StringBuilder(AspectConst.ARROW)
        builder.append(methodName).append('(')
        for (i in parameterValues.indices) {
            if (i > 0) {
                builder.append(AspectConst.SPLIT)
            }
            builder.append(parameterNames[i]).append('=')
            builder.append(parameterValues[i])
        }
        builder.append(')')
        if (Looper.myLooper() != Looper.getMainLooper()) {
            builder.append(" [Thread:\"").append(Thread.currentThread().name).append("\"]")
        }
        return builder
    }

    /**
     * 方法执行完毕，切出
     *
     * @param joinPoint
     * @param result       方法执行后的结果
     * @param lengthMillis 执行方法所需要的时间
     */
    private fun exitMethod(
        joinPoint: ProceedingJoinPoint,
        result: Any?,
        lengthMillis: Long
    ) {
        Trace.endSection()

        val signature = joinPoint.signature
        val cls = signature.declaringType
        val methodName = signature.name
        val hasReturnType = Util.isHasReturnType(signature)

        Log.d(Util.getClassName(cls), buildString {
            append(AspectConst.ARROW)
            append("$methodName() end")
            append(AspectConst.SPLIT)
            append("duration[${lengthMillis}ms]")
            if (hasReturnType) {
                append(AspectConst.BLANK)
                append("return[$result]")
            }
        })
    }
}