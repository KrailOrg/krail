package uk.co.q3c.v7.base.navigate.sitemap.comparator;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import uk.co.q3c.v7.base.navigate.sitemap.comparator.DefaultUserSitemapSorters.SortType;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class DefaultUserSitemapSortersTest {

	@Inject
	DefaultUserSitemapSorters sorters;

	@Test
	public void defaultSort() {

		// given

		// when

		// then
		assertThat(sorters.getSortComparator()).isInstanceOf(AlphabeticAscending.class);
	}

	@Test
	public void selection() {

		// given

		// when
		sorters.setSortAscending(false);
		// then
		assertThat(sorters.getSortComparator()).isInstanceOf(AlphabeticDescending.class);
		// when
		sorters.setSortType(SortType.INSERTION);
		// then
		assertThat(sorters.getSortComparator()).isInstanceOf(InsertionOrderDescending.class);
		// when
		sorters.setSortAscending(true);
		// then
		assertThat(sorters.getSortComparator()).isInstanceOf(InsertionOrderAscending.class);
		sorters.setSortType(SortType.POSITION);
		// then
		assertThat(sorters.getSortComparator()).isInstanceOf(PositionIndexAscending.class);
		// when
		sorters.setSortAscending(false);
		// then
		assertThat(sorters.getSortComparator()).isInstanceOf(PositionIndexDescending.class);
	}

}
