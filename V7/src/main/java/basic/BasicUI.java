package basic;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class BasicUI extends UI {

	@Inject
	@Named("title")
	private final String title = "Basic UI (default title)";

	@Inject(optional = true)
	@Named("version")
	private final String version = "Vaadin <i>7.0.0 beta9</i>";

	@Inject
	private MessageSource msgSource;

	@Override
	protected void init(VaadinRequest request) {
		getPage().setTitle(title);

		Label label = new Label(version + ", " + msgSource.msg(), ContentMode.HTML);
		label.setSizeUndefined();

		VerticalLayout layout = new VerticalLayout(label);
		layout.setSizeFull();
		layout.setComponentAlignment(label, Alignment.MIDDLE_CENTER);

		setContent(layout);
	}

}