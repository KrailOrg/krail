package uk.co.q3c.v7.views;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;
import com.vaadin.ui.Label;
import com.vaadin.annotations.Theme;

@Theme("views")
public class viewsUI extends UI{
	
	@Override
	protected void init(VaadinRequest request){
		Label lbl = new Label("Hello vaadin");
		setContent(lbl);
	}
}
