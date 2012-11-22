package basic;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class View2 extends VerticalLayout implements View, ClickListener {

	private static final long serialVersionUID = 9052414530874756754L;
	private static Logger log = LoggerFactory.getLogger(View2.class);
	private final Button switchViewBtn;
	private final Button homeBtn;
	private final URIDecoder uriDecoder;
	private final Label paramLabel;

	@Inject
	protected View2(URIDecoder uriDecoder) {
		super();
		this.uriDecoder = uriDecoder;
		this.addComponent(new Label("view 2"));
		switchViewBtn = new Button("Switch to view 1");
		switchViewBtn.setData("view1");
		homeBtn = new Button("home");
		homeBtn.setData("");

		paramLabel = new Label();

		this.addComponent(switchViewBtn);
		this.addComponent(homeBtn);
		this.addComponent(paramLabel);

		switchViewBtn.addClickListener(this);
		homeBtn.addClickListener(this);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		log.debug("entered view 2 with " + event.getNavigator().getState());
		List<String> params = uriDecoder.setNavigationState(event.getNavigator().getState()).parameters();
		if (params.isEmpty()) {
			paramLabel.setCaption(" no parameters ");
		} else {
			paramLabel.setCaption("parameters: " + params.toString());
		}
	}

	@Override
	public void buttonClick(ClickEvent event) {
		Button btn = event.getButton();
		this.getUI().getNavigator().navigateTo(btn.getData().toString());
	}
}
