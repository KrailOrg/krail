package uk.co.q3c.v7.base.view;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.ui.ScopedUI;

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
		log.debug("entered view: " + this.getClass().getSimpleName() + " with uri: "
				+ event.getNavigator().getNavigationState());
		List<String> params = event.getNavigator().geNavigationParams();
		processParams(params);
	}

//	/**
//	 * typecasts and returns getUI()
//	 * 
//	 * @return
//	 */
//	public ScopedUI getScopedUI() {
//		return (ScopedUI) getUI();
//	}
	
	/**
	 * typecasts and returns getUI()
	 * 
	 * @return
	 */
	//why do you use getScopedUI instead of getUI ?
	@Override
	public ScopedUI getUI() {
		return (ScopedUI) super.getUI();
	}
	
	public V7Navigator getNavigator() {
		return getUI().getV7Navigator();
	}

	protected abstract void processParams(List<String> params);

	@Override
	public Component getUiComponent() {
		return this;
	}

}
