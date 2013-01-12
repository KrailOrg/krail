package uk.co.q3c.v7.base.view.components;

import com.vaadin.ui.Button;
import com.vaadin.ui.Label;

public interface HeaderBar {

	public abstract void userChanged();

	public abstract Button getLoginBtn();

	public abstract Label getUserLabel();

}