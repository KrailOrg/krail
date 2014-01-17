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
package uk.co.q3c.v7.base.navigate.sitemap;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.jodatime.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.fest.assertions.Fail;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import uk.co.q3c.v7.base.navigate.StrictURIFragmentHandler;
import uk.co.q3c.v7.base.navigate.URIFragmentHandler;
import uk.co.q3c.v7.base.navigate.sitemap.DefaultFileSitemapLoaderTest.TestFileSitemapModule;
import uk.co.q3c.v7.base.view.testviews.subview.MoneyInOutView;
import uk.co.q3c.v7.base.view.testviews.subview.NotV7View;
import uk.co.q3c.v7.base.view.testviews.subview.TransferView;
import uk.co.q3c.v7.i18n.AnnotationI18NTranslator;
import uk.co.q3c.v7.i18n.I18NTranslator;
import uk.co.q3c.v7.i18n.TestLabelKey;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;

import fixture.testviews2.My_AccountView;
import fixture.testviews2.OptionsView;

/**
 * There are several things set up to help with testing. The sitemap.properties file can be modified using
 * {@link #substitute(String, String)}, {@link #deleteLine(String)}, {@link #insertAfter(String, String)} <br>
 * <br>
 * {@link #outputModifiedFile()} will show the changes made<br>
 * <br>
 * The sitemap.getReport() is useful for debugging.<br>
 * <br>
 * Sitemap.toString() will show the page structure
 * 
 * @author dsowerby
 * 
 */
@RunWith(MycilaJunitRunner.class)
@GuiceContext({ TestFileSitemapModule.class })
public class DefaultFileSitemapLoaderTest {

	public static class TestFileSitemapModule extends FileSitemapModule {

		@Override
		protected void define() {
			addEntry("a", new SitemapFile("src/test/java/uk/co/q3c/v7/base/navigate/sitemap_good.properties"));
		}

	}

	private static int COMMENT_LINES = 12;
	private static int BLANK_LINES = 9;
	private static int PAGE_COUNT = 4;
	private static File propDir;
	private File propFile;
	private static File modifiedFile;
	private List<String> lines;
	@Inject
	DefaultFileSitemapLoader loader;

	@BeforeClass
	public static void beforeClass() {
		propDir = new File("src/test/java/uk/co/q3c/v7/base/navigate");
		File modDir = new File(System.getProperty("user.home"));
		modifiedFile = new File(modDir, "temp/sitemap.properties");
	}

	@Before
	public void setup() throws IOException {
		loadMasterFile();
	}

