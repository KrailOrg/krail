package uk.co.q3c.v7.demo.ui;

import javax.inject.Inject;

import org.apache.shiro.SecurityUtils;

import uk.co.q3c.v7.A;
import uk.co.q3c.v7.base.navigate.ScopedUI;
import uk.co.q3c.v7.base.navigate.V7Navigator;

import com.google.inject.name.Named;
import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.VerticalLayout;

@PreserveOnRefresh
@Theme("chameleon")
public class BasicUI extends ScopedUI {

	private final String title;

	private final HeaderBar headerBar;

	private final FooterBar footerBar;

	@Inject
	protected BasicUI(HeaderBar headerBar, FooterBar footerBar, @Named(A.title) String title, V7Navigator navigator) {
		super(navigator);
		this.title = title;
		this.footerBar = footerBar;
		this.headerBar = headerBar;

	}

	@Override
	protected void init(VaadinRequest request) {
		super.init(request);
		getPage().setTitle(title);

		doLayout();
		footerBar.setUserName(SecurityUtils.getSubject().getPrincipal().toString());

		// Navigate to the start view
		getGuiceNavigator().navigateTo("");
	}

	protected void doLayout() {

		VerticalLayout screenLayout = new VerticalLayout(headerBar, getViewDisplayPanel(), footerBar);
		screenLayout.setSizeFull();
		screenLayout.setExpandRatio(getViewDisplayPanel(), 1);
		setContent(screenLayout);

	}

	public HeaderBar getHeaderBar() {
		return headerBar;
	}

	public FooterBar getFooterBar() {
		return footerBar;
	}

}