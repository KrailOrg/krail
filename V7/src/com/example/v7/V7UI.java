package com.example.v7;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.*;

public class V7UI extends UI {
	@Override
	public void init(VaadinRequest request) {
		Label label = new Label("Hello Vaadin user");
		addComponent(label);
	}

}
