package uk.co.q3c.v7.base.navigate;

import java.util.List;

import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.ExpiredCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;

import uk.co.q3c.v7.base.shiro.LoginExceptionHandler;
import uk.co.q3c.v7.base.view.LoginView;
import uk.co.q3c.v7.base.view.V7ViewChangeListener;

import com.vaadin.server.Page.UriFragmentChangedListener;

public interface V7Navigator extends UriFragmentChangedListener {

	void navigateTo(String navigationState);

	String getNavigationState();

	List<String> geNavigationParams();

	void addViewChangeListener(V7ViewChangeListener listener);

	void removeViewChangeListener(V7ViewChangeListener listener);

	/**
	 * Usually called from {@link LoginExceptionHandler}, probably because the user has exceeding login attempts.
	 * Typically, the implementation of this method will navigate to a V7View which allows the user to request a reset
	 * after filling in appropriate security answers.
	 * 
	 * @see ExcessiveAttemptsException
	 * @param token
	 */
	void requestAccountReset(UsernamePasswordToken token);

	/**
	 * Usually called from {@link LoginExceptionHandler} to indicate that the credentials offered during authentication
	 * were valid but have expired. Typically, the implementation of this method will navigate to a V7View which allows
	 * the user to update their password.
	 * 
	 * @see ExpiredCredentialsException
	 * @param token
	 */
	void requestAccountRefresh(UsernamePasswordToken token);

	/**
	 * Usually called from {@link LoginExceptionHandler}. Typically, the implementation of this method will navigate to
	 * a V7View which allows the user to request that their account is unlocked, although it may also just inform the
	 * user and not invoke the navigator at all.
	 * 
	 * @see LockedAccountException
	 * @param token
	 */
	void requestAccountUnlock(UsernamePasswordToken token);

	/**
	 * Usually called from {@link LoginExceptionHandler}. Typically, the implementation of this method will navigate to
	 * a V7View which allows the user to request that their account is re-enabled, although it may also just inform the
	 * user and not invoke the navigator at all.
	 * 
	 * @see DisabledAccountException
	 * @param token
	 */
	void requestAccountEnable(UsernamePasswordToken token);

	/**
	 * Presents a login view to the user. If the login is successful, the {@link LoginView} must call
	 * {@link #returnAfterLogin()} to complete the process
	 */
	// void login();

	/**
	 * A signal to the navigator that a login has been successful. The implementation defines which view should be
	 * switched to, but typically the view is changed from the {@link LoginView} to the one the user was at before
	 * requesting a log in, or to a "landing page" view.
	 */
	void returnAfterLogin();

	/**
	 * Log the current Subject out, and navigate to a logout view
	 */
	void logout();

}
