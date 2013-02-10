package uk.co.q3c.v7.demo.view;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;

import uk.co.q3c.v7.base.view.V7ViewChangeEvent;
import uk.co.q3c.v7.base.view.VerticalViewBase;
import uk.co.q3c.v7.base.view.components.FooterBar;
import uk.co.q3c.v7.base.view.components.HeaderBar;

import com.google.common.collect.Lists;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ChameleonTheme;
import com.vaadin.util.CurrentInstance;

public abstract class DemoViewBase extends VerticalViewBase implements ClickListener {

	private VerticalLayout panelLayout;
	private Label viewLabel;
	private final FooterBar footerBar;
	private final GridLayout grid;
	private Button sendMsgButton;
	private TextField textField;
	private ArrayList<String> params;
	private final HeaderBar headerBar;
	private final Button authorisationButton;
	private final Button disableIfNotAuthorisedButton;
	private final Button authenticationButton;

	protected static final String home = "";
	protected static final String view1 = "secure/view1";
	protected static final String view2 = "public/view2";

	@Inject
	protected DemoViewBase(FooterBar footerBar, HeaderBar headerBar) {
		super();
		this.footerBar = footerBar;
		this.headerBar = headerBar;

		grid = new GridLayout(3, 3);
		grid.addComponent(centrePanel(), 1, 1);
		grid.addComponent(viewLabel(), 1, 0);

		grid.setSizeFull();

		grid.setColumnExpandRatio(0, 1);
		grid.setColumnExpandRatio(1, 1);
		grid.setColumnExpandRatio(2, 1);

		grid.setRowExpandRatio(0, 0.1f);
		grid.setRowExpandRatio(1, 1.5f);
		grid.setRowExpandRatio(2, 0.5f);

		getGrid().addComponent(msgPanel(), 2, 1);
		getGrid().addComponent(linkPanel(), 0, 1);

		authorisationButton = new Button("authorisation button");
		authorisationButton.setImmediate(true);
		authorisationButton.addClickListener(this);
		authorisationButton
				.setDescription("This button should throw an authorisation exception if you are logged in, or an authentication exception if you are not logged in");
		authorisationButton.setWidth("100%");
		addToCentrePanel(authorisationButton);

		authenticationButton = new Button("trigger a method which requires authentication");
		authenticationButton.setImmediate(true);
		authenticationButton.addClickListener(this);
		authenticationButton.setDescription("This button should throw an authentication exception");
		authenticationButton.setWidth("100%");
		addToCentrePanel(authenticationButton);

		disableIfNotAuthorisedButton = new Button("disabled button, it requires authorisation ");
		disableIfNotAuthorisedButton.setImmediate(true);
		disableIfNotAuthorisedButton.addClickListener(this);
		disableIfNotAuthorisedButton.setWidth("100%");
		disableIfNotAuthorisedButton
				.setDescription("This button should be disabled, because the current user does not have permission to use it");
		Subject user = SecurityUtils.getSubject();
		disableIfNotAuthorisedButton.setEnabled(user.isPermitted("button:click"));
		addToCentrePanel(disableIfNotAuthorisedButton);

		this.addComponent(grid);

	}

	protected Button addNavButton(String caption, String uri) {
		Button button = new Button(caption);
		button.setData(uri);
		button.setDescription(uri);
		button.addStyleName(ChameleonTheme.BUTTON_TALL);
		button.setWidth("100%");
		button.addClickListener(this);
		addToCentrePanel(button);
		return button;
	}

	private Panel centrePanel() {
		Panel centrePanel = new Panel();
		centrePanel.setCaption("switch to other views");
		panelLayout = new VerticalLayout();
		centrePanel.setContent(panelLayout);
		panelLayout.setSpacing(false);
		panelLayout.setWidth("100%");
		// centrePanel.setSizeFull();
		return centrePanel;
	}

	private Label viewLabel() {
		viewLabel = new Label();
		viewLabel.setWidth("100%");
		viewLabel.addStyleName(ChameleonTheme.LABEL_H3);
		return viewLabel;
	}

