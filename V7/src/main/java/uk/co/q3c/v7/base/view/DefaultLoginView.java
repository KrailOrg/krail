package uk.co.q3c.v7.base.view;

import java.util.List;

import javax.inject.Inject;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.ConcurrentAccessException;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.ExpiredCredentialsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;

import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.shiro.LoginExceptionHandler;
import uk.co.q3c.v7.base.view.components.HeaderBar;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

// TODO i18N
public class DefaultLoginView extends VerticalViewBase implements LoginView, ClickListener {
	private final Label label;
	private final TextField usernameBox;
	private final TextField passwordBox;
	private final Label demoInfoLabel;
	private final Label demoInfoLabel2;
	private final Button submitButton;
	private final V7Navigator navigator;
	private final HeaderBar headerBar;
	private final Label statusMsgLabel;
	private final LoginExceptionHandler loginExceptionHandler;

	@Inject
	protected DefaultLoginView(V7Navigator navigator, HeaderBar headerBar, LoginExceptionHandler loginExceptionHandler) {
		super();
		this.navigator = navigator;
		this.headerBar = headerBar;
		this.loginExceptionHandler = loginExceptionHandler;

		setSpacing(true);
		label = new Label("Please log in");

		usernameBox = new TextField("user name");
		passwordBox = new TextField("password");

		demoInfoLabel = new Label("for this demo, enter any user name, and a password of 'password'");
		demoInfoLabel2 = new Label("In a real application your Shiro Realm implementation defines how to authenticate");

		submitButton = new Button("submit");
		submitButton.addClickListener(this);

		statusMsgLabel = new Label("Please enter your username and password");

		this.addComponent(label);
		this.addComponent(demoInfoLabel);
		this.addComponent(demoInfoLabel2);
		this.addComponent(usernameBox);
		this.addComponent(passwordBox);
		this.addComponent(submitButton);
		this.addComponent(statusMsgLabel);
	}

	@Override
	protected void processParams(List<String> params) {
		// None to process for login
	}

	@Override
	public void buttonClick(ClickEvent event) {
		UsernamePasswordToken token = new UsernamePasswordToken(usernameBox.getValue(), passwordBox.getValue());
		try {
			SecurityUtils.getSubject().login(token);
			navigator.loginSuccessFul();
		} catch (UnknownAccountException uae) {
			loginExceptionHandler.unknownAccount(this, token);
		} catch (IncorrectCredentialsException ice) {
			loginExceptionHandler.incorrectCredentials(this, token);
		} catch (ExpiredCredentialsException ece) {
			loginExceptionHandler.expiredCredentials(this, token);
		} catch (LockedAccountException lae) {
			loginExceptionHandler.accountLocked(this, token);
		} catch (ExcessiveAttemptsException excess) {
			loginExceptionHandler.excessiveAttempts(this, token);
		} catch (DisabledAccountException dae) {
			loginExceptionHandler.disabledAccount(this, token);
		} catch (ConcurrentAccessException cae) {
			loginExceptionHandler.concurrentAccess(this, token);
		} catch (AuthenticationException ae) {
			loginExceptionHandler.disabledAccount(this, token);
		}
		// unexpected condition - error?
		// an exception would be raised if login failed
	}

	@Override
	public void setUsername(String username) {
		usernameBox.setValue(username);
	}

	@Override
	public void setPassword(String password) {
		passwordBox.setValue(password);
	}

	@Override
	public Button getSubmitButton() {
		return submitButton;
	}

	@Override
	public String getStatusMessage() {
		return statusMsgLabel.getValue();
	}

	@Override
	public void setStatusMessage(String msg) {
		statusMsgLabel.setValue(msg);
	}

}
