package uk.co.q3c.basic;

public class MethodReconfigured extends RuntimeException {

	protected MethodReconfigured() {
		super();
	}

	protected MethodReconfigured(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	protected MethodReconfigured(String message, Throwable cause) {
		super(message, cause);
	}

	protected MethodReconfigured(String message) {
		super(message);
	}

	protected MethodReconfigured(Throwable cause) {
		super(cause);
	}

}
