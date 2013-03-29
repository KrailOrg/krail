package uk.co.q3c.v7.base.ui;

import javax.inject.Inject;

import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.view.components.DefaultHeaderBar;
import uk.co.q3c.v7.base.view.components.FooterBar;
import uk.co.q3c.v7.demo.view.components.InfoBar;

import com.vaadin.annotations.Theme;
import com.vaadin.server.ErrorHandler;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.VerticalLayout;

// @PreserveOnRefresh
@Theme("chameleon")
public class BasicUI extends ScopedUI {

	private final DefaultHeaderBar headerBar;

	private final FooterBar footerBar;

	private final InfoBar infoBar;

	@Inject
	protected BasicUI(DefaultHeaderBar headerBar, FooterBar footerBar, InfoBar infoBar, V7Navigator navigator,
			ErrorHandler errorHandler) {
		super(navigator, errorHandler);
		this.footerBar = footerBar;
		this.headerBar = headerBar;
		this.infoBar = infoBar;

	}

	@Override
	protected AbstractOrderedLayout screenLayout() {
		return new VerticalLayout(headerBar, infoBar, getViewDisplayPanel(), footerBar);
	}

	public DefaultHeaderBar getHeaderBar() {
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