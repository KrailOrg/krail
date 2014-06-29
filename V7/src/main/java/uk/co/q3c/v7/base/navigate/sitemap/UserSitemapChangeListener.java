package uk.co.q3c.v7.base.navigate.sitemap;

/**
 * Listeners are notified when changes either occur to labels (usually as a result of switching Locale) or because the
 * structure of the sitemap has changed. The structure may change as a result of the user logging in / out, or as a
 * result of permissions changes
 * 
 * @author dsowerby
 *
 */
public interface UserSitemapChangeListener {
	/**
	 * 
	 * Fired when only the labels have changed - this typically happens as a result of switching Locale.
	 */
	void labelsChanged();

	/**
	 * Fired when a page is added / removed, or its position has changed. This will happen as a result of permission
	 * changes, logging in /out or potentially the dynamic addition / removal of pages (see
	 * https://github.com/davidsowerby/v7/issues/254).
	 */
	void structureChanged();
}
