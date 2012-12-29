package uk.co.q3c.v7.demo.view;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.q3c.v7.base.navigate.ScopedUI;
import uk.co.q3c.v7.base.navigate.V7View;
import uk.co.q3c.v7.base.navigate.V7ViewChangeEvent;

import com.vaadin.ui.VerticalLayout;

public abstract class ViewBase extends VerticalLayout implements V7View {
	private static Logger log = LoggerFactory.getLogger(ViewBase.class);

	@Inject
	protected ViewBase() {
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
