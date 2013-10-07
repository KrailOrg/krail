package uk.co.q3c.v7.base.view;

import java.util.LinkedHashMap;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.ui.ScopedUI;

import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;

public abstract class CssViewBase extends CssLayout implements V7View {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(VerticalViewBase.class);
	private final V7Navigator navigator;

	@Inject
	protected CssViewBase(V7Navigator navigator) {
		super();
		this.navigator = navigator;
	}

	@Override
	public boolean beforeEnter(V7ViewChangeEvent event) {
		return true;
	}

	@Override
	public void afterEnter(V7ViewChangeEvent event) {
		LOGGER.debug("entered view: " + this.getClass().getSimpleName()
				+ " with uri: "
				+ event.getNewNavigationState().getFragment().getUri());
		processParams(event.getNewNavigationState().getFragment()
				.getParameters());
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

	protected abstract void processParams(LinkedHashMap<String, String> params);

	@Override
	public Component getRootComponent() {
		return this;
	}

	public V7Navigator getNavigator() {
		return navigator;
	}
}