	/**
	 * Map does not define every page, some are automatically added standard pages
	 * 
	 * @throws IOException
	 */
	@Test
	public void parse_partialMap() throws IOException {

		// given
		DateTime start = DateTime.now();
		// when
		assertThat(propFile.exists()).isTrue();
		loader.load();
		// then
		assertThat(loader.getSitemap()).isNotNull();
		assertThat(loader.getCommentLines()).isEqualTo(COMMENT_LINES);
		assertThat(loader.getBlankLines()).isEqualTo(BLANK_LINES);
		assertThat(loader.getSections()).containsOnly("viewPackages", "options", "map", "redirects");
		assertThat(loader.isLabelClassMissing()).isFalse();
		assertThat(loader.isLabelClassNonExistent()).isFalse();
		assertThat(loader.isLabelClassNotI18N()).isFalse();
		assertThat(loader.getLabelKeys()).isEqualTo("uk.co.q3c.v7.i18n.TestLabelKey");
		assertThat(loader.isAppendView()).isTrue();
		assertThat(loader.getLabelKeysClass()).isEqualTo(TestLabelKey.class);
		assertThat(loader.getViewPackages()).containsOnly("fixture.testviews2", "uk.co.q3c.v7.base.view.testviews");
		assertThat(loader.getMissingEnums()).isEmpty();

		System.out.println(loader.getSitemap().getReport());
		System.out.println(loader.getSitemap().toString());
		assertThat(loader.getSitemap().getNodeCount()).isEqualTo(PAGE_COUNT);
		assertThat(loader.missingSections().size()).isEqualTo(0);
		assertThat(loader.isLabelClassNonExistent()).isFalse();
		assertThat(loader.isLabelClassNotI18N()).isFalse();

		assertThat(loader.getMissingEnums()).containsOnly();
		assertThat(loader.getInvalidViewClasses()).containsOnly();
		for (String uv : loader.getUndeclaredViewClasses()) {
			System.out.println(uv);
		}
		assertThat(loader.getUndeclaredViewClasses()).containsOnly();
		assertThat(loader.getIndentationErrors()).containsOnly();

		Sitemap sitemap = loader.getSitemap();
		List<SitemapNode> roots = sitemap.getRoots();
		assertThat(roots.size()).isEqualTo(1);

		System.out.println(sitemap.toString());

		Collection<SitemapNode> nodes = loader.getSitemap().getAllNodes();
		for (SitemapNode node : nodes) {
			validateNode(sitemap, node);
		}

		assertThat(loader.getStartTime()).isNotNull();
		assertThat(loader.getEndTime()).isNotNull();
		assertThat(loader.getStartTime().getMillis()).isGreaterThanOrEqualTo(start.getMillis());
		assertThat(loader.getEndTime().isAfter(loader.getStartTime())).isTrue();

		System.out.println(loader.getReport());
		assertThat(loader.getSitemap().hasErrors()).isFalse();

		System.out.println(loader.getSitemap());
	}

	@Test
	public void keyName() {

		// given
		SitemapNode node = new SitemapNode();
		node.setUriSegment("reset-account");
		// when
		String keyName = loader.keyName(null, node);
		// then

		assertThat(keyName).isEqualTo("Reset_Account");
	}

	@Test
	public void sectionMissingClosingBracket() throws IOException {

		// given
		// given
		substitute("[viewPackages]", "[viewPackages");
		prepFile();
		// when
		loader.parse(modifiedFile);
		// then

		assertThat(loader.isLabelClassNonExistent()).isFalse();
		assertThat(loader.isLabelClassNotI18N()).isFalse();
		assertThat(loader.missingSections()).containsOnly("viewPackages");

		assertThat(loader.getPagesDefined()).isEqualTo(0);
		assertThat(loader.getViewPackages()).isNull();
		assertThat(loader.getPropertyErrors()).containsOnly();
		assertThat(loader.getMissingEnums()).containsOnly();
		assertThat(loader.getInvalidViewClasses()).containsOnly();
		assertThat(loader.getUndeclaredViewClasses()).containsOnly();
		assertThat(loader.getIndentationErrors()).containsOnly();
		// assertThat(reader.getSitemap().hasErrors()).isTrue();
		System.out.println(loader.getReport());
	}

	@Test
	public void invalidPropertyName() throws IOException {

		insertAfter("labelKeys=uk.co.q3c.v7.i18n.TestLabelKey", "randomProperty=23");
		prepFile();
		// when
		loader.parse(modifiedFile);
		// then

		assertThat(loader.isLabelClassNonExistent()).isFalse();
		assertThat(loader.isLabelClassNotI18N()).isFalse();
		assertThat(loader.missingSections()).containsOnly();

		assertThat(loader.getPagesDefined()).isEqualTo(PAGE_COUNT);
		assertThat(loader.getViewPackages()).containsOnly("fixture.testviews2", "uk.co.q3c.v7.base.view.testviews");
		assertThat(loader.getPropertyErrors()).containsOnly();
		assertThat(loader.getMissingEnums()).containsOnly();
		assertThat(loader.getInvalidViewClasses()).containsOnly();
		assertThat(loader.getUndeclaredViewClasses()).containsOnly();
		assertThat(loader.getIndentationErrors()).containsOnly();
		// assertThat(reader.getSitemap().hasErrors()).isFalse();

		System.out.println(loader.getReport());

	}

