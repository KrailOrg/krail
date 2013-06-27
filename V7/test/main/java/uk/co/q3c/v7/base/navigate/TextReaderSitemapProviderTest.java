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

import static org.fest.assertions.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.fest.assertions.Fail;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import uk.co.q3c.v7.base.view.LoginView;
import uk.co.q3c.v7.base.view.LogoutView;
import uk.co.q3c.v7.base.view.PublicHomeView;
import uk.co.q3c.v7.base.view.RequestSystemAccountEnableView;
import uk.co.q3c.v7.base.view.RequestSystemAccountRefreshView;
import uk.co.q3c.v7.base.view.RequestSystemAccountResetView;
import uk.co.q3c.v7.base.view.RequestSystemAccountUnlockView;
import uk.co.q3c.v7.base.view.RequestSystemAccountView;
import uk.co.q3c.v7.base.view.SecureHomeView;
import uk.co.q3c.v7.base.view.SystemAccountView;
import uk.co.q3c.v7.base.view.testviews.subview.MoneyInOutView;
import uk.co.q3c.v7.base.view.testviews.subview.NotV7View;
import uk.co.q3c.v7.base.view.testviews.subview.TransferView;
import uk.co.q3c.v7.i18n.TestLabelKeys;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

import fixture.testviews2.OptionsView;

