package com.backend.domain.chat.exception;



import com.backend.global.exception.GlobalErrorCode;
import lombok.Getter;

@Getter
public class WebSocketException extends RuntimeException {

    private static final GlobalErrorCode ERROR_CODE = GlobalErrorCode.EXCEPTION_IN_WEBSOCKET;
    private static final String MESSAGE_KEY = "exception.websocket";
    private final String message;

    public WebSocketException(String message) {
        this.message = message;
    }
}
