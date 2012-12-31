package uk.co.q3c.v7.base.navigate;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.vaadin.ui.VerticalLayout;

public abstract class V7ViewBase extends VerticalLayout implements V7View {
	private static Logger log = LoggerFactory.getLogger(V7ViewBase.class);

	@Inject
	protected V7ViewBase() {
		super();
	}

	@Override
	public void enter(V7ViewChangeEvent event) {
		log.debug("entered view: " + this.getClass().getSimpleName() + " with uri: "
				+ event.getNavigator().getNavigationState());
		List<String> params = event.getNavigator().geNavigationParams();
		processParams(params);
	}

	public ScopedUI getScopedUI() {
		return (ScopedUI) getUI();
	}

	protected void processParams(List<String> params) {
	}
}
