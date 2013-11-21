package uk.co.q3c.util;

public class CycleDetectedException extends RuntimeException {

	public CycleDetectedException() {
		super();

	}

	public CycleDetectedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);

	}

	public CycleDetectedException(String message, Throwable cause) {
		super(message, cause);

	}

	public CycleDetectedException(String message) {
		super(message);

	}

	public CycleDetectedException(Throwable cause) {
		super(cause);

	}

}