	@Test
	public void invalidSectionName() throws IOException {

		// given
		substitute("[options]", "[option]");
		prepFile();
		// when
		loader.parse(modifiedFile);
		// then

		assertThat(loader.missingSections()).containsOnly("options");

		assertThat(loader.isLabelClassNonExistent()).isFalse();
		assertThat(loader.isLabelClassNotI18N()).isFalse();
		assertThat(loader.getPagesDefined()).isEqualTo(0);
		assertThat(loader.getPropertyErrors()).containsOnly();
		assertThat(loader.getMissingEnums()).containsOnly();
		assertThat(loader.getInvalidViewClasses()).containsOnly();
		assertThat(loader.getUndeclaredViewClasses()).containsOnly();
		assertThat(loader.getIndentationErrors()).containsOnly();
		assertThat(loader.getSitemap().hasErrors()).isTrue();

		System.out.println(loader.getReport());
	}

	/**
	 * Does not implement i18N
	 * 
	 * @throws IOException
	 */
	@Test
	public void invalidLabelKeysClass_no_i18N() throws IOException {

		// given
		substitute("labelKeys=uk.co.q3c.v7.i18n.TestLabelKey", "labelKeys=uk.co.q3c.v7.i18n.TestLabelKey_Invalid");
		prepFile();
		// when
		loader.parse(modifiedFile);
		// then

		assertThat(loader.isLabelClassMissing()).isFalse();
		assertThat(loader.isLabelClassNonExistent()).isFalse();
		assertThat(loader.isLabelClassNotI18N()).isTrue();
		assertThat(loader.missingSections()).containsOnly();

		assertThat(loader.getPagesDefined()).isEqualTo(PAGE_COUNT);
		assertThat(loader.getViewPackages()).containsOnly("fixture.testviews2", "uk.co.q3c.v7.base.view.testviews");
		assertThat(loader.getPropertyErrors()).containsOnly();
		assertThat(loader.getMissingEnums()).contains("MoneyInOut", "Transfers", "Opt");
		assertThat(loader.getInvalidViewClasses()).containsOnly();
		assertThat(loader.getUndeclaredViewClasses()).containsOnly();
		assertThat(loader.getIndentationErrors()).containsOnly();
		assertThat(loader.getSitemap().hasErrors()).isTrue();
		System.out.println(loader.getReport());
	}

	/**
	 * Does not exist
	 * 
	 * @throws IOException
	 */
	@Test
	public void invalidLabelKeysClass_does_not_exist() throws IOException {
		// given
		substitute("labelKeys=uk.co.q3c.v7.i18n.TestLabelKey", "labelKeys=uk.co.q3c.v7.i18n.TestLabelKey2");
		prepFile();
		// when
		loader.parse(modifiedFile);
		// then
		assertThat(loader.isLabelClassMissing()).isFalse();
		assertThat(loader.isLabelClassNonExistent()).isTrue();
		assertThat(loader.isLabelClassNotI18N()).isTrue();
		assertThat(loader.missingSections()).containsOnly();

		assertThat(loader.getPagesDefined()).isEqualTo(PAGE_COUNT);
		assertThat(loader.getViewPackages()).containsOnly("fixture.testviews2", "uk.co.q3c.v7.base.view.testviews");
		assertThat(loader.getPropertyErrors()).containsOnly();
		// Counting precisely is irrelevant as LabelKeys class missing
		assertThat(loader.getMissingEnums().size()).isGreaterThan(0);
		assertThat(loader.getInvalidViewClasses()).containsOnly();
		assertThat(loader.getUndeclaredViewClasses()).containsOnly();
		assertThat(loader.getIndentationErrors()).containsOnly();
		assertThat(loader.getSitemap().hasErrors()).isTrue();

		System.out.println(loader.getReport());
	}

