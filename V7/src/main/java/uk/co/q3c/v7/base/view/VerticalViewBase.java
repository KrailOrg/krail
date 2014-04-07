package uk.co.q3c.v7.base.view;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.q3c.v7.base.ui.ScopedUI;

import com.google.inject.Inject;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

public abstract class VerticalViewBase extends VerticalLayout implements V7View {
	private static Logger log = LoggerFactory.getLogger(VerticalViewBase.class);

	@Inject
	protected VerticalViewBase() {
		super();
	}

	@Override
	public void enter(V7ViewChangeEvent event) {
		log.debug("entered view: " + this.getClass().getSimpleName() + " with uri: " + event.getNavigationState());
		List<String> params = event.getNavigationState().getParameterList();
		processParams(params);
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

	protected abstract void processParams(List<String> params);

	@Override
	public Component getRootComponent() {
		return this;
	}
}
