package uk.co.q3c.v7.base.ui;

import uk.co.q3c.v7.base.guice.uiscope.UIKey;
import uk.co.q3c.v7.base.guice.uiscope.UIScope;
import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.view.V7View;
import uk.co.q3c.v7.base.view.V7ViewHolder;
import uk.co.q3c.v7.i18n.I18NKeys;

import com.vaadin.navigator.Navigator;
import com.vaadin.server.ErrorHandler;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;

public abstract class ScopedUI extends UI implements V7ViewHolder {

	private UIKey instanceKey;
	private UIScope uiScope;
	private final Panel viewDisplayPanel;
	private final V7Navigator navigator;
	private final ErrorHandler errorHandler;
	private AbstractOrderedLayout screenLayout;

	protected ScopedUI(V7Navigator navigator, ErrorHandler errorHandler) {
		super();
		this.errorHandler = errorHandler;
		this.navigator = navigator;
		viewDisplayPanel = new Panel();
		viewDisplayPanel.setSizeFull();
	}

	public void setInstanceKey(UIKey instanceKey) {
		this.instanceKey = instanceKey;
	}

	public UIKey getInstanceKey() {
		return instanceKey;
	}

	@Override
	public void detach() {
		if (uiScope != null) {
			uiScope.releaseScope(this.getInstanceKey());
		}
		super.detach();
	}

	/**
	 * The Vaadin navigator has been replaced by the V7Navigator, use {@link #getV7Navigator()} instead.
	 * 
	 * @see com.vaadin.ui.UI#getNavigator()
	 */
	@Override
	@Deprecated
	public Navigator getNavigator() {
		return null;
	}

	public V7Navigator getV7Navigator() {
		return navigator;
	}

	@Override
	public void setNavigator(Navigator navigator) {
		throw new MethodReconfigured("UI.setNavigator() not available, use injection instead");
	}

	// TODO fromView serves no purpose
	@Override
	public void changeView(V7View fromView, V7View toView) {
		Component content = toView.getUiComponent();
		content.setSizeFull();
		viewDisplayPanel.setContent(content);
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
		Page page = getPage();
		page.addUriFragmentChangedListener(navigator);
		setErrorHandler(errorHandler);
		page.setTitle(pageTitle());
		doLayout();
		// Navigate to the correct start point
		String fragment = getPage().getUriFragment();
		getV7Navigator().navigateTo(fragment);

	}

	/**
	 * Uses the {@link #screenLayout} defined by sub-class implementations of {@link #screenLayout()}, expands it to
	 * full size, and sets the View display panel to take up all spare space.
	 */
	protected void doLayout() {
		if (screenLayout == null) {
			screenLayout = screenLayout();
		}
		screenLayout.setSizeFull();
		screenLayout.setExpandRatio(getViewDisplayPanel(), 1);
		setContent(screenLayout);
	}

	protected abstract AbstractOrderedLayout screenLayout();

	public V7View getView() {
		return (V7View) viewDisplayPanel.getContent();
	}

	/**
	 * Override to provide a title for your UI page This will appear in your browser tab. If this needs to be an I18N
	 * title, use {@link I18NKeys#getValue(java.util.Locale)} (see also the documentation at
	 * https://sites.google.com/site/q3cjava/internationalisation-i18n)
	 * 
	 * @return
	 */
	protected abstract String pageTitle();

}