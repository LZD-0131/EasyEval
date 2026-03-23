package com.dong.easyeval.common;

public class WxOAuthCodeException extends RuntimeException {

    public WxOAuthCodeException(String message) {
        super(message);
    }

    public WxOAuthCodeException(String message, Throwable cause) {
        super(message, cause);
    }
}
