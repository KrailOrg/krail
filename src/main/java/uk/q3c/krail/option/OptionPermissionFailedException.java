package uk.q3c.krail.option;

/**
 * Created by David Sowerby on 15 Aug 2017
 */
public class OptionPermissionFailedException extends RuntimeException {

    public OptionPermissionFailedException(String message) {
        super(message);
    }

    public OptionPermissionFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public OptionPermissionFailedException(Throwable cause) {
        super(cause);
    }

    public OptionPermissionFailedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
