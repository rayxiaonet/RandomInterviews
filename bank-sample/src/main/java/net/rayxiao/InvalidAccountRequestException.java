package net.rayxiao;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by rxiao on 11/1/16.
 * <p>
 * Use this error for invalid requests
 * The request could not be understood by the server due to malformed syntax
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)  // 400

public class InvalidAccountRequestException extends RuntimeException {
    public InvalidAccountRequestException(String msg) {
        super(msg);

    }
}
