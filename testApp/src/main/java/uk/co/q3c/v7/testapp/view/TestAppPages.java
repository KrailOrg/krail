package uk.co.q3c.v7.testapp.view;

import uk.co.q3c.v7.base.navigate.sitemap.DirectSitemapModule;
import uk.co.q3c.v7.base.shiro.PageAccessControl;
import uk.co.q3c.v7.i18n.LabelKey;

public class TestAppPages extends DirectSitemapModule {

	@Override
	protected void define() {
		addEntry("notifications", NotificationsView.class, LabelKey.Notifications, PageAccessControl.PUBLIC);
		addEntry("widgetset", WidgetsetView.class, LabelKey.Message_Box, PageAccessControl.PUBLIC);
	}

}