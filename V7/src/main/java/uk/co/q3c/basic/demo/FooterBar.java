package uk.co.q3c.basic.demo;

import javax.inject.Inject;

import uk.co.q3c.basic.guice.uiscope.UIScoped;

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
		infoLabel = new Label();
		infoLabel
				.setValue("The footer bar is @UIScoped, and can be injected to show user messages from any other component.  Last user message:   ");
		msgLabel = new Label();
		msgLabel.addStyleName(ChameleonTheme.LABEL_H4);
		Label padLabel = new Label();
		padLabel.setWidth("20px");
		addComponent(infoLabel);
		addComponent(padLabel);
		addComponent(msgLabel);
	}

	public void setUserMessage(String msg) {
		msgLabel.setValue(msg);
	}

}
