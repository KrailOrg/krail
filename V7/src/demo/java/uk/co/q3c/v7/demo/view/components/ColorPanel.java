package uk.co.q3c.v7.demo.view.components;

import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;

/**
 * 
 * Allows the setting of background colour
 * 
 * @author David Sowerby 5 Jan 2013
 * 
 */
public class ColorPanel extends CssLayout {

	private String color = "#aeec31";

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
