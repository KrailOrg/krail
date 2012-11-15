package basic;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.vaadin.server.Page.UriFragmentChangedEvent;
import com.vaadin.server.Page.UriFragmentChangedListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class BasicUI extends UI implements UriFragmentChangedListener {

	@Inject
	@Named("title")
	private final String title = "Basic UI (default title)";

	@Inject(optional = true)
	@Named("version")
	private final String version = "Vaadin <b>7.0.0 beta9</b>";

	@Inject
	private MessageSource msgSource;
	private Label fragmentLabel;

	@Override
	protected void init(VaadinRequest request) {
		getPage().setTitle(title);
		getPage().addUriFragmentChangedListener(this);

		Label label = new Label(version + ", " + msgSource.msg(), ContentMode.HTML);
		label.setHeight("100px");

		fragmentLabel = new Label("url fragment = " + getPage().getUriFragment());
		fragmentLabel.setHeight("100px");

		VerticalLayout layout = new VerticalLayout();
		layout.addComponent(label);
		layout.addComponent(fragmentLabel);

		setContent(layout);
	}

	@Override
	public void uriFragmentChanged(UriFragmentChangedEvent event) {
		fragmentLabel.setValue("uri fragment changed to:" + event.getUriFragment());
	}

}