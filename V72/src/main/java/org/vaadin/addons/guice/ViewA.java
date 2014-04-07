package org.vaadin.addons.guice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

@UIScoped
public class ViewA extends VerticalLayout implements V7View {
	private static Logger log = LoggerFactory.getLogger(ViewA.class);
	// private final SessionScopedBean bean;
	private final Label label;

	@Override
	public void enter(V7ViewChangeEvent event) {

		// label.setValue(bean.getSessionScopedData());
	}

	@Inject
	public ViewA(final V7Navigator navigator2) {
		log.debug("~~~ creating View A ~~~");
		addComponent(new Label("ViewA"));

		// this.bean = bean;
		this.label = new Label();
		addComponent(label);

		Button navButton = new Button("Go to page B");
		addComponent(navButton);
		navButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				navigator2.navigateTo("B");

			}
		});

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
		// textField.setValue(bean.getSessionScopedData());
		addComponent(textField);

		textField.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				// bean.setSessionScopedData(event.getProperty().getValue().toString());

			}
		});

	}

	@Override
	public Component getRootComponent() {
		return this;
	}

	@Override
	public String viewName() {
		return "A";
	}

	@Override
	public void setIds() {
		// TODO Auto-generated method stub

	}
}
