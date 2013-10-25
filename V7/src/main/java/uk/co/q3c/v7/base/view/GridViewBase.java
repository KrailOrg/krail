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
