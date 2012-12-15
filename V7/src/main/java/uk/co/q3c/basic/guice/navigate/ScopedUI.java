package uk.co.q3c.basic.guice.navigate;

import uk.co.q3c.basic.guice.uiscope.UIKey;
import uk.co.q3c.basic.guice.uiscope.UIScope;

import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;

public abstract class ScopedUI extends UI implements GuiceViewHolder {

	private UIKey instanceKey;
	private UIScope uiScope;
	private final Panel viewDisplayPanel;
	private final GuiceNavigator navigator;

	protected ScopedUI(GuiceNavigator navigator) {
		super();
		this.navigator = navigator;
		viewDisplayPanel = new Panel();
		viewDisplayPanel.setSizeUndefined();
		viewDisplayPanel.setWidth("100%");
	}

	public void setInstanceKey(UIKey instanceKey) {
		this.instanceKey = instanceKey;
	}

	public UIKey getInstanceKey() {
		return instanceKey;
	}

	public void setScope(UIScope uiScope) {
		this.uiScope = uiScope;
	}

	@Override
	public void detach() {
		if (uiScope != null) {
			uiScope.release(this);
		}
		super.detach();
	}

	@Override
	public Navigator getNavigator() {
		return null;
	}

	public GuiceNavigator getGuiceNavigator() {
		return navigator;
	}

	@Override
	public void setNavigator(Navigator navigator) {
		throw new MethodReconfigured("UI.setNavigator() not available, use injection instead");
	}

	@Override
	public void changeView(GuiceView fromView, GuiceView toView) {
		viewDisplayPanel.setContent(toView.getUiComponent());
	}

	public Panel getViewDisplayPanel() {
		return viewDisplayPanel;
	}

	/**
	 * Make sure you call this from sub-class overrides
	 * 
	 * @see com.vaadin.ui.UI#init(com.vaadin.server.VaadinRequest)
	 */
	@Override
	protected void init(VaadinRequest request) {
		// page isn't available during injected construction
		getPage().addUriFragmentChangedListener(navigator);
	}

}
