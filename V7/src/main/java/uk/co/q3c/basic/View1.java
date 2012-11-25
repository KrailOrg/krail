package uk.co.q3c.basic;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;

public class View1 extends ViewBase implements ClickListener {

	private static Logger log = LoggerFactory.getLogger(View1.class);
	private final Button switchViewBtn;
	private final Button homeBtn;

	@Inject
	protected View1(URIDecoder uriDecoder) {
		super(uriDecoder);
		this.addComponent(new Label("view 1"));
		switchViewBtn = new Button("Switch to view 2 with parameters");
		switchViewBtn.setData("view2/a=b");
		switchViewBtn.addStyleName("big");
		homeBtn = new Button("home");
		homeBtn.setData("");
		homeBtn.addStyleName("default");
		this.addComponent(switchViewBtn);
		this.addComponent(homeBtn);
		switchViewBtn.addClickListener(this);
		homeBtn.addClickListener(this);
	}

	@Override
	public void buttonClick(ClickEvent event) {
		Button btn = event.getButton();
		this.getUI().getNavigator().navigateTo(btn.getData().toString());
	}

	@Override
	protected void processParams(List<String> params) {
	}

}
