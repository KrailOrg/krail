package basic;

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

	protected View1() {
		super();
		this.addComponent(new Label("view 1"));
		switchViewBtn = new Button("Switch to view 2");
		this.addComponent(switchViewBtn);
		switchViewBtn.addClickListener(this);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		log.debug("entered view 1 with " + event.getNavigator().getState());
	}

	@Override
	public void buttonClick(ClickEvent event) {
		this.getUI().getNavigator().navigateTo("view2");
	}

}
