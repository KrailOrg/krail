package uk.co.q3c.v7.base.shiro;

import java.io.Serializable;

import org.apache.shiro.authc.AuthenticationException;

public class DefaultAuthenticationExceptionHandler implements AuthenticationExceptionHandler, Serializable {

	@Override
	public boolean invoke(AuthenticationException exception) {
		// return false;
		throw new RuntimeException("not yet implemented");
	}

}
