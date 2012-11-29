package uk.co.q3c.basic.view;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.q3c.basic.URIDecoder;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.VerticalLayout;

public class ViewBase extends VerticalLayout implements View {
	private static Logger log = LoggerFactory.getLogger(ViewBase.class);
	private final URIDecoder uriDecoder;

	@Inject
	protected ViewBase(URIDecoder uriDecoder) {
		super();
		this.uriDecoder = uriDecoder;
	}

	@Override
	public void enter(ViewChangeEvent event) {
		log.debug("entered view " + this.getClass().getSimpleName() + event.getNavigator().getState());
		List<String> params = uriDecoder.setNavigationState(event.getNavigator().getState()).parameters();
		processParams(params);
	}

	protected void processParams(List<String> params) {
	}
}
