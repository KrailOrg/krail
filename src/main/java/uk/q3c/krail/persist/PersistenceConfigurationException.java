package uk.q3c.krail.persist;

/**
 * Created by David Sowerby on 15 Aug 2017
 */
public class PersistenceConfigurationException extends RuntimeException {
    public PersistenceConfigurationException() {
    }

    public PersistenceConfigurationException(String message) {
        super(message);
    }

    public PersistenceConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public PersistenceConfigurationException(Throwable cause) {
        super(cause);
    }

    public PersistenceConfigurationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
