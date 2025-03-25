package org.sbuf.exception;

public enum ResponseMessage {

    GENERIC_ERROR_CODE(500, "Error"),
    ENCRYPTION_ERROR(501, "Encryption error");

    public final Integer code;
    public final String message;

    ResponseMessage(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
