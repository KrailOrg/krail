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

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;
import fixture.MockCurrentLocale;
import fixture.testviews2.ViewA;
import fixture.testviews2.ViewA1;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.co.q3c.v7.base.guice.vsscope.VaadinSessionScopeModule;
import uk.co.q3c.v7.base.navigate.StrictURIFragmentHandler;
import uk.co.q3c.v7.base.navigate.URIFragmentHandler;
import uk.co.q3c.v7.base.shiro.PageAccessControl;
import uk.co.q3c.v7.base.user.opt.DefaultUserOption;
import uk.co.q3c.v7.base.user.opt.DefaultUserOptionStore;
import uk.co.q3c.v7.base.user.opt.UserOption;
import uk.co.q3c.v7.base.user.opt.UserOptionStore;
import uk.co.q3c.v7.i18n.CurrentLocale;
import uk.co.q3c.v7.i18n.MapTranslate;
import uk.co.q3c.v7.i18n.TestLabelKey;
import uk.co.q3c.v7.i18n.Translate;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({VaadinSessionScopeModule.class})
public class DefaultSitemapCheckerTest {

    String uriNodeNoClass = "node/noclass";
    String uriNodeNoKey = "node/nokey";

    String uripublic_Node1 = "public/node1";
    String uripublic_Node11 = "public/node1/1";

    @Inject
    DefaultSitemapChecker checker;

    @Inject
    Translate translate;

    @Inject
    DefaultMasterSitemap sitemap;
    CurrentLocale currentLocale = new MockCurrentLocale();
    private MasterSitemapNode baseNode;
    private MasterSitemapNode node1;
    private MasterSitemapNode node11;
    private MasterSitemapNode nodeNoClass;
    private MasterSitemapNode nodeNoKey;

    @Test(expected = SitemapException.class)
    public void checkOnly() {

        // given
        buildSitemap(0);
        // when
        checker.check();
        // then

    }

    /**
     * the root node "node" will have nothing set except the segment
     *
     * @param index
     */
    private void buildSitemap(int index) {
        switch (index) {
            case 0:

                nodeNoClass = sitemap.append(uriNodeNoClass);
                nodeNoClass.setLabelKey(TestLabelKey.No);
                nodeNoClass.setPageAccessControl(PageAccessControl.PUBLIC);
                nodeNoKey = sitemap.append(uriNodeNoKey);
                nodeNoKey.setViewClass(ViewA1.class);
                nodeNoKey.setPageAccessControl(PageAccessControl.PUBLIC);
                baseNode = sitemap.nodeFor("node");
                baseNode.setPageAccessControl(PageAccessControl.PUBLIC);
                break;
            case 1:
                node1 = sitemap.append(uripublic_Node1);
                node1.setLabelKey(TestLabelKey.No);
                node1.setViewClass(ViewA1.class);
                node1.setPageAccessControl(PageAccessControl.PERMISSION);
                sitemap.addRedirect("public", uripublic_Node1);
            case 2:
                node1 = sitemap.append(uripublic_Node1);
                node1.setLabelKey(TestLabelKey.No);
                node1.setViewClass(ViewA1.class);

                node11 = sitemap.append(uripublic_Node11);
                node11.setLabelKey(TestLabelKey.No);
                node11.setViewClass(ViewA1.class);
                node11.setPageAccessControl(PageAccessControl.PERMISSION);

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
            checker.check();
        } catch (SitemapException se) {
            report = checker.getReport()
                            .toString();
        }
        // then
        assertThat(report).contains("node/noclass");
        assertThat(report).contains("node/nokey");
        assertThat(report).contains("node/n");
    }

    @Test
    public void redirect() {

        // given
        buildSitemap(1);
        MasterSitemapNode publicNode = sitemap.nodeFor("public");
        publicNode.setLabelKey(TestLabelKey.Home);
        // when
        checker.check();
        // then

        assertThat(publicNode).isNotNull();
        assertThat(publicNode.getPageAccessControl()).isEqualTo(PageAccessControl.PERMISSION);
    }

    @Test
    public void redirect_multiLevel() {

        // given
        buildSitemap(2);
        MasterSitemapNode publicNode = sitemap.nodeFor("public");
        publicNode.setLabelKey(TestLabelKey.Public);
        // when
        checker.check();
        // then

        MasterSitemapNode n1 = sitemap.nodeFor(uripublic_Node1);
        MasterSitemapNode n11 = sitemap.nodeFor(uripublic_Node11);

        assertThat(publicNode).isNotNull();
        assertThat(publicNode.getPageAccessControl()).isEqualTo(PageAccessControl.PERMISSION);

        assertThat(n1).isNotNull();
        assertThat(n1.getPageAccessControl()).isEqualTo(PageAccessControl.PERMISSION);

        assertThat(n11).isNotNull();
        assertThat(n11.getPageAccessControl()).isEqualTo(PageAccessControl.PERMISSION);

    }

    @Test(expected = SitemapException.class)
    public void replaceMissingViews() {

        // given
        buildSitemap(0);
        // when
        checker.replaceMissingViewWith(ViewA.class)
               .check();
        // then

    }

    @Test(expected = SitemapException.class)
    public void replaceMissingKeys() {

        // given
        buildSitemap(0);
        // when
        checker.replaceMissingKeyWith(TestLabelKey.Home)
               .check();
        // then

    }

    @Test
    public void replaceMissingViewsAndKeys() {

        // given
        buildSitemap(0);
        // when
        checker.replaceMissingViewWith(ViewA.class)
               .replaceMissingKeyWith(TestLabelKey.Home)
               .check();
        // then
        assertThat(checker.getMissingLabelKeys()).isEmpty();
        assertThat(checker.getMissingViewClasses()).isEmpty();
        assertThat(checker.getMissingPageAccessControl()).isEmpty();
        assertThat(baseNode.getLabelKey()).isEqualTo(TestLabelKey.Home);
        assertThat(baseNode.getViewClass()).isEqualTo(ViewA.class);
        assertThat(nodeNoClass.getLabelKey()).isEqualTo(TestLabelKey.No);
        assertThat(nodeNoClass.getViewClass()).isEqualTo(ViewA.class);
        assertThat(nodeNoKey.getLabelKey()).isEqualTo(TestLabelKey.Home);
        assertThat(nodeNoKey.getViewClass()).isEqualTo(ViewA1.class);
    }

    @Test(expected = SitemapException.class)
    public void redirectLoop_immediate() {

        // given

        sitemap.addRedirect("p/1", "p/2");
        sitemap.addRedirect("p/2", "p/1");

        // when
        checker.check();
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
        checker.check();
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
        checker.check();
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
                bind(UserOption.class).to(DefaultUserOption.class);
                bind(UserOptionStore.class).to(DefaultUserOptionStore.class);
                bind(Translate.class).to(MapTranslate.class);
                bind(CurrentLocale.class).toInstance(currentLocale);
            }

        };
    }

}
