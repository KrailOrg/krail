package uk.co.q3c.v7.base.navigate.sitemap;

import static org.assertj.core.api.Assertions.*;

import org.junit.Before;
import org.junit.Test;

import uk.co.q3c.v7.base.navigate.StandardPageMappingReader;
import uk.co.q3c.v7.base.navigate.sitemap.PageRecord;

public class DeconstructPageMappingTest {

	private StandardPageMappingReader dpm;
	int lineNumber = 2;

	@Before
	public void setup() {
		dpm = new StandardPageMappingReader();
	}

	@Test
	public void line_syntaxOk() {

		// given
		PageRecord pr;
		String line = "Public_Home=public  ~ Yes";
		// when
		pr = dpm.deconstruct(line, lineNumber);
		// then
		assertThat(pr.getStandardPageKeyName()).isEqualTo("Public_Home");
		assertThat(pr.getUri()).isEqualTo("public");
		assertThat(pr.getSegment()).isEqualTo("public");
		assertThat(pr.getLabelKeyName()).isEqualTo("Yes");
	}

	@Test
	public void line_missing_labelkey() {

		// given
		PageRecord pr;
		String line = "Public_Home=public  ";
		// when
		pr = dpm.deconstruct(line, lineNumber);
		// then
		assertThat(pr).isNull();
		assertThat(dpm.getSyntaxErrors()).containsOnly(
				StandardPageMappingReader.missingLabelKeyMsg + " at line " + lineNumber);

	}

	@Test
	public void line_empty_labelkey() {

		// given
		PageRecord pr;
		String line = "Public_Home=public  ~  ";
		// when
		pr = dpm.deconstruct(line, lineNumber);
		// then
		assertThat(pr).isNull();
		assertThat(dpm.getSyntaxErrors()).containsOnly(
				StandardPageMappingReader.emptyLabelKeyMsg + " at line " + lineNumber);

	}

	@Test
	public void line_missingUri() {

		// given
		PageRecord pr;
		String line = "Public_Home  ~ Yes";
		// when
		pr = dpm.deconstruct(line, lineNumber);
		// then
		assertThat(pr).isNull();
		assertThat(dpm.getSyntaxErrors()).containsOnly(StandardPageMappingReader.missingUriMsg + " at line " + lineNumber);
	}

	@Test
	public void line_empty_standardpagekey() {

		// given
		PageRecord pr;
		String line = " =public  ~ Yes";
		// when
		pr = dpm.deconstruct(line, lineNumber);
		// then
		assertThat(pr).isNull();
		assertThat(dpm.getSyntaxErrors()).containsOnly(
				StandardPageMappingReader.emptyStandardPageKey + " at line " + lineNumber);
	}
}
