package uk.co.q3c.v7.base.navigate.sitemap;

import java.util.ArrayList;
import java.util.List;

import uk.co.q3c.v7.base.navigate.NavigationState;

import com.google.common.base.Joiner;

public interface MasterSitemap extends Sitemap<MasterSitemapNode> {

	public abstract String getReport();

	public abstract void setReport(String report);

	public abstract MasterSitemapNode append(String uri);

	public abstract MasterSitemapNode append(NavigationState navigationState);

}
