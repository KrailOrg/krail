package uk.co.q3c.basic;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class View1 extends VerticalLayout implements View, ClickListener {

	private static final long serialVersionUID = -7319085242848063460L;
	private static Logger log = LoggerFactory.getLogger(View1.class);
	private final Button switchViewBtn;
	private final Button homeBtn;
	private final URIDecoder uriDecoder;

	@Inject
	protected View1(URIDecoder uriDecoder) {
		super();
		this.uriDecoder = uriDecoder;
		this.addComponent(new Label("view 1"));
		switchViewBtn = new Button("Switch to view 2 with parameters");
		switchViewBtn.setData("view2/a=b");
		homeBtn = new Button("home");
		homeBtn.setData("");
		this.addComponent(switchViewBtn);
		this.addComponent(homeBtn);
		switchViewBtn.addClickListener(this);
		homeBtn.addClickListener(this);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		log.debug("entered view 1 with " + event.getNavigator().getState());
	}

	@Override
	public void buttonClick(ClickEvent event) {
		Button btn = event.getButton();
		this.getUI().getNavigator().navigateTo(btn.getData().toString());
	}

}
