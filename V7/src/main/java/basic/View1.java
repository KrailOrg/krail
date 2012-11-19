package basic;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class View1 extends VerticalLayout implements View, ClickListener {

	private static final long serialVersionUID = -7319085242848063460L;
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
		System.out.println("entered view 1");
	}

	@Override
	public void buttonClick(ClickEvent event) {
		this.getUI().getNavigator().navigateTo("view2");
	}

}
