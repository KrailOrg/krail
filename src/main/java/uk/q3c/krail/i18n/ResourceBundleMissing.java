package uk.q3c.krail.i18n;

/**
 * Created by David Sowerby on 18/11/14.
 */
public class ResourceBundleMissing extends RuntimeException {
    public ResourceBundleMissing() {
    }

    public ResourceBundleMissing(String message) {
        super(message);
    }

    public ResourceBundleMissing(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceBundleMissing(Throwable cause) {
        super(cause);
    }

    public ResourceBundleMissing(String message, Throwable cause, boolean enableSuppression, boolean
            writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
