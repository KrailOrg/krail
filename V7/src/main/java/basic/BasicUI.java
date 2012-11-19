package basic;

import javax.inject.Inject;

import com.google.inject.name.Named;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewProvider;
import com.vaadin.server.Page.UriFragmentChangedEvent;
import com.vaadin.server.Page.UriFragmentChangedListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class BasicUI extends UI implements UriFragmentChangedListener {

	private final String title;

	private final String version;

	private final MessageSource msgSource;

	private final ViewProvider viewProvider;

	private Navigator navigator;

	@Inject
	protected BasicUI(@Named("title") String title, @Named("version") String version, MessageSource msgSource,
			ViewProvider viewProvider) {
		super();
		this.title = title;
		this.version = version;
		this.msgSource = msgSource;
		this.viewProvider = viewProvider;

	}

	@Override
	protected void init(VaadinRequest request) {
		getPage().setTitle(title);
		getPage().addUriFragmentChangedListener(this);

		Panel headerBar = new Panel("header bar " + title + "  " + version);
		VerticalLayout topBarLayout = new VerticalLayout();
		topBarLayout.addComponent(new Label(msgSource.msg()));
		headerBar.setContent(topBarLayout);
		headerBar.setHeight("100px");
		headerBar.setWidth("100%");

		Panel footerBar = new Panel("footer bar");
		footerBar.setHeight("100px");
		footerBar.setWidth("100%");

		VerticalLayout coreLayout = new VerticalLayout();
		coreLayout.addComponent(new View1());
		coreLayout.setSizeUndefined();
		coreLayout.setWidth("100%");

		VerticalLayout screenLayout = new VerticalLayout(headerBar, coreLayout, footerBar);
		screenLayout.setSizeFull();
		screenLayout.setExpandRatio(coreLayout, 1);

		navigator = new Navigator(this, coreLayout);

		// Only one provider needed because GuiceViewProvider does the work to select the View class from the view name
		navigator.addProvider(viewProvider);

		// Navigate to the start view
		navigator.navigateTo("view1");
		setContent(screenLayout);
	}

	@Override
	public void uriFragmentChanged(UriFragmentChangedEvent event) {

	}

	@Override
	public Navigator getNavigator() {
		return navigator;
	}

}