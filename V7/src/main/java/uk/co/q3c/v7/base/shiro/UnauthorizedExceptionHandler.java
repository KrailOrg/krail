package uk.co.q3c.v7.base.shiro;


public interface UnauthorizedExceptionHandler {
	/**
	 * Returns true if exception is handled
	 * 
	 * @param exception
	 * @return
	 */
	void invoke();

}
