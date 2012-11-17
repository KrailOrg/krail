package basic;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.Page.UriFragmentChangedEvent;
import com.vaadin.server.Page.UriFragmentChangedListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class BasicUI extends UI implements UriFragmentChangedListener {

	@Inject
	@Named("title")
	private String title;

	@Inject
	@Named("version")
	private String version;

	@Inject
	private MessageSource msgSource;

	private Navigator navigator;

	@Override
	protected void init(VaadinRequest request) {
		getPage().setTitle(title);
		getPage().addUriFragmentChangedListener(this);

		Panel topBar = new Panel("top bar " + title + "  " + version);
		VerticalLayout topBarLayout = new VerticalLayout();
		topBarLayout.addComponent(new Label(msgSource.msg()));
		topBar.setContent(topBarLayout);
		topBar.setHeight("100px");
		topBar.setWidth("100%");

		Panel bottomBar = new Panel("bottom bar");
		bottomBar.setHeight("100px");
		bottomBar.setWidth("100%");

		VerticalLayout coreLayout = new VerticalLayout();
		coreLayout.addComponent(new View1());
		coreLayout.setSizeUndefined();
		coreLayout.setWidth("100%");

		VerticalLayout screenLayout = new VerticalLayout(topBar, coreLayout, bottomBar);
		screenLayout.setSizeFull();
		screenLayout.setExpandRatio(coreLayout, 1);

		navigator = new Navigator(this, coreLayout);
		//
		// Navigator.ComponentContainerViewDisplay viewDisplay = new
		// Navigator.ComponentContainerViewDisplay(coreLayout);

		// Create and register the views
		navigator.addProvider(new GuiceViewProvider());

		// Navigate to the start view
		navigator.navigateTo("view1");
		;
		setContent(screenLayout);
	}

	@Override
	public void uriFragmentChanged(UriFragmentChangedEvent event) {

	}

}