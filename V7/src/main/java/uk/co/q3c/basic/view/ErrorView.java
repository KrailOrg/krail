package uk.co.q3c.basic.view;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.q3c.basic.URIDecoder;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;

public class ErrorView extends ViewBase implements ClickListener {

	private static Logger log = LoggerFactory.getLogger(ErrorView.class);
	private final Button homeBtn;
	private final Label uriLabel;

	@Inject
	protected ErrorView(URIDecoder uriDecoder) {
		super(uriDecoder);
		this.addComponent(new Label("Error view"));
		homeBtn = new Button("go home");
		homeBtn.setData("");

		uriLabel = new Label();

		this.addComponent(homeBtn);
		this.addComponent(uriLabel);

		homeBtn.addClickListener(this);
	}

	@Override
	public void processParams(List<String> params) {
		uriLabel.setCaption(this.getUI().getNavigator().getState());
		if (params.isEmpty()) {
			uriLabel.setCaption(" no parameters ");
		} else {
			uriLabel.setCaption("parameters: " + params.toString());
		}
	}

	@Override
	public void buttonClick(ClickEvent event) {
		Button btn = event.getButton();
		this.getUI().getNavigator().navigateTo(btn.getData().toString());
	}
}
