package org.sbuf.aspect;

import org.sbuf.exception.SbufException;
import lombok.Data;
import org.aspectj.lang.ProceedingJoinPoint;

import java.util.function.BiConsumer;


@Data
public class LoggingAspectParameter {

    private ProceedingJoinPoint joinPoint;
    private JoinPointDetail detail;
    private String klassType;
    private BiConsumer<JoinPointDetail, Object> successConsumer;
    private BiConsumer<JoinPointDetail, SbufException> errorConsumer;
    private AbstractLoggingAspect.LoggingKlassStrategy loggingKlassStrategy = AbstractLoggingAspect.LoggingKlassStrategy.KLASS_METHOD;

    public LoggingAspectParameter(ProceedingJoinPoint joinPoint, String klassType) {
        this.joinPoint = joinPoint;
        this.klassType = klassType;
    }
}