	@Test
	public void viewNotFound() throws IOException {

		substituteIn(29, "subview.Transfer", "subview.Transfers");
		prepFile();
		outputModifiedFile();
		// when
		loader.parse(modifiedFile);
		// then

		assertThat(loader.isLabelClassNonExistent()).isFalse();
		assertThat(loader.isLabelClassNotI18N()).isFalse();
		assertThat(loader.missingSections()).containsOnly();

		assertThat(loader.getPagesDefined()).isEqualTo(PAGE_COUNT);
		assertThat(loader.getViewPackages()).containsOnly("fixture.testviews2", "uk.co.q3c.v7.base.view.testviews");
		assertThat(loader.getPropertyErrors()).containsOnly();
		assertThat(loader.getMissingEnums()).containsOnly();
		assertThat(loader.getInvalidViewClasses()).containsOnly();
		assertThat(loader.getUndeclaredViewClasses()).containsOnly("subview.TransfersView");
		assertThat(loader.getIndentationErrors()).containsOnly();
		assertThat(loader.getSitemap().hasErrors()).isTrue();
		System.out.println(loader.getReport());
	}

	@Test
	public void viewNotV7View() throws IOException {

		substituteIn(30, "subview.MoneyInOut", "subview.NotV7");
		prepFile();
		// when
		loader.parse(modifiedFile);

		// then
		assertThat(loader.isLabelClassNonExistent()).isFalse();
		assertThat(loader.isLabelClassNotI18N()).isFalse();
		assertThat(loader.missingSections()).containsOnly();

		assertThat(loader.getPagesDefined()).isEqualTo(PAGE_COUNT);
		assertThat(loader.getViewPackages()).containsOnly("fixture.testviews2", "uk.co.q3c.v7.base.view.testviews");
		assertThat(loader.getPropertyErrors()).containsOnly();
		assertThat(loader.getMissingEnums()).containsOnly();
		assertThat(loader.getInvalidViewClasses()).containsOnly(NotV7View.class.getName());
		assertThat(loader.getUndeclaredViewClasses()).containsOnly();
		assertThat(loader.getIndentationErrors()).containsOnly();
		assertThat(loader.getSitemap().hasErrors()).isTrue();
		System.out.println(loader.getReport());
	}

	/**
	 * Tries to go out of structure by double indenting from previous
	 * 
	 * @throws IOException
	 */
	@Test
	public void mapIndentTooGreat() throws IOException {

		substituteIn(29, "+-transfers", "+---transfers");
		prepFile();
		// when
		loader.parse(modifiedFile);

		// then
		System.out.println(loader.getReport());
		assertThat(loader.isLabelClassNonExistent()).isFalse();
		assertThat(loader.isLabelClassNotI18N()).isFalse();
		assertThat(loader.missingSections()).containsOnly();

		assertThat(loader.getPagesDefined()).isEqualTo(PAGE_COUNT);
		assertThat(loader.getViewPackages()).containsOnly("fixture.testviews2", "uk.co.q3c.v7.base.view.testviews");
		assertThat(loader.getPropertyErrors()).containsOnly();
		assertThat(loader.getMissingEnums()).containsOnly();
		assertThat(loader.getInvalidViewClasses()).containsOnly();
		assertThat(loader.getUndeclaredViewClasses()).containsOnly();
		assertThat(loader.getIndentationErrors()).containsOnly("'transfers' at line 2");
		assertThat(loader.getSitemap().hasErrors()).isFalse();

		System.out.println(loader.getSitemap().toString());
	}

	@Test
	public void options() throws IOException {

		// given
		prepFile();
		// when
		loader.parse(modifiedFile);
		// then
		assertThat(loader.isAppendView()).isTrue();
		assertThat(loader.getLabelKeys()).isEqualTo("uk.co.q3c.v7.i18n.TestLabelKey");

		// given properties not defined
		deleteLine("appendView=true");
		prepFile();

		// when
		loader.parse(modifiedFile);
		// then defaults correct
		assertThat(loader.isAppendView()).isTrue();

	}

