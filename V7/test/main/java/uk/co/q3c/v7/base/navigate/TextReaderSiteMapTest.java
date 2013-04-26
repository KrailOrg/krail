/*
 * Copyright (C) 2013 David Sowerby
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package uk.co.q3c.v7.base.navigate;

import static org.fest.assertions.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import uk.co.q3c.v7.base.view.testviews.subview.MoneyInOutView;
import uk.co.q3c.v7.base.view.testviews.subview.TransferView;
import uk.co.q3c.v7.i18n.TestLabelKeys;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

import fixture.testviews2.LoginView;
import fixture.testviews2.OptionsView;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class TextReaderSiteMapTest {

	private static final String propFileName = "sitemap.properties";
	static File propDir;
	private static File propFile;
	@Inject
	TextReaderSiteMapBuilder reader;

	@BeforeClass
	public static void beforeClass() {
		propDir = new File("test/main/java/uk/co/q3c/v7/base/navigate");
		propFile = new File(propDir, propFileName);
		System.out.println(propFile.getAbsolutePath());
	}

	@Before
	public void setup() {
		assertThat(propFile.exists()).isTrue();
	}

	@Test
	public void parse() throws IOException {

		// given

		// when
		reader.parse(propFile);
		// then
		assertThat(reader.getSiteMap()).isNotNull();
		assertThat(reader.getCommentLines()).isEqualTo(16);
		assertThat(reader.getBlankLines()).isEqualTo(5);
		assertThat(reader.getSections()).containsOnly("viewPackages", "options", "map");
		assertThat(reader.getLabelKeys()).isEqualTo("uk.co.q3c.v7.i18n.TestLabelKeys");
		assertThat(reader.isAppendView()).isTrue();
		assertThat(reader.getLabelKeysClass()).isEqualTo(TestLabelKeys.class);
		assertThat(reader.getViewPackages()).containsOnly("fixture.testviews2", "uk.co.q3c.v7.base.view.testviews");
		assertThat(reader.getMissingEnums()).isEmpty();
		assertThat(reader.getSiteMap().getNodeCount()).isEqualTo(7);

		SiteMap tree = reader.getSiteMap();
		List<SiteMapNode> roots = tree.getRoots();
		assertThat(roots.size()).isEqualTo(3);

		System.out.println(tree.toString());

		for (SiteMapNode node : roots) {
			validateNode(tree, node);
		}

	}

	private void validateNode(SiteMap tree, SiteMapNode node) {

		switch (node.getUrlSegment()) {
		case "":
			assertThat(tree.getChildCount(node)).isEqualTo(0);
			assertThat(tree.url(node)).isEqualTo("");
			break;

		case "public":
			assertThat(tree.getChildCount(node)).isEqualTo(1);
			assertThat(tree.url(node)).isEqualTo("public");
			SiteMapNode searchNode = new SiteMapNode("login", LoginView.class, TestLabelKeys.login);
			assertThat(tree.getChildren(node)).contains(searchNode);
			break;

		case "secure":
			assertThat(tree.getChildCount(node)).isEqualTo(3);
			assertThat(tree.url(node)).isEqualTo("secure");
			searchNode = new SiteMapNode("transfers", TransferView.class, TestLabelKeys.transfers);
			assertThat(tree.getChildren(node)).contains(searchNode);
			searchNode = new SiteMapNode("money-in-out", MoneyInOutView.class, TestLabelKeys.moneyInOut);
			assertThat(tree.getChildren(node)).contains(searchNode);
			searchNode = new SiteMapNode("options", OptionsView.class, TestLabelKeys.opt);
			assertThat(tree.getChildren(node)).contains(searchNode);
			break;

		case "transfers":
			assertThat(tree.getChildCount(node)).isEqualTo(0);
			assertThat(tree.url(node)).isEqualTo("secure/transfers");
			break;

		case "money-in-out":
			assertThat(tree.getChildCount(node)).isEqualTo(0);
			assertThat(tree.url(node)).isEqualTo("secure/money-in-out");
			break;

		case "options":
			assertThat(tree.getChildCount(node)).isEqualTo(0);
			assertThat(tree.url(node)).isEqualTo("secure/options");
			break;
		}
	}

	@Test
	public void sectionMissingClosingBracket() {

		// given

		// when

		// then
		assertThat(false).isEqualTo(true);

	}

	@Test
	public void invalidPropertyName() {

		// given

		// when

		// then
		assertThat(false).isEqualTo(true);

	}

	@Test
	public void invalidSectionName() {

		// given

		// when

		// then
		assertThat(false).isEqualTo(true);

	}

	@Test
	public void invalidLabelKeysClass() {

		// given

		// when

		// then
		assertThat(false).isEqualTo(true);

	}

	@Test
	public void viewNotFound() {

		// given

		// when

		// then
		assertThat(false).isEqualTo(true);

	}

	@Test
	public void viewNotV7View() {

		// given

		// when

		// then
		assertThat(false).isEqualTo(true);

	}

	/**
	 * Tries to go out of structure by double indenting from previous
	 */
	@Test
	public void mapIndentTooGreat() {

		// given

		// when

		// then
		assertThat(false).isEqualTo(true);

	}

}
