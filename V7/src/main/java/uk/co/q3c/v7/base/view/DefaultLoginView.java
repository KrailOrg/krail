package uk.co.q3c.v7.base.view;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;

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
import org.apache.shiro.subject.Subject;

import uk.co.q3c.v7.base.guice.uiscope.UIScoped;
import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.shiro.LoginExceptionHandler;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ChameleonTheme;

// TODO i18N
@UIScoped
public class DefaultLoginView extends GridViewBase implements LoginView, ClickListener {
	private final Label label;
	private final TextField usernameBox;
	private final PasswordField passwordBox;
	private final Label demoInfoLabel;
	private final Label demoInfoLabel2;
	private final Button submitButton;
	private final V7Navigator navigator;
	private final Label statusMsgLabel;
	private final LoginExceptionHandler loginExceptionHandler;
        private final Provider<Subject> subjectPro;

	@Inject
	protected DefaultLoginView(V7Navigator navigator, LoginExceptionHandler loginExceptionHandler, Provider<Subject> subjectPro) {
		super();
		this.navigator = navigator;
		this.loginExceptionHandler = loginExceptionHandler;
		this.subjectPro = subjectPro;
		this.setColumns(3);
		this.setRows(3);
		this.setSizeFull();
		Panel centrePanel = new Panel("Log in"); // TODO i18N
		centrePanel.addStyleName(ChameleonTheme.PANEL_BUBBLE);
		centrePanel.setSizeUndefined();
		VerticalLayout vl = new VerticalLayout();
		centrePanel.setContent(vl);
		vl.setSpacing(true);
		vl.setSizeUndefined();
		label = new Label("Please log in");
		usernameBox = new TextField("user name");
		passwordBox = new PasswordField("password");

		demoInfoLabel = new Label("for this demo, enter any user name, and a password of 'password'");
		demoInfoLabel2 = new Label("In a real application your Shiro Realm implementation defines how to authenticate");

		submitButton = new Button("submit");
		submitButton.addClickListener(this);

		statusMsgLabel = new Label("Please enter your username and password");

		vl.addComponent(label);
		vl.addComponent(demoInfoLabel);
		vl.addComponent(demoInfoLabel2);
		vl.addComponent(usernameBox);
		vl.addComponent(passwordBox);
		vl.addComponent(submitButton);
		vl.addComponent(statusMsgLabel);

		this.addComponent(centrePanel, 1, 1);
		this.setColumnExpandRatio(0, 1);
		this.setColumnExpandRatio(2, 1);

		this.setRowExpandRatio(0, 1);
		this.setRowExpandRatio(2, 1);

	}

	@Override
	protected void processParams(LinkedHashMap<String, String> params) {
		// None to process for login
	}

	@Override
	public void buttonClick(ClickEvent event) {
		UsernamePasswordToken token = new UsernamePasswordToken(usernameBox.getValue(), passwordBox.getValue());
		try {
		    subjectPro.get().login(token);
		} catch (UnknownAccountException uae) {
			loginExceptionHandler.unknownAccount(this, token, uae);
		} catch (IncorrectCredentialsException ice) {
			loginExceptionHandler.incorrectCredentials(this, token, ice);
		} catch (ExpiredCredentialsException ece) {
			loginExceptionHandler.expiredCredentials(this, token, ece);
		} catch (LockedAccountException lae) {
			loginExceptionHandler.accountLocked(this, token, lae);
		} catch (ExcessiveAttemptsException excess) {
			loginExceptionHandler.excessiveAttempts(this, token, excess);
		} catch (DisabledAccountException dae) {
			loginExceptionHandler.disabledAccount(this, token, dae);
		} catch (ConcurrentAccessException cae) {
			loginExceptionHandler.concurrentAccess(this, token, cae);
		} catch (AuthenticationException ae) {
			loginExceptionHandler.genericException(this, token, ae);
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

	public TextField getUsernameBox() {
		return usernameBox;
	}

	public PasswordField getPasswordBox() {
		return passwordBox;
	}

}
