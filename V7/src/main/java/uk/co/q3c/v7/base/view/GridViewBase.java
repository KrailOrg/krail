package uk.co.q3c.v7.base.view;

import java.util.LinkedHashMap;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.q3c.v7.base.ui.ScopedUI;

import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;

public abstract class GridViewBase extends GridLayout implements V7View {
	private static Logger log = LoggerFactory.getLogger(GridViewBase.class);

	@Inject
	protected GridViewBase() {
		super();
	}

	@Override
	public void enter(V7ViewChangeEvent event) {
		log.debug("entered view: " + this.getClass().getSimpleName() + " with uri: "
				+ event.getNewNavigationState().getFragment().getUri());
		processParams(event.getNewNavigationState().getFragment().getParameters());
	}

	/**
	 * typecasts and returns getUI()
	 * 
	 * @return
	 */

	public ScopedUI getScopedUI() {
		return (ScopedUI) getUI();
	}

	protected abstract void processParams(LinkedHashMap<String, String> map);

	@Override
	public Component getRootComponent() {
		return this;
	}

}
