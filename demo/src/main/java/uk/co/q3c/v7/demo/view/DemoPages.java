package uk.co.q3c.v7.demo.view;

import uk.co.q3c.v7.base.navigate.sitemap.DirectSitemapModule;
import uk.co.q3c.v7.base.shiro.PageAccessControl;
import uk.co.q3c.v7.i18n.LabelKey;

public class DemoPages extends DirectSitemapModule {

	@Override
	protected void define() {
		addEntry("notifications", NotificationsView.class, LabelKey.Notifications, PageAccessControl.PUBLIC);
	}

}