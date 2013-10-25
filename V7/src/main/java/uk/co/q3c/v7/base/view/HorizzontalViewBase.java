package uk.co.q3c.v7.base.view;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.ui.ScopedUI;

import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;

public abstract class HorizzontalViewBase extends HorizontalLayout implements
		V7View {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(VerticalViewBase.class);
	private final V7Navigator navigator;

	@Inject
	protected HorizzontalViewBase(V7Navigator navigator) {
		super();
		this.navigator = navigator;
	}

	/**
	 * typecasts and returns getUI()
	 * 
	 * @return
	 */

	@Override
	public ScopedUI getUI() {
		return (ScopedUI) super.getUI();
	}

	@Override
	public Component getRootComponent() {
		return this;
	}

	public V7Navigator getNavigator() {
		return navigator;
	}
}
