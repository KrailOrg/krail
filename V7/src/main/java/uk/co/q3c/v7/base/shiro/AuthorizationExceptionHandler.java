package uk.co.q3c.v7.base.shiro;

import org.apache.shiro.authz.AuthorizationException;

public interface AuthorizationExceptionHandler {
	/**
	 * Returns true if exception is handled
	 * 
	 * @param exception
	 * @return
	 */
	boolean invoke(AuthorizationException exception);

}
