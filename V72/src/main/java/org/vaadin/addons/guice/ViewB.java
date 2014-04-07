package org.vaadin.addons.guice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.risto.stepper.IntStepper;

import uk.co.q3c.v7.base.guice.uiscope.UIScoped;
import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.view.V7View;
import uk.co.q3c.v7.base.view.V7ViewChangeEvent;

import com.google.inject.Inject;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import de.steinwedel.messagebox.ButtonId;
import de.steinwedel.messagebox.Icon;
import de.steinwedel.messagebox.MessageBox;

@UIScoped
public class ViewB extends VerticalLayout implements V7View {
	private static Logger log = LoggerFactory.getLogger(ViewB.class);
	private final SessionScopedBean bean;
	private final Label label;
	private final IntStepper stepper;
	private final Button popupButton;

	@Override
	public void enter(V7ViewChangeEvent event) {

		label.setValue(bean.getSessionScopedData());
	}

	@Inject
	public ViewB(final SessionScopedBean bean, final V7Navigator navigator2) {
		log.debug("~~~ creating View B ~~~");
		addComponent(new Label("ViewB"));

		this.bean = bean;
		this.label = new Label();
		addComponent(label);
		Button navButton = new Button("Go to page A");
		addComponent(navButton);
		navButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				navigator2.navigateTo("A");

			}
		});
		addComponent(navButton);

		Button widgetsetButton = new Button("Go to page widgetset page");
		widgetsetButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				navigator2.navigateTo("widgetset");

			}
		});
		addComponent(widgetsetButton);

		TextField textField = new TextField();
		textField.setImmediate(true);
		textField.setValue(bean.getSessionScopedData());
		addComponent(textField);

		textField.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				bean.setSessionScopedData(event.getProperty().getValue().toString());
				label.setValue(bean.getSessionScopedData());

			}
		});

		stepper = new IntStepper("Stepper");
		stepper.setValue(5);
		addComponent(stepper);

		popupButton = new Button("Popup message box");
		popupButton.setWidth("100%");
		popupButton.addClickListener(new ClickListener() {

			private MessageBox messageBox;

			@Override
			public void buttonClick(ClickEvent event) {
				messageBox = MessageBox.showPlain(Icon.INFO, "Example 1", "Hello World!", ButtonId.OK);
			}
		});
		addComponent(popupButton);

	}

	@Override
	public Component getRootComponent() {
		return this;
	}

	@Override
	public String viewName() {
		return "B";
	}

	@Override
	public void setIds() {
		// TODO Auto-generated method stub

	}

}
