package uk.co.q3c.v7.base.view;

import com.vaadin.ui.Component;

public interface V7View {
	
	/**
	 * To enable implementations to be able to implement this interface without
	 * descending from Component. If the implementation does descend from
	 * Component, just return 'this'
	 * 
	 * @return
	 */
	public Component getRootComponent();

}
