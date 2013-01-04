package uk.co.q3c.v7.base.ui;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.apache.shiro.web.subject.WebSubject;

import uk.co.q3c.v7.base.guice.uiscope.UIKey;
import uk.co.q3c.v7.base.guice.uiscope.UIScope;
import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.navigate.V7View;
import uk.co.q3c.v7.base.navigate.V7ViewHolder;

import com.vaadin.navigator.Navigator;
import com.vaadin.server.ErrorHandler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.server.VaadinServletResponse;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;

public abstract class ScopedUI extends UI implements V7ViewHolder {

	private UIKey instanceKey;
	private UIScope uiScope;
	private final Panel viewDisplayPanel;
	private final V7Navigator navigator;
	private final ErrorHandler errorHandler;

	protected ScopedUI(V7Navigator navigator, ErrorHandler errorHandler) {
		super();
		this.errorHandler = errorHandler;
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
			uiScope.releaseScope(this.getInstanceKey());
		}
		super.detach();
	}

	@Override
	public Navigator getNavigator() {
		return null;
	}

	public V7Navigator getGuiceNavigator() {
		return navigator;
	}

	@Override
	public void setNavigator(Navigator navigator) {
		throw new MethodReconfigured("UI.setNavigator() not available, use injection instead");
	}

	@Override
	public void changeView(V7View fromView, V7View toView) {
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
		VaadinServletRequest vsr = (VaadinServletRequest) request;
		HttpServletRequest httpRequest = vsr.getHttpServletRequest();

		VaadinResponse response = VaadinService.getCurrentResponse();
		VaadinServletResponse vsresp = (VaadinServletResponse) response;
		HttpServletResponse httpResponse = vsresp.getHttpServletResponse();

		Subject subject = new WebSubject.Builder(httpRequest, httpResponse)
				.principals(new SimplePrincipalCollection("user", "debug")).host("debug").buildSubject();
		ThreadContext.put(ThreadContext.SUBJECT_KEY, subject);

		setErrorHandler(errorHandler);

		// // FIXME replace with login https://github.com/davidsowerby/v7/issues/46
		// UsernamePasswordToken token = new UsernamePasswordToken("user", "password");
		// token.setRememberMe(false);
		// SecurityUtils.getSubject().login(token);

	}

}