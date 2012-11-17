package basic;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class View2 extends VerticalLayout implements View {

	protected View2() {
		super();
		this.addComponent(new Label("view 2"));
	}

	@Override
	public void enter(ViewChangeEvent event) {
		System.out.println("entered view 2");
	}

}