	@Test
	public void options_to_non_default() throws IOException {

		// given properties set to non-default
		substitute("appendView=true", "appendView=false");
		prepFile();
		// when
		loader.parse(modifiedFile);
		// then values correct
		assertThat(loader.isAppendView()).isFalse();

	}

	private void validateNode(Sitemap sitemap, SitemapNode node) {
		String uri = sitemap.uri(node);
		switch (uri) {

		case "my-account":
			assertThat(sitemap.getChildCount(node)).isEqualTo(3);
			assertThat(node.getUriSegment()).isEqualTo("my-account");
			assertThat(node.getViewClass()).isEqualTo(My_AccountView.class);
			assertThat(node.getLabelKey()).isEqualTo(TestLabelKey.My_Account);
			assertThat(node.isPublicPage()).isTrue();
			assertThat(node.hasPermissions()).isFalse();
			break;

		case "my-account/transfers":
			assertThat(sitemap.getChildCount(node)).isEqualTo(0);
			assertThat(node.getUriSegment()).isEqualTo("transfers");
			assertThat(node.getViewClass()).isEqualTo(TransferView.class);
			assertThat(node.getLabelKey()).isEqualTo(TestLabelKey.Transfers);
			assertThat(node.isPublicPage()).isTrue();
			assertThat(node.hasPermissions()).isFalse();
			break;

		case "my-account/money-in-out":
			assertThat(sitemap.getChildCount(node)).isEqualTo(0);
			assertThat(node.getUriSegment()).isEqualTo("money-in-out");
			assertThat(node.getViewClass()).isEqualTo(MoneyInOutView.class);
			assertThat(node.getLabelKey()).isEqualTo(TestLabelKey.MoneyInOut);
			assertThat(node.isPublicPage()).isFalse();
			assertThat(node.hasPermissions()).isTrue();
			break;

		case "my-account/options":
			assertThat(sitemap.getChildCount(node)).isEqualTo(0);
			assertThat(node.getUriSegment()).isEqualTo("options");
			assertThat(node.getViewClass()).isEqualTo(OptionsView.class);
			assertThat(node.getLabelKey()).isEqualTo(TestLabelKey.Opt);
			assertThat(node.isPublicPage()).isFalse();
			assertThat(node.hasPermissions()).isTrue();
			break;

		default:
			Fail.fail("unexpected uri: '" + uri + "'");
		}
	}

	private void loadMasterFile() throws IOException {
		propFile = new File(propDir, "sitemap_good.properties");
		lines = FileUtils.readLines(propFile);
	}

	private void prepFile() throws IOException {
		FileUtils.writeLines(modifiedFile, lines);
	}

	private void substitute(String original, String replacement) {
		int index = lines.indexOf(original);
		if (index >= 0) {
			lines.remove(index);
		} else {
			throw new RuntimeException("Subsitution failed in test setup, " + original + " was not found");
		}
		if (replacement != null) {
			lines.add(index, replacement);
		}
	}

	private void substituteIn(int lineNum, String original, String replacement) {
		String line = lines.get(lineNum);
		if (line.contains(original)) {
			line = line.replace(original, replacement);
			lines.add(lineNum, line);
		} else {
			throw new RuntimeException("Subsitution failed in test setup, " + original + " was not found in line "
					+ lineNum);
		}

	}

	private void deleteLine(String original) {
		int index = lines.indexOf(original);
		if (index >= 0) {
			lines.remove(index);
		}
	}

	private void insertAfter(String reference, String insertion) {
		int index = lines.indexOf(reference);
		lines.add(index + 1, insertion);
	}

	private void outputModifiedFile() {
		for (String line : lines) {
			System.out.println(line);
		}
	}

	@ModuleProvider
	protected AbstractModule module() {
		return new AbstractModule() {

			@Override
			protected void configure() {
				bind(I18NTranslator.class).to(AnnotationI18NTranslator.class);
				bind(URIFragmentHandler.class).to(StrictURIFragmentHandler.class);
			}

		};
	}

}
