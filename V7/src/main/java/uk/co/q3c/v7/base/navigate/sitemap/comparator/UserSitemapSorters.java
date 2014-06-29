package uk.co.q3c.v7.base.navigate.sitemap.comparator;

import java.util.Comparator;

import uk.co.q3c.v7.base.navigate.sitemap.UserSitemapNode;
import uk.co.q3c.v7.base.navigate.sitemap.comparator.DefaultUserSitemapSorters.SortType;

/**
 * A set of comparators which can be used to sort {@link UserSitemapNode}s, with a lookup key and name to support
 * selection by an end user
 * 
 * @author dsowerby
 *
 */
public interface UserSitemapSorters {

	void setSortAscending(boolean ascending);

	void setSortType(SortType sortType);

	Comparator<UserSitemapNode> getSortComparator();

}
