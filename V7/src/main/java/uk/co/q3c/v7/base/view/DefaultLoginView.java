package uk.co.q3c.v7.base.view;

import java.util.List;

import javax.inject.Inject;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;

import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.navigate.VerticalViewBase;
import uk.co.q3c.v7.demo.view.components.HeaderBar;

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

	@Inject
	protected DefaultLoginView(V7Navigator navigator, HeaderBar headerBar) {
		super();
		this.navigator = navigator;
		this.headerBar = headerBar;

		setSpacing(true);
		label = new Label("Please log in");

		usernameBox = new TextField("user name");
		passwordBox = new TextField("password");

		demoInfoLabel = new Label("for this demo, enter any user name, and a password of 'password'");
		demoInfoLabel2 = new Label("In a real application your Shiro Realm implementation defines how to authenticate");

		submitButton = new Button("submit");
		submitButton.addClickListener(this);

		this.addComponent(label);
		this.addComponent(demoInfoLabel);
		this.addComponent(demoInfoLabel2);
		this.addComponent(usernameBox);
		this.addComponent(passwordBox);
		this.addComponent(submitButton);
	}

	@Override
	protected void processParams(List<String> params) {
		// None to process for login
	}

	@Override
	public void buttonClick(ClickEvent event) {
		UsernamePasswordToken token = new UsernamePasswordToken(usernameBox.getValue(), passwordBox.getValue());
		SecurityUtils.getSubject().login(token);
		// an exception would be raised if login failed
		headerBar.userChanged();
		navigator.returnAfterLogin();
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

}