/**
 * There aere several things set up to help with testing. The sitemap.properties file can be modified using
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
@GuiceContext({})
public class TextReaderSitemapProviderTest {
	private static int COMMENT_LINES = 9;
	private static int BLANK_LINES = 10;
	private static int PAGE_COUNT = 13;
	private static File propDir;
	private File propFile;
	private static File modifiedFile;
	private List<String> lines;
	@Inject
	TextReaderSitemapProvider reader;

	@BeforeClass
	public static void beforeClass() {
		propDir = new File("test/main/java/uk/co/q3c/v7/base/navigate");
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
		String propFileName = "sitemap_good.properties";
		propFile = new File(propDir, propFileName);
		DateTime start = DateTime.now();
		// when
		assertThat(propFile.exists()).isTrue();
		reader.parse(propFile);
		// then
		assertThat(reader.getSitemap()).isNotNull();
		assertThat(reader.getCommentLines()).isEqualTo(COMMENT_LINES);
		assertThat(reader.getBlankLines()).isEqualTo(BLANK_LINES);
		assertThat(reader.getSections()).containsOnly("viewPackages", "options", "map", "standardPageMapping",
				"redirects");
		assertThat(reader.isLabelClassMissing()).isFalse();
		assertThat(reader.isLabelClassNonExistent()).isFalse();
		assertThat(reader.isLabelClassNotI18N()).isFalse();
		assertThat(reader.getLabelKeys()).isEqualTo("uk.co.q3c.v7.i18n.TestLabelKeys");
		assertThat(reader.isAppendView()).isTrue();
		assertThat(reader.getLabelKeysClass()).isEqualTo(TestLabelKeys.class);
		assertThat(reader.getViewPackages()).containsOnly("fixture.testviews2", "uk.co.q3c.v7.base.view.testviews");
		assertThat(reader.redirectEntries()).containsOnly(":public");
		assertThat(reader.getMissingEnums()).isEmpty();

		System.out.println(reader.getSitemap().getReport());
		System.out.println(reader.getSitemap().toString());
		assertThat(reader.getSitemap().getNodeCount()).isEqualTo(PAGE_COUNT);
		assertThat(reader.missingSections().size()).isEqualTo(0);
		assertThat(reader.isLabelClassNonExistent()).isFalse();
		assertThat(reader.isLabelClassNotI18N()).isFalse();

		assertThat(reader.getMissingEnums()).containsOnly();
		assertThat(reader.getInvalidViewClasses()).containsOnly();
		assertThat(reader.getUndeclaredViewClasses()).containsOnly();
		assertThat(reader.getIndentationErrors()).containsOnly();

		Sitemap tree = reader.getSitemap();
		List<SitemapNode> roots = tree.getRoots();
		assertThat(roots.size()).isEqualTo(2);

		System.out.println(tree.toString());

		Collection<SitemapNode> nodes = reader.getSitemap().getEntries();
		for (SitemapNode node : nodes) {
			validateNode(tree, node);
		}

		assertThat(reader.getStartTime()).isNotNull();
		assertThat(reader.getEndTime()).isNotNull();
		assertThat(reader.getStartTime().getMillis()).isGreaterThanOrEqualTo(start.getMillis());
		assertThat(reader.getEndTime().isAfter(reader.getStartTime())).isTrue();

		assertThat(reader.getReport()).isNotNull();
		assertThat(reader.getReport().toString()).isNotEmpty();

		for (StandardPageKey spk : StandardPageKey.values()) {
			assertThat(reader.standardPageUri(spk)).overridingErrorMessage("not expecting null for " + spk.name())
					.isNotNull();
		}
		System.out.println(reader.getReport().toString());
		assertThat(reader.getSitemap().hasErrors()).isFalse();

		System.out.println(reader.getSitemap());
	}

	@Test
	public void keyName() {

		// given
		SitemapNode node = new SitemapNode();
		node.setUriSegment("reset-account");
		// when
		String keyName = reader.keyName(null, node);
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
		reader.parse(modifiedFile);
		// then

		assertThat(reader.isLabelClassNonExistent()).isFalse();
		assertThat(reader.isLabelClassNotI18N()).isFalse();
		assertThat(reader.missingSections()).containsOnly("viewPackages");

		assertThat(reader.getPagesDefined()).isEqualTo(0);
		assertThat(reader.getViewPackages()).isNull();
		assertThat(reader.getMissingPages()).containsOnly();
		assertThat(reader.getPropertyErrors()).containsOnly();
		assertThat(reader.getMissingEnums()).containsOnly();
		assertThat(reader.getInvalidViewClasses()).containsOnly();
		assertThat(reader.getUndeclaredViewClasses()).containsOnly();
		assertThat(reader.getIndentationErrors()).containsOnly();
		// assertThat(reader.getSitemap().hasErrors()).isTrue();
		System.out.println(reader.getReport());
	}

	@Test
	public void invalidPropertyName() throws IOException {

		insertAfter("labelKeys=uk.co.q3c.v7.i18n.TestLabelKeys", "randomProperty=23");
		prepFile();
		// when
		reader.parse(modifiedFile);
		// then

		assertThat(reader.isLabelClassNonExistent()).isFalse();
		assertThat(reader.isLabelClassNotI18N()).isFalse();
		assertThat(reader.missingSections()).containsOnly();

		assertThat(reader.getPagesDefined()).isEqualTo(PAGE_COUNT);
		assertThat(reader.getViewPackages()).containsOnly("fixture.testviews2", "uk.co.q3c.v7.base.view.testviews");
		assertThat(reader.getMissingPages()).containsOnly();
		assertThat(reader.getPropertyErrors()).containsOnly();
		assertThat(reader.getMissingEnums()).containsOnly();
		assertThat(reader.getInvalidViewClasses()).containsOnly();
		assertThat(reader.getUndeclaredViewClasses()).containsOnly();
		assertThat(reader.getIndentationErrors()).containsOnly();
		// assertThat(reader.getSitemap().hasErrors()).isFalse();

		System.out.println(reader.getReport());

	}

	@Test
	public void invalidSectionName() throws IOException {

		// given
		substitute("[options]", "[option]");
		prepFile();
		// when
		reader.parse(modifiedFile);
		// then

		assertThat(reader.missingSections()).containsOnly("options");

		assertThat(reader.isLabelClassNonExistent()).isFalse();
		assertThat(reader.isLabelClassNotI18N()).isFalse();
		assertThat(reader.getPagesDefined()).isEqualTo(0);
		assertThat(reader.getMissingPages()).containsOnly();
		assertThat(reader.getPropertyErrors()).containsOnly();
		assertThat(reader.getMissingEnums()).containsOnly();
		assertThat(reader.getInvalidViewClasses()).containsOnly();
		assertThat(reader.getUndeclaredViewClasses()).containsOnly();
		assertThat(reader.getIndentationErrors()).containsOnly();
		assertThat(reader.getSitemap().hasErrors()).isTrue();

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
		substitute("labelKeys=uk.co.q3c.v7.i18n.TestLabelKeys", "labelKeys=uk.co.q3c.v7.i18n.TestLabelKeys_Invalid");
		prepFile();
		// when
		reader.parse(modifiedFile);
		// then

		assertThat(reader.isLabelClassMissing()).isFalse();
		assertThat(reader.isLabelClassNonExistent()).isFalse();
		assertThat(reader.isLabelClassNotI18N()).isTrue();
		assertThat(reader.missingSections()).containsOnly();

		assertThat(reader.getPagesDefined()).isEqualTo(PAGE_COUNT);
		assertThat(reader.getViewPackages()).containsOnly("fixture.testviews2", "uk.co.q3c.v7.base.view.testviews");
		assertThat(reader.getMissingPages()).containsOnly();
		assertThat(reader.getPropertyErrors()).containsOnly();
		assertThat(reader.getMissingEnums()).contains("MoneyInOut", "Transfers", "Opt");
		assertThat(reader.getInvalidViewClasses()).containsOnly();
		assertThat(reader.getUndeclaredViewClasses()).containsOnly();
		assertThat(reader.getIndentationErrors()).containsOnly();
		assertThat(reader.getSitemap().hasErrors()).isTrue();
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
		substitute("labelKeys=uk.co.q3c.v7.i18n.TestLabelKeys", "labelKeys=uk.co.q3c.v7.i18n.TestLabelKeys2");
		prepFile();
		// when
		reader.parse(modifiedFile);
		// then
		assertThat(reader.isLabelClassMissing()).isFalse();
		assertThat(reader.isLabelClassNonExistent()).isTrue();
		assertThat(reader.isLabelClassNotI18N()).isTrue();
		assertThat(reader.missingSections()).containsOnly();

		assertThat(reader.getPagesDefined()).isEqualTo(PAGE_COUNT);
		assertThat(reader.getViewPackages()).containsOnly("fixture.testviews2", "uk.co.q3c.v7.base.view.testviews");
		assertThat(reader.getMissingPages()).containsOnly();
		assertThat(reader.getPropertyErrors()).containsOnly();
		// Counting precisely is irrelevant as LabelKeys class missing
		assertThat(reader.getMissingEnums().size()).isGreaterThan(0);
		assertThat(reader.getInvalidViewClasses()).containsOnly();
		assertThat(reader.getUndeclaredViewClasses()).containsOnly();
		assertThat(reader.getIndentationErrors()).containsOnly();
		assertThat(reader.getSitemap().hasErrors()).isTrue();

		System.out.println(reader.getReport());

	}

	@Test
	public void viewNotFound() throws IOException {

		substitute("--transfers     : subview.Transfer", "--transfers     : subview.Transfers");
		prepFile();
		outputModifiedFile();
		// when
		reader.parse(modifiedFile);
		// then

		assertThat(reader.isLabelClassNonExistent()).isFalse();
		assertThat(reader.isLabelClassNotI18N()).isFalse();
		assertThat(reader.missingSections()).containsOnly();

		assertThat(reader.getPagesDefined()).isEqualTo(PAGE_COUNT);
		assertThat(reader.getViewPackages()).containsOnly("fixture.testviews2", "uk.co.q3c.v7.base.view.testviews");
		assertThat(reader.getMissingPages()).containsOnly();
		assertThat(reader.getPropertyErrors()).containsOnly();
		assertThat(reader.getMissingEnums()).containsOnly();
		assertThat(reader.getInvalidViewClasses()).containsOnly();
		assertThat(reader.getUndeclaredViewClasses()).containsOnly("subview.TransfersView");
		assertThat(reader.getIndentationErrors()).containsOnly();
		assertThat(reader.getSitemap().hasErrors()).isTrue();
		System.out.println(reader.getReport());

	}

	@Test
	public void viewNotV7View() throws IOException {

		substitute("--money-in-out  : subview.MoneyInOut      ~ MoneyInOut",
				"--money-in-out : subview.NotV7 ~ MoneyInOut");
		prepFile();
		// when
		reader.parse(modifiedFile);

		// then
		assertThat(reader.isLabelClassNonExistent()).isFalse();
		assertThat(reader.isLabelClassNotI18N()).isFalse();
		assertThat(reader.missingSections()).containsOnly();

		assertThat(reader.getPagesDefined()).isEqualTo(PAGE_COUNT);
		assertThat(reader.getViewPackages()).containsOnly("fixture.testviews2", "uk.co.q3c.v7.base.view.testviews");
		assertThat(reader.getMissingPages()).containsOnly();
		assertThat(reader.getPropertyErrors()).containsOnly();
		assertThat(reader.getMissingEnums()).containsOnly();
		assertThat(reader.getInvalidViewClasses()).containsOnly(NotV7View.class.getName());
		assertThat(reader.getUndeclaredViewClasses()).containsOnly();
		assertThat(reader.getIndentationErrors()).containsOnly();
		assertThat(reader.getSitemap().hasErrors()).isTrue();
		System.out.println(reader.getReport());

	}

	/**
	 * Tries to go out of structure by double indenting from previous
	 * 
	 * @throws IOException
	 */
	@Test
	public void mapIndentTooGreat() throws IOException {

		substitute("--transfers     : subview.Transfer", "----transfers     : subview.Transfer");
		prepFile();
		// when
		reader.parse(modifiedFile);

		// then
		assertThat(reader.isLabelClassNonExistent()).isFalse();
		assertThat(reader.isLabelClassNotI18N()).isFalse();
		assertThat(reader.missingSections()).containsOnly();

		assertThat(reader.getPagesDefined()).isEqualTo(PAGE_COUNT);
		assertThat(reader.getViewPackages()).containsOnly("fixture.testviews2", "uk.co.q3c.v7.base.view.testviews");
		assertThat(reader.getMissingPages()).containsOnly();
		assertThat(reader.getPropertyErrors()).containsOnly();
		assertThat(reader.getMissingEnums()).containsOnly();
		assertThat(reader.getInvalidViewClasses()).containsOnly();
		assertThat(reader.getUndeclaredViewClasses()).containsOnly();
		assertThat(reader.getIndentationErrors()).containsOnly("transfers");
		assertThat(reader.getSitemap().hasErrors()).isFalse();

		System.out.println(reader.getReport());
		System.out.println(reader.getSitemap().toString());
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
	public void redirectTargetNotADefinedPage() throws IOException {

		// given
		insertAfter("[redirects]", "wiggly : wiggly/home");
		prepFile();
		// when
		reader.parse(modifiedFile);
		// then
		assertThat(reader.getRedirectErrors()).containsOnly(
				"'wiggly/home' cannot be a redirect target, it has not been defined as a page");
		assertThat(reader.getSitemap().hasErrors()).isTrue();

	}

	/**
	 * 
	 * Also tests emptyUri
	 * 
	 * @throws IOException
	 */
	@Test
	public void redirectTargetEmptyButValid() throws IOException {

		// given
		// make empty segment a valid page
		insertAfter("[map]", "-   :  WigglyHome ~Home");
		// redirect to empty
		substitute("       : public", "wiggly  :   ");

		prepFile();
		// when
		reader.parse(modifiedFile);
		System.out.println(reader.getSitemap().toString());
		// then

		assertThat(reader.getSitemap().uris()).contains("");
		assertThat(reader.getRedirectErrors()).containsOnly();
		System.out.println(reader.getSitemap().getReport());
		assertThat(reader.getSitemap().hasErrors()).isFalse();

	}

	@Test
	public void options() throws IOException {

		// given
		prepFile();
		// when
		reader.parse(modifiedFile);
		// then
		assertThat(reader.isAppendView()).isTrue();
		assertThat(reader.isGeneratePublicHomePage()).isTrue();
		assertThat(reader.isGenerateAuthenticationPages()).isTrue();
		assertThat(reader.isGenerateRequestAccount()).isTrue();
		assertThat(reader.isGenerateRequestAccountReset()).isTrue();
		assertThat(reader.getLabelKeys()).isEqualTo("uk.co.q3c.v7.i18n.TestLabelKeys");
		assertThat(reader.getSystemAccountUri()).isEqualTo("public/system-account");

		// given properties not defined
		deleteLine("appendView=true");
		deleteLine("generatePublicHomePage=true");
		deleteLine("generateAuthenticationPages=true");
		deleteLine("generateRequestAccount=true");
		deleteLine("generateRequestAccountReset=true");
		deleteLine("systemAccountUri=public/system-account");
		prepFile();

		// when
		reader.parse(modifiedFile);
		// then defaults correct
		assertThat(reader.isAppendView()).isTrue();
		assertThat(reader.isGeneratePublicHomePage()).isTrue();
		assertThat(reader.isGenerateAuthenticationPages()).isTrue();
		assertThat(reader.isGenerateRequestAccount()).isTrue();
		assertThat(reader.isGenerateRequestAccountReset()).isTrue();
		assertThat(reader.getSystemAccountUri()).isEqualTo("public/system-account");

	}

	@Test
	public void options_to_non_default() throws IOException {

		// given properties set to non-default
		substitute("appendView=true", "appendView=false");
		substitute("generatePublicHomePage=true", "generatePublicHomePage=false");
		// anything except 'true' is false
		substitute("generateAuthenticationPages=true", "generateAuthenticationPages=tru");
		substitute("generateRequestAccount=true", "generateRequestAccount=false");
		substitute("generateRequestAccountReset=true", "generateRequestAccountReset=false");
		substitute("systemAccountUri=public/system-account", "systemAccountUri=public/sysaccount");
		prepFile();
		// when
		reader.parse(modifiedFile);
		// then values correct
		assertThat(reader.isAppendView()).isFalse();
		assertThat(reader.isGeneratePublicHomePage()).isFalse();
		assertThat(reader.isGenerateAuthenticationPages()).isFalse();
		assertThat(reader.isGenerateRequestAccount()).isFalse();
		assertThat(reader.isGenerateRequestAccountReset()).isFalse();
		assertThat(reader.getSystemAccountUri()).isEqualTo("public/sysaccount");

	}

	@Test
	public void redirectTargetLoop() throws IOException {

		// given
		insertAfter("       : public", "public: ");
		prepFile();
		// when
		reader.parse(modifiedFile);

		// then
		assertThat(reader.getRedirectErrors()).contains("'' cannot be both a redirect source and redirect target");
		assertThat(reader.getSitemap().hasErrors()).isTrue();

	}

	@Test
	public void systemAccountUriChange() throws IOException {

		// given
		substitute("systemAccountUri=public/system-account", "systemAccountUri=public/sysaccount");
		prepFile();
		// when
		reader.parse(modifiedFile);
		// then
		// assertThat(reader.getSitemap().).isEqualTo(expected);
	}

	private void validateNode(Sitemap tree, SitemapNode node) {
		String uri = tree.uri(node);
		switch (uri) {

		case "public":
			assertThat(tree.getChildCount(node)).isEqualTo(3);
			assertThat(node.getUriSegment()).isEqualTo("public");
			assertThat(node.getViewClass()).isEqualTo(PublicHomeView.class);
			assertThat(node.getLabelKey()).isEqualTo(StandardPageKey.Public_Home);
			break;

		case "public/login": {
			assertThat(tree.getChildCount(node)).isEqualTo(0);
			assertThat(node.getUriSegment()).isEqualTo("login");
			assertThat(node.getViewClass()).isEqualTo(LoginView.class);
			assertThat(node.getLabelKey()).isEqualTo(StandardPageKey.Login);
			break;
		}

		case "public/logout": {
			assertThat(tree.getChildCount(node)).isEqualTo(0);
			assertThat(node.getUriSegment()).isEqualTo("logout");
			assertThat(node.getViewClass()).isEqualTo(LogoutView.class);
			assertThat(node.getLabelKey()).isEqualTo(StandardPageKey.Logout);
			break;
		}

		case "public/system-account": {
			assertThat(tree.getChildCount(node)).isEqualTo(5);
			assertThat(node.getUriSegment()).isEqualTo("system-account");
			assertThat(node.getViewClass()).isEqualTo(SystemAccountView.class);
			assertThat(node.getLabelKey()).isEqualTo(StandardPageKey.System_Account);
			break;
		}

		case "public/system-account/enable-account":
			assertThat(node.getUriSegment()).isEqualTo("enable-account");
			assertThat(node.getLabelKey()).isEqualTo(StandardPageKey.Enable_Account);
			assertThat(tree.getChildCount(node)).isEqualTo(0);
			assertThat(node.getViewClass()).isEqualTo(RequestSystemAccountEnableView.class);
			break;
		case "public/system-account/request-account":
			assertThat(node.getUriSegment()).isEqualTo("request-account");
			assertThat(node.getLabelKey()).isEqualTo(StandardPageKey.Request_Account);
			assertThat(tree.getChildCount(node)).isEqualTo(0);
			assertThat(node.getViewClass()).isEqualTo(RequestSystemAccountView.class);
			break;
		case "public/system-account/refresh-account":
			assertThat(node.getUriSegment()).isEqualTo("refresh-account");
			assertThat(node.getLabelKey()).isEqualTo(StandardPageKey.Refresh_Account);
			assertThat(tree.getChildCount(node)).isEqualTo(0);
			assertThat(node.getViewClass()).isEqualTo(RequestSystemAccountRefreshView.class);
			break;
		case "public/system-account/unlock-account":
			assertThat(node.getUriSegment()).isEqualTo("unlock-account");
			assertThat(node.getLabelKey()).isEqualTo(StandardPageKey.Unlock_Account);
			assertThat(tree.getChildCount(node)).isEqualTo(0);
			assertThat(node.getViewClass()).isEqualTo(RequestSystemAccountUnlockView.class);
			break;
		case "public/system-account/reset-account": {
			assertThat(node.getUriSegment()).isEqualTo("reset-account");
			assertThat(node.getLabelKey()).isEqualTo(StandardPageKey.Reset_Account);
			assertThat(tree.getChildCount(node)).isEqualTo(0);
			assertThat(node.getViewClass()).isEqualTo(RequestSystemAccountResetView.class);
			break;
		}

		case "secure":
			assertThat(tree.getChildCount(node)).isEqualTo(3);
			assertThat(node.getUriSegment()).isEqualTo("secure");
			assertThat(node.getViewClass()).isEqualTo(SecureHomeView.class);
			assertThat(node.getLabelKey()).isEqualTo(StandardPageKey.Secure_Home);
			break;

		case "secure/transfers":
			assertThat(tree.getChildCount(node)).isEqualTo(0);
			assertThat(node.getUriSegment()).isEqualTo("transfers");
			assertThat(node.getViewClass()).isEqualTo(TransferView.class);
			assertThat(node.getLabelKey()).isEqualTo(TestLabelKeys.Transfers);
			break;

		case "secure/money-in-out":
			assertThat(tree.getChildCount(node)).isEqualTo(0);
			assertThat(node.getUriSegment()).isEqualTo("money-in-out");
			assertThat(node.getViewClass()).isEqualTo(MoneyInOutView.class);
			assertThat(node.getLabelKey()).isEqualTo(TestLabelKeys.MoneyInOut);
			break;

		case "secure/options":
			assertThat(tree.getChildCount(node)).isEqualTo(0);
			assertThat(node.getUriSegment()).isEqualTo("options");
			assertThat(node.getViewClass()).isEqualTo(OptionsView.class);
			assertThat(node.getLabelKey()).isEqualTo(TestLabelKeys.Opt);
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

}
