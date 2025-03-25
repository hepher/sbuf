package org.sbuf.exception;

import org.sbuf.model.dto.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
public class ExceptionResponse extends AbstractEntity {

    private String errorCode;
    private String message;
    private String transactionId;
    private HttpStatus httpStatus;
}
