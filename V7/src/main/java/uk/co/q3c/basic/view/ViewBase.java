package uk.co.q3c.basic.view;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.q3c.basic.guice.navigate.GuiceView;
import uk.co.q3c.basic.guice.navigate.GuiceViewChangeEvent;
import uk.co.q3c.basic.guice.navigate.ScopedUI;

import com.vaadin.ui.VerticalLayout;

public abstract class ViewBase extends VerticalLayout implements GuiceView {
	private static Logger log = LoggerFactory.getLogger(ViewBase.class);

	@Inject
	protected ViewBase() {
		super();
	}

	@Override
	public void enter(GuiceViewChangeEvent event) {
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
