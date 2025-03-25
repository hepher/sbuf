package org.sbuf.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

@Aspect
@Component
@ConditionalOnExpression("${sbuf.aspect.service.enabled:true}")
public class ServiceLoggingAspect extends AbstractLoggingAspect {

    @Pointcut("within(@org.springframework.stereotype.Service *)")
    public void trackingServiceExecution() {}

    @Around("trackingServiceExecution()")
    public Object aroundServiceExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        return proceed(new LoggingAspectParameter(joinPoint, "Service"));
    }
}
