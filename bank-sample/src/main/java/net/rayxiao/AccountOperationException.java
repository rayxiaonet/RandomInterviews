package net.rayxiao;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by rxiao on 10/31/16.
 * <p>
 * Use this error for account operation issue
 * The server understood the request, but is refusing to fulfill it.
 */

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class AccountOperationException extends RuntimeException {
    public AccountOperationException(String msg) {
        super(msg);

    }

}
