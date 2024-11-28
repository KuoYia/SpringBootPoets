package clover.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@Aspect
@Component
public class TransactionAspect {

    private static final Logger logger = LoggerFactory.getLogger(TransactionAspect.class);
    // 自动注入事务管理器
    @Autowired
    private PlatformTransactionManager transactionManager;
    // 定义一个切入点，匹配所有使用@TransactionalRead注解的方法
    @Pointcut("@annotation(clover.TransactionalRead)")
    public void transactionalRead() {}
    /**
     * 管理事务的方法，使用@Around通知在匹配的方法执行前后进行事务的开启、提交或回滚
     * @param joinPoint 连接点，表示正在执行的方法
     * @return 方法执行结果
     * @throws Throwable 可能抛出的异常
     */
    @Around("transactionalRead()")
    public Object manageTransaction(ProceedingJoinPoint joinPoint) throws Throwable {
        logger.info("开始事务");
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            Object result = joinPoint.proceed();
            transactionManager.commit(status);
            logger.info("事务提交成功");
            return result;
        } catch (Throwable e) {
            transactionManager.rollback(status);
            logger.info("事务回滚");
            throw e;
        }
    }
}
