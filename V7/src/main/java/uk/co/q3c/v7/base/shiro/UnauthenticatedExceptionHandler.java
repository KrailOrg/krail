package uk.co.q3c.v7.base.shiro;


public interface UnauthenticatedExceptionHandler {

	/**
	 * Returns true if exception is handled
	 * 
	 * @param t
	 * @return
	 */
	void invoke();

}
