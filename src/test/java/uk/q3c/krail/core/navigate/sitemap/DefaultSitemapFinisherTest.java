/*
 *
 *  * Copyright (c) 2016. David Sowerby
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 *  * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 *  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 *  * specific language governing permissions and limitations under the License.
 *
 */
package uk.q3c.krail.core.navigate.sitemap;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;
import fixture.testviews2.ViewA;
import fixture.testviews2.ViewA1;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.q3c.krail.core.eventbus.EventBusModule;
import uk.q3c.krail.core.guice.uiscope.UIScopeModule;
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScopeModule;
import uk.q3c.krail.core.navigate.StrictURIFragmentHandler;
import uk.q3c.krail.core.navigate.URIFragmentHandler;
import uk.q3c.krail.core.shiro.PageAccessControl;
import uk.q3c.krail.i18n.TestLabelKey;
import uk.q3c.krail.testutil.TestI18NModule;
import uk.q3c.krail.testutil.TestOptionModule;
import uk.q3c.krail.testutil.TestPersistenceModule;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({VaadinSessionScopeModule.class, TestPersistenceModule.class, TestI18NModule.class, TestOptionModule.class, EventBusModule.class, UIScopeModule
        .class})
public class DefaultSitemapFinisherTest {



    String uripublic_Node1 = "public/node1";
    String uripublic_Node11 = "public/node1/1";

    @Inject
    DefaultSitemapFinisher checker;

    @Inject
    DefaultMasterSitemap sitemap;

    private MasterSitemapNode baseNode;
    private MasterSitemapNode nodeNoClass;
    private MasterSitemapNode nodeNoKey;


    @Test(expected = SitemapException.class)
    public void checkOnly() {

        // given
        buildSitemap(0);
        // when
        checker.check(sitemap);
        // then

    }

    /**
     * the root node "node" will have nothing set except the segment
     *
     * @param index which build to use
     */
    private void buildSitemap(int index) {
        switch (index) {
            case 0:
                NodeRecord nr = new NodeRecord("undefined1");
                nr.setLabelKey(TestLabelKey.No);
                nr.setPageAccessControl(PageAccessControl.PUBLIC);
                nodeNoClass = sitemap.append(nr);

                nr = new NodeRecord("undefined2");
                nr.setViewClass(ViewA1.class);
                nr.setPageAccessControl(PageAccessControl.PUBLIC);
                nodeNoKey = sitemap.append(nr);

                nr = new NodeRecord("node");
                nr.setPageAccessControl(PageAccessControl.PUBLIC);
                baseNode = sitemap.append(nr);
                break;
            case 1:
                nr = new NodeRecord(uripublic_Node1);
                nr.setLabelKey(TestLabelKey.No);
                nr.setViewClass(ViewA1.class);
                nr.setPageAccessControl(PageAccessControl.PERMISSION);
                sitemap.addRedirect("public", uripublic_Node1);
                sitemap.append(nr);
            case 2:
                nr = new NodeRecord(uripublic_Node1);
                nr.setLabelKey(TestLabelKey.No);
                nr.setViewClass(ViewA1.class);
                sitemap.append(nr);

                nr = new NodeRecord(uripublic_Node11);
                nr.setLabelKey(TestLabelKey.No);
                nr.setViewClass(ViewA1.class);
                nr.setPageAccessControl(PageAccessControl.PERMISSION);
                sitemap.append(nr);

                sitemap.addRedirect("public", uripublic_Node1);
                sitemap.addRedirect(uripublic_Node1, uripublic_Node11);

        }
    }

    public void checkOnly_report() {

        // given
        buildSitemap(0);
        // when
        String report = null;
        try {
            checker.check(sitemap);
        } catch (SitemapException se) {
            report = checker.getReport()
                            .toString();
        }
        // then
        assertThat(report).contains("node/noclass");
        assertThat(report).contains("node/nokey");
        assertThat(report).contains("node/n");
    }



    @Test(expected = SitemapException.class)
    public void replaceMissingViews() {

        // given
        buildSitemap(0);
        // when
        checker.replaceMissingViewWith(ViewA.class)
               .check(sitemap);
        // then

    }

    @Test(expected = SitemapException.class)
    public void replaceMissingKeys() {

        // given
        buildSitemap(0);
        // when
        checker.replaceMissingKeyWith(TestLabelKey.Home)
               .check(sitemap);
        // then

    }

    @Test
    public void replaceMissingViewsAndKeys() {

        // given
        buildSitemap(0);
        // when
        checker.replaceMissingViewWith(ViewA.class)
               .replaceMissingKeyWith(TestLabelKey.Home)
               .check(sitemap);
        // then
        assertThat(checker.getMissingLabelKeys()).isEmpty();
        assertThat(checker.getMissingViewClasses()).isEmpty();
        assertThat(checker.getMissingPageAccessControl()).isEmpty();
        baseNode = sitemap.nodeFor("node");
        assertThat(baseNode.getLabelKey()).isEqualTo(TestLabelKey.Home);
        assertThat(baseNode.getViewClass()).isEqualTo(ViewA.class);
        nodeNoClass = sitemap.nodeFor("undefined1");
        assertThat(nodeNoClass.getLabelKey()).isEqualTo(TestLabelKey.No);
        assertThat(nodeNoClass.getViewClass()).isEqualTo(ViewA.class);
        nodeNoKey = sitemap.nodeFor("undefined2");
        assertThat(nodeNoKey.getLabelKey()).isEqualTo(TestLabelKey.Home);
        assertThat(nodeNoKey.getViewClass()).isEqualTo(ViewA1.class);
    }

    @Test(expected = SitemapException.class)
    public void redirectLoop_immediate() {

        // given

        sitemap.addRedirect("p/1", "p/2");
        sitemap.addRedirect("p/2", "p/1");

        // when
        checker.check(sitemap);
        // then

    }

    @Test(expected = SitemapException.class)
    public void redirectLoop_longloop() {

        // given

        sitemap.addRedirect("p/1", "p/2");
        sitemap.addRedirect("p/2", "p/3");
        sitemap.addRedirect("p/3", "p/4");
        sitemap.addRedirect("p/4", "p/1");
        sitemap.addRedirect("a/1", "a/2");
        sitemap.addRedirect("a/2", "a/1");

        // when
        checker.check(sitemap);
        // then

    }

    @Test(expected = SitemapException.class)
    public void redirectLoop_longloop_two_errors() {

        // given

        sitemap.addRedirect("p/1", "p/2");
        sitemap.addRedirect("p/2", "p/3");
        sitemap.addRedirect("p/3", "p/4");
        sitemap.addRedirect("p/4", "p/1");
        sitemap.addRedirect("p/3", "p/2");

        // when
        checker.check(sitemap);
        // then

    }

    @ModuleProvider
    protected AbstractModule moduleProvider() {
        return new AbstractModule() {

            @Override
            protected void configure() {
                bind(URIFragmentHandler.class).to(StrictURIFragmentHandler.class);
                bind(MasterSitemap.class).to(DefaultMasterSitemap.class);
                bind(UserSitemap.class).to(DefaultUserSitemap.class);
            }

        };
    }

}
