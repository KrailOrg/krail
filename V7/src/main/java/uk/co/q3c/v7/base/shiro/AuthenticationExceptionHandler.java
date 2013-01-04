package uk.co.q3c.v7.base.shiro;

import org.apache.shiro.authc.AuthenticationException;

public interface AuthenticationExceptionHandler {

	/**
	 * Returns true if exception is handled
	 * 
	 * @param exception
	 * @return
	 */
	boolean invoke(AuthenticationException exception);

}