	protected void addToCentrePanel(Component c) {
		panelLayout.addComponent(c);
	}

	@Override
	public void processParams(List<String> params) {
		this.params = Lists.newArrayList(params);
		String s = "Using an instance of " + simpleClassName() + ".\n\nWe arrived at this page with ";
		if (params.isEmpty()) {
			s = s + " no parameters ";
		} else {
			s = s + " parameters: " + params.toString();
		}
		viewLabel.setValue(s);
	}

	/**
	 * Removes the "enhancer by guice" from the class name
	 * 
	 * @param simpleName
	 * @return
	 */
	private String simpleClassName() {
		String s = this.getClass().getSimpleName();
		int i = s.indexOf("$");
		if (i > 1) {
			String s1 = s.substring(0, i);
			return s1;
		}
		return s;
	}

	public Label getViewLabel() {
		return viewLabel;
	}

	public GridLayout getGrid() {
		return grid;
	}

	public FooterBar getFooterBar() {
		return footerBar;
	}

	protected Panel msgPanel() {
		Panel panel = new Panel("Use the footer bar for messages");
		VerticalLayout vl = new VerticalLayout();
		panel.setContent(vl);
		panel.setSizeUndefined();
		panel.setWidth("100%");
		vl.setSizeUndefined();
		vl.addComponent(new Label(
				"This section uses a @UIScoped FooterBar, injected into the View, to display a message"));
		vl.addComponent(new Label("Enter some text, and click 'fire message', and the text will appear in footer"));
		textField = new TextField();
		vl.addComponent(textField);

		sendMsgButton = new Button("Fire message");
		sendMsgButton.setImmediate(true);
		sendMsgButton.addClickListener(this);
		vl.addComponent(sendMsgButton);

		return panel;
	}

	@Override
	public void buttonClick(ClickEvent event) {
		Button btn = event.getButton();
		if (btn == sendMsgButton) {
			getFooterBar().setUserMessage(textField.getValue());
			return;
		}

		if (btn == authorisationButton) {
			doSecureThing();
			return;
		}

		if (btn == authenticationButton) {
			doAuthenticationThing();
			return;
		}

		String uri = (btn.getData() == null) ? null : btn.getData().toString();
		if (uri != null) {
			this.getScopedUI().getV7Navigator().navigateTo(uri);
		}
	}

	@RequiresAuthentication
	protected void doAuthenticationThing() {
		Notification.show(
				"You got here because you have logged in. If you had not, Shiro would have raised an exception",
				Type.HUMANIZED_MESSAGE);
	}

	/**
	 * This should cause a failure, because the user cannot have these permissions
	 */
	@RequiresPermissions("button:secure")
	protected void doSecureThing() {
		throw new RuntimeException("Should not be here");

	}

	private Panel linkPanel() {
		Panel panel = new Panel("Use alternate UIs");
		VerticalLayout vl = new VerticalLayout();
		panel.setContent(vl);
		vl.addComponent(new Label(
				"This demo uses a simple counter to alternate between using different UIs. Open another tab to see the alternate.  (One has a sidebar, the other does not)"));

		// this.getUI() is null at this stage, so use CurrentInstance
		UI ui = CurrentInstance.get(UI.class);

		String linkMsg = (ui.getPage() == null) ? "no link available" : "Alternate UI, opened in another tab";
		String linkTarget = (ui.getPage() == null) ? "" : ui.getPage().getLocation().toString();

		Link link = new Link(linkMsg, new ExternalResource(linkTarget));

		// Open the URL in a new window/tab
		link.setTargetName("_blank");

		vl.addComponent(link);
		vl.addComponent(new Label("See also the DemoUIProvider javadoc"));
		return panel;
	}

	public ArrayList<String> getParams() {
		return params;
	}

	@Override
	public void enter(V7ViewChangeEvent event) {
		super.enter(event);
	}

	public abstract int getColourIndex();
}
