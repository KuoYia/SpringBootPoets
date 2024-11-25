package clover.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

// 定义一个切面（Aspect），用于在Spring框架中添加AOP（面向切面编程）功能
@Aspect
@Component
public class LoggingAspect {

    // 定义一个切入点（Pointcut），匹配com.clover.service包及其子包下所有类的所有方法
    // 这个切入点将用于后续的前置通知（@Before）和后置通知（@AfterReturning）


    @Pointcut("execution(* clover.service.impl.*.find*(..)))")
    public void serviceLayerExecution() {
    }

    // 定义一个前置通知（Before advice），在匹配serviceLayerExecution()切入点的方法执行之前执行
    @Before("serviceLayerExecution()")
    public void beforeAdvice(JoinPoint joinPoint) {
        // 使用SimpleDateFormat定义日期格式
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 格式化当前日期和时间
        String calledTime = dateFormat.format(new Date());
        // 获取当前执行的方法名
        String methodName = joinPoint.getSignature().getName();
        // 打印日志，显示方法执行前的时间和方法名
        System.out.println("Before advice: " + calledTime + " - " + methodName);
    }

    // 定义一个后置返回通知（AfterReturning advice），在匹配serviceLayerExecution()切入点的方法执行之后，并且有返回值时执行
    @AfterReturning(pointcut = "serviceLayerExecution()", returning = "result")
    public void logAfterReturning3(JoinPoint joinPoint, Object result) {
        // 使用SimpleDateFormat定义日期格式
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 格式化当前日期和时间
        String calledTime = dateFormat.format(new Date());
        // 获取当前执行的方法所在的类的全名
        String className = joinPoint.getSignature().getDeclaringTypeName();
        // 获取当前执行的方法名
        String methodName = joinPoint.getSignature().getName();
        // 打印日志，显示方法执行后的时间、类名、方法名和返回值
        System.out.println("KuoYian结束时间:" + calledTime + " - " + className + " - " + methodName + " - Returned: " + result);
    }




}
