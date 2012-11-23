package uk.co.q3c.basic;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;

public class HomeView extends ViewBase implements ClickListener {

	private static final long serialVersionUID = -7319085242848063460L;
	private static Logger log = LoggerFactory.getLogger(HomeView.class);
	private final Button switchToView2Btn;
	private final Button switchToView1Btn;

	@Inject
	protected HomeView(URIDecoder uriDecoder) {
		super(uriDecoder);
		this.addComponent(new Label("Home View"));
		switchToView2Btn = new Button("Switch to view 2");
		switchToView2Btn.setData("view2");
		switchToView1Btn = new Button("Switch to view 1");
		switchToView1Btn.setData("view1");
		this.addComponent(switchToView2Btn);
		this.addComponent(switchToView1Btn);
		switchToView2Btn.addClickListener(this);
		switchToView1Btn.addClickListener(this);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		log.debug("entered home page " + event.getNavigator().getState());
	}

	@Override
	public void buttonClick(ClickEvent event) {
		Button btn = event.getButton();
		this.getUI().getNavigator().navigateTo(btn.getData().toString());
	}

}
