/*
 * Copyright (c) 2014 David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package uk.q3c.krail.base.navigate.sitemap;

import org.junit.Before;
import org.junit.Test;
import uk.q3c.krail.base.navigate.StandardPageMappingReader;

import static org.assertj.core.api.Assertions.assertThat;

public class DeconstructPageMappingTest {

    int lineNumber = 2;
    private StandardPageMappingReader dpm;

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
        assertThat(dpm.getSyntaxErrors()).containsOnly(StandardPageMappingReader.missingLabelKeyMsg + " at line " +
                lineNumber);

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
        assertThat(dpm.getSyntaxErrors()).containsOnly(StandardPageMappingReader.emptyLabelKeyMsg + " at line " +
                lineNumber);

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
        assertThat(dpm.getSyntaxErrors()).containsOnly(StandardPageMappingReader.missingUriMsg + " at line " +
                lineNumber);
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
        assertThat(dpm.getSyntaxErrors()).containsOnly(StandardPageMappingReader.emptyStandardPageKey + " at line " + lineNumber);
    }
}
