package uk.co.q3c.basic.view;

import java.util.List;

import javax.inject.Inject;

import uk.co.q3c.basic.FooterBar;
import uk.co.q3c.basic.URIDecoder;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ChameleonTheme;

public class DemoViewBase extends ViewBase implements ClickListener {

	private VerticalLayout panelLayout;
	private Label viewLabel;
	private final FooterBar footerBar;
	private final GridLayout grid;
	private Button sendMsgButton;
	private TextField textField;

	@Inject
	protected DemoViewBase(URIDecoder uriDecoder, FooterBar footerBar) {
		super(uriDecoder);
		this.footerBar = footerBar;
		grid = new GridLayout(3, 3);
		grid.addComponent(centrePanel(), 1, 1);
		grid.addComponent(viewLabel(), 1, 0);

		grid.setSizeFull();

		grid.setColumnExpandRatio(0, 1);
		grid.setColumnExpandRatio(1, 1);
		grid.setColumnExpandRatio(2, 1);

		grid.setRowExpandRatio(0, 1);
		grid.setRowExpandRatio(1, 1);
		grid.setRowExpandRatio(2, 1);

		getGrid().addComponent(msgPanel(), 1, 2);

		this.addComponent(grid);
	}

	protected Button addNavButton(String caption, String uri) {
		Button button = new Button(caption);
		button.setData(uri);
		button.addStyleName("big");
		button.setWidth("100%");
		button.addClickListener(this);
		addToCentrePanel(button);
		return button;
	}

	private Panel centrePanel() {
		Panel centrePanel = new Panel();
		centrePanel.addStyleName(ChameleonTheme.PANEL_BORDERLESS);
		centrePanel.setCaption("switch to other views");
		panelLayout = new VerticalLayout();
		centrePanel.setContent(panelLayout);
		panelLayout.setSizeFull();
		centrePanel.setSizeFull();
		return centrePanel;
	}

	private Label viewLabel() {
		viewLabel = new Label();
		viewLabel.setWidth("100%");
		viewLabel.addStyleName("h3");
		return viewLabel;
	}

	protected void addToCentrePanel(Component c) {
		panelLayout.addComponent(c);
	}

	@Override
	public void processParams(List<String> params) {
		String s = "Using an instance of " + this.getClass().getSimpleName() + ".\n\nWe arrived at this page with ";
		if (params.isEmpty()) {
			s = s + " no parameters ";
		} else {
			s = s + " parameters: " + params.toString();
		}
		viewLabel.setValue(s);
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
		panel.addStyleName(ChameleonTheme.PANEL_BORDERLESS);
		VerticalLayout vl = new VerticalLayout();
		panel.setContent(vl);

		panel.setSizeFull();
		vl.setSizeFull();
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
		} else {
			String uri = (btn.getData() == null) ? null : btn.getData().toString();
			if (uri != null) {
				this.getUI().getNavigator().navigateTo(uri);
			}
		}
	}

}
