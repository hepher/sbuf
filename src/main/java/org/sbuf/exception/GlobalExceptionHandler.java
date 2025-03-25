package org.sbuf.exception;

import org.sbuf.util.ApplicationContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({ SbufException.class })
    public ResponseEntity<ExceptionResponse> customExceptionHandler(SbufException ex) {
        ExceptionResponse resp = new ExceptionResponse(ex.getErrorCode(), StringUtils.remove(ex.getMessage(), "\\"), ApplicationContextUtils.getTransactionId(), ex.getHttpStatus());
        return new ResponseEntity<>(resp, ex.getHttpStatus());
    }

    @ExceptionHandler({ Exception.class })
    public ResponseEntity<ExceptionResponse> exceptionHandler(Exception ex) {
        log.error(ex.getLocalizedMessage(), ApplicationContextUtils.getExceptionStackTrace(ex));
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        String transactionId = ApplicationContextUtils.getTransactionId();
        ExceptionResponse resp = new ExceptionResponse(null, ExceptionUtils.getRootCauseMessage(ex), transactionId, status);
        return new ResponseEntity<>(resp, status);
    }
}
