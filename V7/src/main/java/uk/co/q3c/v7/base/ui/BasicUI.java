package uk.co.q3c.v7.base.ui;

import javax.inject.Inject;

import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.demo.view.components.FooterBar;
import uk.co.q3c.v7.demo.view.components.HeaderBar;
import uk.co.q3c.v7.demo.view.components.InfoBar;

import com.vaadin.annotations.Theme;
import com.vaadin.data.util.converter.ConverterFactory;
import com.vaadin.server.ErrorHandler;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.VerticalLayout;

// @PreserveOnRefresh
@Theme("chameleon")
public class BasicUI extends ScopedUI {

	private final HeaderBar headerBar;

	private final FooterBar footerBar;

	private final InfoBar infoBar;

	@Inject
	protected BasicUI(HeaderBar headerBar, FooterBar footerBar, InfoBar infoBar, V7Navigator navigator,
			ErrorHandler errorHandler, ConverterFactory converterFactory) {
		super(navigator, errorHandler, converterFactory);
		this.footerBar = footerBar;
		this.headerBar = headerBar;
		this.infoBar = infoBar;

	}

	@Override
	protected AbstractOrderedLayout screenLayout() {
		return new VerticalLayout(headerBar, infoBar, getViewDisplayPanel(), footerBar);
	}

	public HeaderBar getHeaderBar() {
		return headerBar;
	}

	public FooterBar getFooterBar() {
		return footerBar;
	}

	@Override
	protected String pageTitle() {
		return "V7";
	}

}