package uk.co.q3c.v7.base.view.components;

import javax.inject.Inject;

import uk.co.q3c.v7.base.guice.uiscope.UIScoped;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ChameleonTheme;

@UIScoped
public class FooterBar extends HorizontalLayout {

	private final Label infoLabel;
	private final Label msgLabel;

	@Inject
	protected FooterBar() {
		super();
		setSpacing(true);
		infoLabel = new Label();
		infoLabel
				.setValue("  Because the footer bar is @UIScoped, it can be injected into any component to show user messages.  Last user message:   ");
		msgLabel = new Label();
		msgLabel.addStyleName(ChameleonTheme.LABEL_H3);

		Label padLabel = new Label(" ");
		padLabel.setWidth("20px");
		Label padLabel2 = new Label(" ");
		padLabel2.setWidth("20px");

		addComponent(infoLabel);
		// addComponent(padLabel);
		addComponent(msgLabel);
	}

	public void setUserMessage(String msg) {
		msgLabel.setValue(msg);
	}

	public String getUserMessage() {
		return msgLabel.getValue();
	}

}
