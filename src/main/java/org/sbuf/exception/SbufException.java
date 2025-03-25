package org.sbuf.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@NoArgsConstructor
public class SbufException extends RuntimeException {

    private String errorCode;
    private HttpStatus httpStatus = HttpStatus.UNPROCESSABLE_ENTITY;
    private Object systemErrorResponse;

    public SbufException(String message) {
        super(message);
    }

    public SbufException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public SbufException(ResponseMessage responseMessage) {
        super(responseMessage.message);
        this.errorCode = responseMessage.code.toString();
    }

    public SbufException(String message, String errorCode, HttpStatus httpStatus, Object systemErrorResponse) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.systemErrorResponse = systemErrorResponse;
    }
}
