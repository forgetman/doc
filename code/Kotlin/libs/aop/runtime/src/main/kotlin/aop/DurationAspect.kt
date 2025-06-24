package aop

import android.util.Log
import aop.annotation.Duration
import aop.util.Interval
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.MethodSignature

/**
 * 根据方法, 打印消耗的时间
 *
 * @author yuansui
 * @since 2018/5/11
 */
@Aspect
class DurationAspect {
    @Pointcut(AspectConst.START + "Duration" + AspectConst.END_METHOD)
    fun methodCut() {
    }

    @Pointcut(AspectConst.START + "Duration" + AspectConst.END_CONSTRUCTOR)
    fun constructorCut() {
    }

    @Around("methodCut() || constructorCut()")
    @Throws(Throwable::class)
    fun durationMethod(joinPoint: ProceedingJoinPoint): Any? {
        val methodSignature = joinPoint.signature as MethodSignature
        val duration = methodSignature.method.getAnnotation(
            Duration::class.java
        )
        if (duration != null && !duration.enable) {
            return joinPoint.proceed()
        }
        var className = methodSignature.declaringType.simpleName
        val methodName = methodSignature.name
        val interval = Interval()
        interval.start()
        val result = joinPoint.proceed()
        interval.stop()
        if (className.isEmpty()) {
            className = "Anonymous class"
        }
        Log.d(className, AspectConst.ARROW + methodName + "(), duration[${interval.elapsedTime}ms]")
        return result
    }
}