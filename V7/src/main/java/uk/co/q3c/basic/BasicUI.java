package uk.co.q3c.basic;

import javax.inject.Inject;

import uk.co.q3c.basic.guice.navigate.GuiceNavigator;
import uk.co.q3c.basic.guice.navigate.ScopedUI;

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
	protected BasicUI(HeaderBar headerBar, FooterBar footerBar, @Named(A.title) String title, GuiceNavigator navigator) {
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