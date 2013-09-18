package uk.co.q3c.v7.base.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.q3c.v7.base.config.V7ConfigurationException;
import uk.co.q3c.v7.base.guice.uiscope.UIKey;
import uk.co.q3c.v7.base.guice.uiscope.UIScope;
import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.navigate.sitemap.Sitemap;
import uk.co.q3c.v7.base.view.V7View;
import uk.co.q3c.v7.base.view.V7ViewHolder;
import uk.co.q3c.v7.i18n.I18NKey;

import com.vaadin.data.util.converter.ConverterFactory;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.ErrorHandler;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;

public abstract class ScopedUI extends UI implements V7ViewHolder {
	private static Logger log = LoggerFactory.getLogger(ScopedUI.class);
	private UIKey instanceKey;
	private UIScope uiScope;
	private final Panel viewDisplayPanel;
	private final V7Navigator navigator;
	private final ErrorHandler errorHandler;
	private AbstractOrderedLayout screenLayout;
	private final ConverterFactory converterFactory;
	private V7View view;

	protected ScopedUI(V7Navigator navigator, ErrorHandler errorHandler, ConverterFactory converterFactory) {
		super();
		this.errorHandler = errorHandler;
		this.navigator = navigator;
		this.converterFactory = converterFactory;
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
		if (log.isDebugEnabled()) {
			String from = (fromView == null) ? "null" : fromView.getClass().getSimpleName();
			String to = (toView == null) ? "null" : toView.getClass().getSimpleName();
			log.debug("changing view from " + from + " to " + to);
		}

		Component content = toView.getRootComponent();
		content.setSizeFull();
		viewDisplayPanel.setContent(content);
		this.view = toView;
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

		getSession().setConverterFactory(converterFactory);

		// page isn't available during injected construction
		Page page = getPage();
		page.addUriFragmentChangedListener(navigator);
		setErrorHandler(errorHandler);
		page.setTitle(pageTitle());
		doLayout();
		// Navigate to the correct start point
		String fragment = getPage().getUriFragment();
		getV7Navigator().navigateTo(fragment==null?"":fragment);

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
		if (viewDisplayPanel.getParent() == null) {
			String msg = "Your implementation of ScopedUI.screenLayout() must include getViewDisplayPanel().  AS a minimum this could be 'return new VerticalLayout(getViewDisplayPanel())'";
			log.error(msg);
			throw new V7ConfigurationException(msg);
		}
		// screenLayout.setExpandRatio(getViewDisplayPanel(), 1);
		viewDisplayPanel.setSizeFull();
		setContent(screenLayout);
	}

	/**
	 * Override this to provide your screen layout. In order for Views to work one child component of this layout must
	 * be provided by {@link #getViewDisplayPanel()}. The simplest example would be
	 * {@code return new VerticalLayout(getViewDisplayPanel()}, which would set the View to take up all the available
	 * screen space. {@link BasicUI} is an example of a UI which contains a header and footer bar.
	 * 
	 * @return
	 */
	protected abstract AbstractOrderedLayout screenLayout();

	public V7View getView() {
		return view;
	}

	/**
	 * Override to provide a title for your UI page This will appear in your browser tab. If this needs to be an I18N
	 * title, use {@link I18NKey#getValue(java.util.Locale)} (see also the documentation at
	 * https://sites.google.com/site/q3cjava/internationalisation-i18n)
	 * 
	 * @return
	 */
	protected abstract String pageTitle();

}