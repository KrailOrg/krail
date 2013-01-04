package uk.co.q3c.v7.base.shiro;

import org.apache.shiro.authc.AuthenticationException;

public class DefaultAuthenticationExceptionHandler implements AuthenticationExceptionHandler {

	@Override
	public boolean invoke(AuthenticationException exception) {
		// return false;
		throw new RuntimeException("not yet implemented");
	}

}
