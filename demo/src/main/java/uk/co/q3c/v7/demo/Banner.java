package uk.co.q3c.v7.demo;

import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;

/**
 * 
 * {@link CssLayout} which allows the setting of background colour
 * 
 * @author David Sowerby 5 Jan 2013
 * 
 */
public class Banner extends CssLayout {

	private String color = "#aeec31";

	public Banner() {
		super();
		addComponent(new Label("Banner"));
	}

	@Override
	protected String getCss(Component c) {
		return "background: " + color;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
}
