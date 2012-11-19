package basic;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class View2 extends VerticalLayout implements View, ClickListener {

	private static final long serialVersionUID = 9052414530874756754L;
	private final Button switchViewBtn;

	protected View2() {
		super();
		this.addComponent(new Label("view 2"));
		switchViewBtn = new Button("Switch to view 1");
		this.addComponent(switchViewBtn);
		switchViewBtn.addClickListener(this);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		System.out.println("entered view 2");
	}

	@Override
	public void buttonClick(ClickEvent event) {
		this.getUI().getNavigator().navigateTo("view1");
	}
}
