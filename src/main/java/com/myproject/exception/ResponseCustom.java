package com.myproject.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResponseCustom<T> {

    int errorCode;
    String message;
    private T data;

    public static <T> ResponseCustom<T> build(T data) {
        ResponseCustom<T> response = new ResponseCustom<>();
        response.data = data;
        response.errorCode = 200;
        return response;
    }

    public ResponseCustom(int errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public ResponseCustom(int errorCode, String message, T data) {
        this.errorCode = errorCode;
        this.message = message;
        this.data = data;
    }

}
