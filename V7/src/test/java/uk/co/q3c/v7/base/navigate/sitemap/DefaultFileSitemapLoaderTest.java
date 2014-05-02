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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.assertj.jodatime.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import uk.co.q3c.v7.base.guice.vsscope.VaadinSessionScopeModule;
import uk.co.q3c.v7.base.navigate.StrictURIFragmentHandler;
import uk.co.q3c.v7.base.navigate.URIFragmentHandler;
import uk.co.q3c.v7.base.navigate.sitemap.DefaultFileSitemapLoaderTest.TestFileSitemapModule;
import uk.co.q3c.v7.base.navigate.sitemap.SitemapLoader.LoaderErrorEntry;
import uk.co.q3c.v7.base.navigate.sitemap.SitemapLoader.LoaderInfoEntry;
import uk.co.q3c.v7.base.navigate.sitemap.SitemapLoader.LoaderWarningEntry;
import uk.co.q3c.v7.base.view.testviews.subview.MoneyInOutView;
import uk.co.q3c.v7.base.view.testviews.subview.TransferView;
import uk.co.q3c.v7.i18n.DefaultI18NProcessor;
import uk.co.q3c.v7.i18n.I18NModule;
import uk.co.q3c.v7.i18n.I18NProcessor;
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
@GuiceContext({ TestFileSitemapModule.class, I18NModule.class, VaadinSessionScopeModule.class })
public class DefaultFileSitemapLoaderTest {

	public static class TestFileSitemapModule extends FileSitemapModule {

