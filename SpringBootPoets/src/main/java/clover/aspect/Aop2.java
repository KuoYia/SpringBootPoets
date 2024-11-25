package clover.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import clover.model.OperationLog;
import clover.service.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Aspect
@Component
public class Aop2 {

    private static final Logger logger = LoggerFactory.getLogger(Aop2.class);
    public static final String LOG_ID_KEY = "logId";

    @Autowired
    private LogService logService;

    @Pointcut("execution(* clover.service.impl.*.*(..))")
    public void serviceLayerExecution() {}

    @Before("serviceLayerExecution()")
    public void beforeAdvice(JoinPoint joinPoint) {
        // 获取或生成logId
        String logId = MDC.get(LOG_ID_KEY);
        if (logId == null || logId.isEmpty()) {
            logId = UUID.randomUUID().toString();
            MDC.put(LOG_ID_KEY, logId);
        }

        OperationLog operationLog = new OperationLog();
        operationLog.setOperationTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        operationLog.setClassName(joinPoint.getSignature().getDeclaringTypeName());
        operationLog.setMethodName(joinPoint.getSignature().getName());
        operationLog.setMethodArgs(joinPoint.getArgs().toString());
        operationLog.setUserId(logId); // 假设用户唯一标识为logId

        // 保存操作日志到数据库
        logService.saveOperationLog(operationLog);
    }
}