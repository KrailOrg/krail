package uk.q3c.krail.option;

/**
 * Created by David Sowerby on 15 Aug 2017
 */
public class OptionPermissionException extends RuntimeException {
    public OptionPermissionException() {
    }

    public OptionPermissionException(String message) {
        super(message);
    }

    public OptionPermissionException(String message, Throwable cause) {
        super(message, cause);
    }

    public OptionPermissionException(Throwable cause) {
        super(cause);
    }

    public OptionPermissionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
