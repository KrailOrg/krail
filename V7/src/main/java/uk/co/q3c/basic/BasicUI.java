package uk.co.q3c.basic;

import javax.inject.Inject;

import uk.co.q3c.basic.guice.navigate.ComponentContainerViewDisplay;
import uk.co.q3c.basic.guice.navigate.GuiceNavigator;
import uk.co.q3c.basic.guice.navigate.GuiceViewProvider;

import com.google.inject.name.Named;
import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.Page.UriFragmentChangedEvent;
import com.vaadin.server.Page.UriFragmentChangedListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.VerticalLayout;

@PreserveOnRefresh
@Theme("chameleon")
public class BasicUI extends ScopedUI implements UriFragmentChangedListener {

	private final GuiceViewProvider viewProvider;

	private final GuiceNavigator navigator;

	private final String title;

	private final HeaderBar headerBar;

	private final FooterBar footerBar;

	@Inject
	protected BasicUI(HeaderBar headerBar, FooterBar footerBar, @Named(A.title) String title,
			GuiceViewProvider viewProvider, GuiceNavigator navigator, ComponentContainerViewDisplay display) {
		super(display);
		this.title = title;
		this.viewProvider = viewProvider;
		this.footerBar = footerBar;
		this.headerBar = headerBar;
		this.navigator = navigator;

	}

	@Override
	protected void init(VaadinRequest request) {
		getPage().setTitle(title);
		getPage().addUriFragmentChangedListener(this);
		Navigator nav;
		VerticalLayout viewArea = doLayout();

		// Navigate to the start view
		navigator.navigateTo("view1");
	}

	protected VerticalLayout doLayout() {
		// viewArea is the layout where Views will be placed
		VerticalLayout viewArea = new VerticalLayout();
		// viewArea.addComponent(new View1());
		viewArea.setSizeUndefined();
		viewArea.setWidth("100%");

		VerticalLayout screenLayout = new VerticalLayout(headerBar, viewArea, footerBar);
		screenLayout.setSizeFull();
		screenLayout.setExpandRatio(viewArea, 1);
		setContent(screenLayout);
		return viewArea;

	}

	@Override
	public void uriFragmentChanged(UriFragmentChangedEvent event) {

	}

	public HeaderBar getHeaderBar() {
		return headerBar;
	}

	public FooterBar getFooterBar() {
		return footerBar;
	}

}