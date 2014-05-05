package uk.co.q3c.v7.base.view;

import java.util.List;

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

import uk.co.q3c.util.ID;
import uk.co.q3c.v7.base.shiro.LoginExceptionHandler;
import uk.co.q3c.v7.base.shiro.SubjectProvider;
import uk.co.q3c.v7.base.user.status.UserStatus;
import uk.co.q3c.v7.i18n.DescriptionKey;
import uk.co.q3c.v7.i18n.I18N;
import uk.co.q3c.v7.i18n.I18NKey;
import uk.co.q3c.v7.i18n.I18NValue;
import uk.co.q3c.v7.i18n.LabelKey;
import uk.co.q3c.v7.i18n.Translate;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ChameleonTheme;

public class DefaultLoginView extends GridViewBase implements LoginView, ClickListener {
	@I18NValue(value = LabelKey.Authentication)
	private Label label;
	@I18N(caption = LabelKey.User_Name, description = DescriptionKey.Enter_your_user_name)
	private TextField usernameBox;
	private PasswordField passwordBox;
	private Label demoInfoLabel;
	private Label demoInfoLabel2;
	private Button submitButton;
	private Label statusMsgLabel;
	private final LoginExceptionHandler loginExceptionHandler;
	private final Provider<Subject> subjectProvider;
	private final Translate translate;
	private final UserStatus userStatus;

	@Inject
	protected DefaultLoginView(LoginExceptionHandler loginExceptionHandler, SubjectProvider subjectProvider,
			Translate translate, UserStatus userStatus) {
		super();
		this.loginExceptionHandler = loginExceptionHandler;
		this.subjectProvider = subjectProvider;
		this.translate = translate;
		this.userStatus = userStatus;
		buildView();
	}

	@Override
	public void setIds() {
		setId(ID.getId(this));
		submitButton.setId(ID.getId(this, submitButton));
		usernameBox.setId(ID.getId("username", this, usernameBox));
		passwordBox.setId(ID.getId("password", this, passwordBox));
		statusMsgLabel.setId(ID.getId("status", this, statusMsgLabel));
	}

	@Override
	protected void processParams(List<String> params) {
		// None to process for login
	}

	@Override
	public void buttonClick(ClickEvent event) {
		UsernamePasswordToken token = new UsernamePasswordToken(usernameBox.getValue(), passwordBox.getValue());
		try {
			subjectProvider.get().login(token);
			userStatus.statusChanged();
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

	public TextField getUsernameBox() {
		return usernameBox;
	}

	public PasswordField getPasswordBox() {
		return passwordBox;
	}

	@Override
	public String viewName() {

		return getClass().getSimpleName();
	}

	protected void buildView() {
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
		label = new Label();
		usernameBox = new TextField();
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
	public void setStatusMessage(I18NKey<?> messageKey) {
		setStatusMessage(translate.from(messageKey));
	}

}
