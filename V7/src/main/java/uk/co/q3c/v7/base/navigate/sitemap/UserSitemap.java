package uk.co.q3c.v7.base.navigate.sitemap;

public interface UserSitemap extends Sitemap<UserSitemapNode> {

	public abstract UserSitemapNode userNodeFor(SitemapNode masterNode);

	public abstract void buildUriMap();

}
