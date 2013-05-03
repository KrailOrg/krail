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

import org.joda.time.DateTime;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import uk.co.q3c.v7.base.config.V7Ini.StandardPageKey;
import uk.co.q3c.v7.base.view.testviews.subview.MoneyInOutView;
import uk.co.q3c.v7.base.view.testviews.subview.NotV7View;
import uk.co.q3c.v7.base.view.testviews.subview.TransferView;
import uk.co.q3c.v7.i18n.TestLabelKeys;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

import fixture.testviews2.LoginView;
import fixture.testviews2.OptionsView;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class TextReaderSiteMapTest {

	private static File propDir;
	private File propFile;
	@Inject
	TextReaderSiteMapProvider reader;

	@BeforeClass
	public static void beforeClass() {
		propDir = new File("test/main/java/uk/co/q3c/v7/base/navigate");

	}

	@Test
	public void parse() throws IOException {

		// given
		String propFileName = "sitemap_good.properties";
		propFile = new File(propDir, propFileName);
		DateTime start = DateTime.now();
		// when
		assertThat(propFile.exists()).isTrue();
		reader.parse(propFile);
		// then
		assertThat(reader.getSiteMap()).isNotNull();
		assertThat(reader.getCommentLines()).isEqualTo(24);
		assertThat(reader.getBlankLines()).isEqualTo(9);
		assertThat(reader.getSections()).containsOnly("viewPackages", "options", "map", "standardPages", "redirects");
		assertThat(reader.getLabelKeys()).isEqualTo("uk.co.q3c.v7.i18n.TestLabelKeys");
		assertThat(reader.isAppendView()).isTrue();
		assertThat(reader.getLabelKeysClass()).isEqualTo(TestLabelKeys.class);
		assertThat(reader.getViewPackages()).containsOnly("fixture.testviews2", "uk.co.q3c.v7.base.view.testviews");
		assertThat(reader.getRedirects()).containsOnly(":public/home", "public:public/home", "secure:secure/home");
		assertThat(reader.getMissingEnums()).isEmpty();
		assertThat(reader.getSiteMap().getNodeCount()).isEqualTo(7);
		assertThat(reader.missingSections().size()).isEqualTo(0);
		assertThat(reader.isEnumNotExtant()).isFalse();
		assertThat(reader.isEnumNotI18N()).isFalse();

		assertThat(reader.getMissingEnums()).containsOnly();
		assertThat(reader.getInvalidViewClasses()).containsOnly();
		assertThat(reader.getUndeclaredViewClasses()).containsOnly();
		assertThat(reader.getIndentationErrors()).containsOnly();

		SiteMap tree = reader.getSiteMap();
		List<SiteMapNode> roots = tree.getRoots();
		assertThat(roots.size()).isEqualTo(3);

		System.out.println(tree.toString());

		for (SiteMapNode node : roots) {
			validateNode(tree, node);
		}

		assertThat(reader.getStartTime()).isNotNull();
		assertThat(reader.getEndTime()).isNotNull();
		assertThat(reader.getStartTime().getMillis()).isGreaterThanOrEqualTo(start.getMillis());
		assertThat(reader.getEndTime().isAfter(reader.getStartTime())).isTrue();

		assertThat(reader.getReport()).isNotNull();
		assertThat(reader.getReport().toString()).isNotEmpty();

		for (StandardPageKey spk : StandardPageKey.values()) {
			assertThat(reader.standardPageUrl(spk)).isNotNull();
		}
		System.out.println(reader.getReport().toString());

	}

	@Test
	public void sectionMissingClosingBracket() throws IOException {

		// given
		String propFileName = "sitemap_1.properties";
		propFile = new File(propDir, propFileName);
		// when
		assertThat(propFile.exists()).isTrue();
		reader.parse(propFile);
		// then

		assertThat(reader.isEnumNotExtant()).isFalse();
		assertThat(reader.isEnumNotI18N()).isFalse();
		assertThat(reader.missingSections()).containsOnly("viewPackages");

		assertThat(reader.getPagesDefined()).isEqualTo(0);
		assertThat(reader.getViewPackages()).isNull();
		assertThat(reader.getMissingPages()).containsOnly();
		assertThat(reader.getPropertyErrors()).containsOnly();
		assertThat(reader.getMissingEnums()).containsOnly();
		assertThat(reader.getInvalidViewClasses()).containsOnly();
		assertThat(reader.getUndeclaredViewClasses()).containsOnly();
		assertThat(reader.getIndentationErrors()).containsOnly();
		System.out.println(reader.getReport());
	}

	@Test
	public void invalidPropertyName() throws IOException {

		String propFileName = "sitemap_3.properties";
		propFile = new File(propDir, propFileName);
		// when
		assertThat(propFile.exists()).isTrue();
		reader.parse(propFile);
		// then

		assertThat(reader.isEnumNotExtant()).isFalse();
		assertThat(reader.isEnumNotI18N()).isFalse();
		assertThat(reader.missingSections()).containsOnly();

		assertThat(reader.getPagesDefined()).isEqualTo(7);
		assertThat(reader.getViewPackages()).containsOnly("fixture.testviews2", "uk.co.q3c.v7.base.view.testviews");
		assertThat(reader.getMissingPages()).containsOnly();
		assertThat(reader.getPropertyErrors()).containsOnly();
		assertThat(reader.getMissingEnums()).containsOnly();
		assertThat(reader.getInvalidViewClasses()).containsOnly();
		assertThat(reader.getUndeclaredViewClasses()).containsOnly("SecureView");
		assertThat(reader.getIndentationErrors()).containsOnly();

		System.out.println(reader.getReport());

	}

	@Test
	public void invalidSectionName() throws IOException {

		// given
		String propFileName = "sitemap_2.properties";
		propFile = new File(propDir, propFileName);
		// when
		assertThat(propFile.exists()).isTrue();
		reader.parse(propFile);
		// then
		assertThat(reader.missingSections()).containsOnly("options");

		assertThat(reader.isEnumNotExtant()).isFalse();
		assertThat(reader.isEnumNotI18N()).isFalse();
		assertThat(reader.getPagesDefined()).isEqualTo(0);
		assertThat(reader.getMissingPages()).containsOnly();
		assertThat(reader.getPropertyErrors()).containsOnly();
		assertThat(reader.getMissingEnums()).containsOnly();
		assertThat(reader.getInvalidViewClasses()).containsOnly();
		assertThat(reader.getUndeclaredViewClasses()).containsOnly();
		assertThat(reader.getIndentationErrors()).containsOnly();

		System.out.println(reader.getReport());
	}

	/**
	 * Does not implement i18N
	 * 
	 * @throws IOException
	 */
	@Test
	public void invalidLabelKeysClass_no_i18N() throws IOException {

		// given
		String propFileName = "sitemap_4.properties";
		propFile = new File(propDir, propFileName);
		// when
		assertThat(propFile.exists()).isTrue();
		reader.parse(propFile);
		// then

		assertThat(reader.isEnumNotExtant()).isFalse();
		assertThat(reader.isEnumNotI18N()).isTrue();
		assertThat(reader.missingSections()).containsOnly();

		assertThat(reader.getPagesDefined()).isEqualTo(7);
		assertThat(reader.getViewPackages()).containsOnly("fixture.testviews2", "uk.co.q3c.v7.base.view.testviews");
		assertThat(reader.getMissingPages()).containsOnly();
		assertThat(reader.getPropertyErrors()).containsOnly();
		assertThat(reader.getMissingEnums()).contains("moneyInOut", "home", "transfers", "login", "opt");
		assertThat(reader.getInvalidViewClasses()).containsOnly();
		assertThat(reader.getUndeclaredViewClasses()).containsOnly("SecureView");
		assertThat(reader.getIndentationErrors()).containsOnly();
		System.out.println(reader.getReport());
	}

	/**
	 * Does not exist
	 * 
	 * @throws IOException
	 */
	@Test
	public void invalidLabelKeysClass_does_not_exist() throws IOException {
		// given
		String propFileName = "sitemap_5.properties";
		propFile = new File(propDir, propFileName);
		// when
		assertThat(propFile.exists()).isTrue();
		reader.parse(propFile);
		// then
		assertThat(reader.isEnumNotExtant()).isTrue();
		assertThat(reader.isEnumNotI18N()).isFalse();
		assertThat(reader.missingSections()).containsOnly();

		assertThat(reader.getPagesDefined()).isEqualTo(7);
		assertThat(reader.getViewPackages()).containsOnly("fixture.testviews2", "uk.co.q3c.v7.base.view.testviews");
		assertThat(reader.getMissingPages()).containsOnly();
		assertThat(reader.getPropertyErrors()).containsOnly();
		assertThat(reader.getMissingEnums()).containsOnly("moneyInOut", "home", "transfers", "login", "opt");
		assertThat(reader.getInvalidViewClasses()).containsOnly();
		assertThat(reader.getUndeclaredViewClasses()).containsOnly("SecureView");
		assertThat(reader.getIndentationErrors()).containsOnly();

		System.out.println(reader.getReport());

	}

	@Test
	public void viewNotFound() throws IOException {

		String propFileName = "sitemap_3.properties";
		propFile = new File(propDir, propFileName);
		// when
		assertThat(propFile.exists()).isTrue();
		reader.parse(propFile);
		// then

		assertThat(reader.isEnumNotExtant()).isFalse();
		assertThat(reader.isEnumNotI18N()).isFalse();
		assertThat(reader.missingSections()).containsOnly();

		assertThat(reader.getPagesDefined()).isEqualTo(7);
		assertThat(reader.getViewPackages()).containsOnly("fixture.testviews2", "uk.co.q3c.v7.base.view.testviews");
		assertThat(reader.getMissingPages()).containsOnly();
		assertThat(reader.getPropertyErrors()).containsOnly();
		assertThat(reader.getMissingEnums()).containsOnly();
		assertThat(reader.getInvalidViewClasses()).containsOnly();
		assertThat(reader.getUndeclaredViewClasses()).containsOnly("SecureView");
		assertThat(reader.getIndentationErrors()).containsOnly();

		System.out.println(reader.getReport());

	}

	@Test
	public void viewNotV7View() throws IOException {

		String propFileName = "sitemap_6.properties";
		propFile = new File(propDir, propFileName);
		// when
		assertThat(propFile.exists()).isTrue();
		reader.parse(propFile);
		// then
		assertThat(reader.isEnumNotExtant()).isFalse();
		assertThat(reader.isEnumNotI18N()).isFalse();
		assertThat(reader.missingSections()).containsOnly();

		assertThat(reader.getPagesDefined()).isEqualTo(7);
		assertThat(reader.getViewPackages()).containsOnly("fixture.testviews2", "uk.co.q3c.v7.base.view.testviews");
		assertThat(reader.getMissingPages()).containsOnly();
		assertThat(reader.getPropertyErrors()).containsOnly();
		assertThat(reader.getMissingEnums()).containsOnly();
		assertThat(reader.getInvalidViewClasses()).containsOnly(NotV7View.class.getName());
		assertThat(reader.getUndeclaredViewClasses()).containsOnly("subview.NotV7View");
		assertThat(reader.getIndentationErrors()).containsOnly();
		System.out.println(reader.getReport());

	}

	/**
	 * Tries to go out of structure by double indenting from previous
	 * 
	 * @throws IOException
	 */
	@Test
	public void mapIndentTooGreat() throws IOException {

		String propFileName = "sitemap_7.properties";
		propFile = new File(propDir, propFileName);
		// when
		assertThat(propFile.exists()).isTrue();
		reader.parse(propFile);

		// then
		assertThat(reader.isEnumNotExtant()).isFalse();
		assertThat(reader.isEnumNotI18N()).isFalse();
		assertThat(reader.missingSections()).containsOnly();

		assertThat(reader.getPagesDefined()).isEqualTo(7);
		assertThat(reader.getViewPackages()).containsOnly("fixture.testviews2", "uk.co.q3c.v7.base.view.testviews");
		assertThat(reader.getMissingPages()).containsOnly();
		assertThat(reader.getPropertyErrors()).containsOnly();
		assertThat(reader.getMissingEnums()).containsOnly();
		assertThat(reader.getInvalidViewClasses()).containsOnly();
		assertThat(reader.getUndeclaredViewClasses()).containsOnly();
		assertThat(reader.getIndentationErrors()).containsOnly("transfers");

		System.out.println(reader.getReport());
		System.out.println(reader.getSiteMap().toString());
	}

	/**
	 * Try to call report before parsing anything
	 */
	@Test(expected = SiteMapException.class)
	public void reportBeforeParse() {

		// given

		// when
		reader.getReport();
		// then

	}

	@Test
	public void standardPageMissing() {

		String propFileName = "sitemap_8.properties";
		propFile = new File(propDir, propFileName);
		// when
		assertThat(propFile.exists()).isTrue();
		reader.parse(propFile);

		// then
		assertThat(reader.isEnumNotExtant()).isFalse();
		assertThat(reader.isEnumNotI18N()).isFalse();
		assertThat(reader.missingSections()).containsOnly();

		assertThat(reader.getPagesDefined()).isEqualTo(7);
		assertThat(reader.getViewPackages()).containsOnly("fixture.testviews2", "uk.co.q3c.v7.base.view.testviews");
		assertThat(reader.getMissingPages()).containsOnly("resetAccount", "requestAccount");
		assertThat(reader.getPropertyErrors()).containsOnly("Property resetAccount cannot have an empty value");
		assertThat(reader.getMissingEnums()).containsOnly();
		assertThat(reader.getInvalidViewClasses()).containsOnly();
		assertThat(reader.getUndeclaredViewClasses()).containsOnly();
		assertThat(reader.getIndentationErrors()).containsOnly();

		System.out.println(reader.getReport());
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

}
