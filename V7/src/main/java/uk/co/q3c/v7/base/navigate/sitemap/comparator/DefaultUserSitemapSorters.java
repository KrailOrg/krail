package uk.co.q3c.v7.base.navigate.sitemap.comparator;

import java.util.Comparator;

import uk.co.q3c.v7.base.navigate.sitemap.UserSitemapNode;

import com.google.inject.Inject;

/**
 * A set of comparators which can be used to sort {@link UserSitemapNode}s, with a lookup key and name to support
 * selection by an end user
 * 
 * @author dsowerby
 *
 */
public class DefaultUserSitemapSorters implements UserSitemapSorters {

	public enum SortType {
		ALPHA, INSERTION, POSITION
	};

	private boolean ascending = true;
	private SortType sortType = SortType.ALPHA;
	private Comparator<UserSitemapNode> selectedComparator;
	private final AlphabeticAscending alphaAscending;
	private final AlphabeticDescending alphaDescending;
	private final InsertionOrderAscending insertionAscending;
	private final InsertionOrderDescending insertionDescending;
	private final PositionIndexAscending positionAscending;
	private final PositionIndexDescending positionDescending;

	@Inject
	protected DefaultUserSitemapSorters(AlphabeticAscending alphaAscending, AlphabeticDescending alphaDescending,
			InsertionOrderAscending insertionAscending, InsertionOrderDescending insertionDescending,
			PositionIndexAscending positionAscending, PositionIndexDescending positionDescending) {
		this.alphaAscending = alphaAscending;
		this.alphaDescending = alphaDescending;
		this.insertionAscending = insertionAscending;
		this.insertionDescending = insertionDescending;
		this.positionAscending = positionAscending;
		this.positionDescending = positionDescending;
		select();
	}

	@Override
	public void setSortAscending(boolean ascending) {
		this.ascending = ascending;
		select();
	}

	@Override
	public void setSortType(SortType sortType) {
		this.sortType = sortType;
		select();
	}

	public boolean isAscending() {
		return ascending;
	}

	public SortType getSortType() {
		return sortType;
	}

	private void select() {
		switch (sortType) {
		case ALPHA:
			selectedComparator = (ascending) ? alphaAscending : alphaDescending;
			break;
		case INSERTION:
			selectedComparator = (ascending) ? insertionAscending : insertionDescending;
			break;
		case POSITION:
			selectedComparator = (ascending) ? positionAscending : positionDescending;
			break;
		}
	}

	@Override
	public Comparator<UserSitemapNode> getSortComparator() {
		return selectedComparator;
	}
}
