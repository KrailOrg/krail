package uk.co.q3c.v7.base.view;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.q3c.v7.base.ui.ScopedUI;

import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;

public abstract class GridViewBase extends GridLayout implements V7View {
	private static final Logger LOGGER = LoggerFactory.getLogger(GridViewBase.class);

	@Inject
	protected GridViewBase() {
		super();
	}

	@Override
	public boolean beforeEnter(V7ViewChangeEvent event) {
		return true;
	}
	
	@Override
	public void afterEnter(V7ViewChangeEvent event) {
		LOGGER.debug("entered view: " + this.getClass().getSimpleName() + " with uri: "
				+ event.getNewNavigationState().getFragment().getUri());
	}

	/**
	 * typecasts and returns getUI()
	 * 
	 * @return
	 */

	public ScopedUI getScopedUI() {
		return (ScopedUI) getUI();
	}

	@Override
	public Component getRootComponent() {
		return this;
	}

}