		@Override
		protected void define() {
			addEntry("a", new SitemapFile(modifiedFile.getAbsolutePath()));
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

	List<SitemapLoader> loaders;

	LoaderReportBuilder lrb;

	@BeforeClass
	public static void beforeClass() {
		propDir = new File("src/test/java/uk/co/q3c/v7/base/navigate");
		File modDir = new File(System.getProperty("user.home"));
		modifiedFile = new File(modDir, "temp/sitemap.properties");
	}

	@Before
	public void setup() throws IOException {
		loadMasterFile();
		loaders = new ArrayList<>();
		loaders.add(loader);
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
		prepFile();
		// when
		assertThat(propFile.exists()).isTrue();
		loader.load();
		lrb = new LoaderReportBuilder(loaders);
		loader.getSitemap().setReport(lrb.getReport().toString());
		// then
		assertThat(loader.getSitemap()).isNotNull();
		assertThat(loader.getCommentLines()).isEqualTo(COMMENT_LINES);
		assertThat(loader.getBlankLines()).isEqualTo(BLANK_LINES);
		assertThat(loader.getSections()).containsOnly("viewPackages", "options", "map", "redirects");

		assertThat(loader.getLabelKey()).isEqualTo("uk.co.q3c.v7.i18n.TestLabelKey");
		assertThat(loader.isAppendView()).isTrue();
		assertThat(loader.getLabelKeysClass()).isEqualTo(TestLabelKey.class);
		assertThat(loader.getViewPackages()).containsOnly("fixture.testviews2", "uk.co.q3c.v7.base.view.testviews");
		assertThat(loader.getMissingEnums()).isEmpty();

		System.out.println(loader.getSitemap().getReport());
		System.out.println(loader.getSitemap().toString());
		assertThat(loader.getSitemap().getNodeCount()).isEqualTo(PAGE_COUNT);
		assertThat(loader.missingSections().size()).isEqualTo(0);
		assertThat(loader.getErrorCount()).isEqualTo(0);
		assertThat(loader.getWarningCount()).isEqualTo(0);
		assertThat(loader.getInfoCount()).isEqualTo(0);

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
		substitute("[viewPackages]", "[viewPackages");
		prepFile();
		// when
		loader.load();
		lrb = new LoaderReportBuilder(loaders);
		loader.getSitemap().setReport(lrb.getReport().toString());
		// then

		assertThat(loader.missingSections()).containsOnly("viewPackages");
		assertThat(loader.getErrorCount()).isEqualTo(1);
		assertThat(loader.getWarningCount()).isEqualTo(1);
		assertThat(loader.getInfoCount()).isEqualTo(0);
		assertThat(containsError(FileSitemapLoader.SECTION_MISSING)).isTrue();
		assertThat(containsWarning(FileSitemapLoader.SECTION_MISSING_CLOSING)).isTrue();
		assertThat(loader.getPagesDefined()).isEqualTo(0);
		assertThat(loader.getViewPackages()).isNull();
		System.out.println(lrb.getReport());
	}

	@Test
	public void unrecognisedPropertyName() throws IOException {
		// given
		insertAfter("labelKeys=uk.co.q3c.v7.i18n.TestLabelKey", "randomProperty=23");
		prepFile();
		// when
		loader.load();
		lrb = new LoaderReportBuilder(loaders);
		loader.getSitemap().setReport(lrb.getReport().toString());
		// then

		assertThat(loader.missingSections()).containsOnly();

		assertThat(loader.getPagesDefined()).isEqualTo(PAGE_COUNT);
		assertThat(loader.getViewPackages()).containsOnly("fixture.testviews2", "uk.co.q3c.v7.base.view.testviews");

		assertThat(loader.getMissingEnums()).containsOnly();
		assertThat(loader.getErrorCount()).isEqualTo(0);
		assertThat(loader.getWarningCount()).isEqualTo(1);
		assertThat(loader.getInfoCount()).isEqualTo(0);
		assertThat(containsWarning(FileSitemapLoader.PROPERTY_NAME_UNRECOGNISED)).isTrue();
		System.out.println(lrb.getReport());

	}

	@Test
	public void invalidSectionName() throws IOException {

		// given
		substitute("[options]", "[option]");
		prepFile();
		// when
		loader.load();
		lrb = new LoaderReportBuilder(loaders);
		loader.getSitemap().setReport(lrb.getReport().toString());
		// then

		assertThat(loader.missingSections()).containsOnly("options");

		assertThat(loader.getErrorCount()).isEqualTo(1);
		assertThat(loader.getWarningCount()).isEqualTo(1);
		assertThat(loader.getInfoCount()).isEqualTo(0);
		assertThat(containsWarning(FileSitemapLoader.SECTION_NOT_VALID_FOR_SITEMAP)).isTrue();
		assertThat(containsWarning(FileSitemapLoader.SECTION_MISSING)).isTrue();
		assertThat(loader.getPagesDefined()).isEqualTo(0);
		assertThat(loader.getMissingEnums()).containsOnly();

		System.out.println(lrb.getReport());
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
		loader.load();
		lrb = new LoaderReportBuilder(loaders);
		loader.getSitemap().setReport(lrb.getReport().toString());
		// then
		assertThat(loader.getErrorCount()).isEqualTo(2);
		assertThat(loader.getWarningCount()).isEqualTo(0);
		assertThat(loader.getInfoCount()).isEqualTo(0);

		assertThat(loader.missingSections()).isEmpty();
		assertThat(loader.getPagesDefined()).isEqualTo(PAGE_COUNT);
		assertThat(loader.getViewPackages()).containsOnly("fixture.testviews2", "uk.co.q3c.v7.base.view.testviews");
		assertThat(loader.getMissingEnums()).contains("MoneyInOut", "Transfers", "Opt");

		assertThat(containsError(FileSitemapLoader.LABELKEY_DOES_NOT_IMPLEMENT_I18N_KEY)).isTrue();
		assertThat(containsError(FileSitemapLoader.LABELKEY_NOT_VALID_CLASS_FOR_I18N_LABELS)).isTrue();
		System.out.println(lrb.getReport());
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
		loader.load();
		lrb = new LoaderReportBuilder(loaders);
		loader.getSitemap().setReport(lrb.getReport().toString());

		// then
		assertThat(loader.getErrorCount()).isEqualTo(2);
		assertThat(loader.getWarningCount()).isEqualTo(0);
		assertThat(loader.getInfoCount()).isEqualTo(0);

		assertThat(loader.missingSections()).isEmpty();
		assertThat(loader.getPagesDefined()).isEqualTo(PAGE_COUNT);
		assertThat(loader.getViewPackages()).containsOnly("fixture.testviews2", "uk.co.q3c.v7.base.view.testviews");
		assertThat(loader.getMissingEnums()).contains("MoneyInOut", "Transfers", "Opt");

		assertThat(containsError(FileSitemapLoader.LABELKEY_NOT_IN_CLASSPATH)).isTrue();
		assertThat(containsError(FileSitemapLoader.LABELKEY_NOT_VALID_CLASS_FOR_I18N_LABELS)).isTrue();
		System.out.println(lrb.getReport());
	}

	@Test
	public void viewNotFound() throws IOException {

		substituteIn(29, "subview.Transfer", "subview.Transfers");
		prepFile();
		outputModifiedFile();
		// when
		loader.load();
		lrb = new LoaderReportBuilder(loaders);
		loader.getSitemap().setReport(lrb.getReport().toString());
		// then
		assertThat(loader.getErrorCount()).isEqualTo(1);
		assertThat(loader.getWarningCount()).isEqualTo(0);
		assertThat(loader.getInfoCount()).isEqualTo(0);

		assertThat(loader.missingSections()).isEmpty();
		assertThat(loader.getPagesDefined()).isEqualTo(PAGE_COUNT);
		assertThat(loader.getViewPackages()).containsOnly("fixture.testviews2", "uk.co.q3c.v7.base.view.testviews");
		assertThat(loader.getMissingEnums()).isEmpty();

		assertThat(containsError(FileSitemapLoader.VIEW_NOT_FOUND_IN_SPECIFIED_PACKAGES)).isTrue();
		System.out.println(lrb.getReport());

	}

	@Test
	public void viewNotV7View() throws IOException {

		substituteIn(30, "subview.MoneyInOut", "subview.NotV7");
		prepFile();
		// when
		loader.load();
		lrb = new LoaderReportBuilder(loaders);
		loader.getSitemap().setReport(lrb.getReport().toString());

		// then
		assertThat(loader.getErrorCount()).isEqualTo(1);
		assertThat(loader.getWarningCount()).isEqualTo(0);
		assertThat(loader.getInfoCount()).isEqualTo(0);

		assertThat(loader.missingSections()).isEmpty();
		assertThat(loader.getPagesDefined()).isEqualTo(PAGE_COUNT);
		assertThat(loader.getViewPackages()).containsOnly("fixture.testviews2", "uk.co.q3c.v7.base.view.testviews");
		assertThat(loader.getMissingEnums()).isEmpty();

		assertThat(containsError(FileSitemapLoader.VIEW_DOES_NOT_IMPLEMENT_V7VIEW)).isTrue();
		System.out.println(lrb.getReport());

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
		loader.load();
		lrb = new LoaderReportBuilder(loaders);
		loader.getSitemap().setReport(lrb.getReport().toString());

		// then
		assertThat(loader.getErrorCount()).isEqualTo(0);
		assertThat(loader.getWarningCount()).isEqualTo(1);
		assertThat(loader.getInfoCount()).isEqualTo(0);

		assertThat(loader.missingSections()).isEmpty();
		assertThat(loader.getPagesDefined()).isEqualTo(PAGE_COUNT);
		assertThat(loader.getViewPackages()).containsOnly("fixture.testviews2", "uk.co.q3c.v7.base.view.testviews");
		assertThat(loader.getMissingEnums()).isEmpty();

		assertThat(containsWarning(FileSitemapLoader.LINE_FORMAT_INDENTATION_INCORRECT)).isTrue();
		System.out.println(lrb.getReport());
	}

	@Test
	public void options() throws IOException {

		// given
		prepFile();
		// when
		loader.load();
		lrb = new LoaderReportBuilder(loaders);
		loader.getSitemap().setReport(lrb.getReport().toString());

		// then
		assertThat(loader.isAppendView()).isTrue();
		assertThat(loader.getLabelKey()).isEqualTo("uk.co.q3c.v7.i18n.TestLabelKey");

		// given properties not defined
		deleteLine("appendView=true");
		prepFile();

		// when
		loader.load();
		lrb = new LoaderReportBuilder(loaders);
		loader.getSitemap().setReport(lrb.getReport().toString());

		// then defaults correct
		assertThat(loader.isAppendView()).isTrue();

	}

	@Test
	public void options_to_non_default() throws IOException {

		// given properties set to non-default
		substitute("appendView=true", "appendView=false");
		prepFile();
		// when
		loader.load();
		lrb = new LoaderReportBuilder(loaders);
		loader.getSitemap().setReport(lrb.getReport().toString());
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
			assertThat(node.hasRoles()).isFalse();
			break;

		case "my-account/transfers":
			assertThat(sitemap.getChildCount(node)).isEqualTo(0);
			assertThat(node.getUriSegment()).isEqualTo("transfers");
			assertThat(node.getViewClass()).isEqualTo(TransferView.class);
			assertThat(node.getLabelKey()).isEqualTo(TestLabelKey.Transfers);
			assertThat(node.isPublicPage()).isTrue();
			assertThat(node.hasRoles()).isFalse();
			break;

		case "my-account/money-in-out":
			assertThat(sitemap.getChildCount(node)).isEqualTo(0);
			assertThat(node.getUriSegment()).isEqualTo("money-in-out");
			assertThat(node.getViewClass()).isEqualTo(MoneyInOutView.class);
			assertThat(node.getLabelKey()).isEqualTo(TestLabelKey.MoneyInOut);
			assertThat(node.isPublicPage()).isFalse();
			assertThat(node.hasRoles()).isTrue();
			break;

		case "my-account/options":
			assertThat(sitemap.getChildCount(node)).isEqualTo(0);
			assertThat(node.getUriSegment()).isEqualTo("options");
			assertThat(node.getViewClass()).isEqualTo(OptionsView.class);
			assertThat(node.getLabelKey()).isEqualTo(TestLabelKey.Opt);
			assertThat(node.isPublicPage()).isFalse();
			assertThat(node.hasRoles()).isTrue();
			break;

		default:
			fail("unexpected uri: '" + uri + "'");
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
				bind(I18NProcessor.class).to(DefaultI18NProcessor.class);
				bind(URIFragmentHandler.class).to(StrictURIFragmentHandler.class);
			}

		};
	}

	private boolean containsError(String pattern) {
		Map<String, List<LoaderErrorEntry>> errorMap = loader.getErrors();
		if (errorMap.isEmpty()) {
			return false;
		} else {
			for (String key : errorMap.keySet()) {
				System.out.println(key);
			}
			return true;
		}
	}

	private boolean containsWarning(String pattern) {
		Map<String, List<LoaderWarningEntry>> errorMap = loader.getWarnings();
		if (errorMap.isEmpty()) {
			return false;
		} else {
			for (String key : errorMap.keySet()) {
				System.out.println(key);
			}
			return true;
		}
	}

	private boolean containsInfo(String pattern) {
		Map<String, List<LoaderInfoEntry>> errorMap = loader.getInfos();
		if (errorMap.isEmpty()) {
			return false;
		} else {
			for (String key : errorMap.keySet()) {
				System.out.println(key);
			}
			return true;
		}
	}

}
