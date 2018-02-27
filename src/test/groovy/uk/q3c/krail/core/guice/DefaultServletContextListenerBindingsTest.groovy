package uk.q3c.krail.core.guice

import com.google.inject.Injector
import com.google.inject.Key
import spock.lang.Specification
import uk.q3c.krail.core.i18n.I18NProcessor
import uk.q3c.krail.core.navigate.sitemap.MasterSitemap
import uk.q3c.krail.core.navigate.sitemap.UserSitemap
import uk.q3c.krail.core.option.OptionPopup
import uk.q3c.krail.core.option.hierarchy.SimpleUserHierarchy
import uk.q3c.krail.i18n.persist.PatternCacheLoader
import uk.q3c.krail.i18n.persist.PatternDao
import uk.q3c.krail.option.Option
import uk.q3c.krail.option.UserHierarchy
import uk.q3c.krail.option.UserHierarchyDefault
import uk.q3c.krail.option.persist.OptionCache
import uk.q3c.krail.option.persist.OptionDao
import uk.q3c.krail.option.persist.OptionDaoDelegate
import uk.q3c.krail.persist.InMemory
import uk.q3c.krail.testutil.guice.TestServletContextListener

/**
 * This is not intended to be a comprehensive test, but just checks a sample of interfaces to ensure their bindings are included
 *
 * Created by David Sowerby on 22 Aug 2017
 */
class DefaultServletContextListenerBindingsTest extends Specification {

    def "bindings"() {
        given:
        DefaultServletContextListener manager = new TestServletContextListener()
        Key patternDaoKey = Key.get(PatternDao.class, InMemory)
        Key optionDaoDelegateKey = Key.get(OptionDaoDelegate.class, InMemory)
        Key userHierarchyKey = Key.get(UserHierarchy.class, UserHierarchyDefault)

        when:
        Injector injector = manager.getInjector()

        then:
        injector.getInstance(MasterSitemap.class) != null
        injector.getInstance(UserSitemap.class) != null
        injector.getInstance(Option.class) != null
        injector.getInstance(OptionCache.class) != null
        injector.getInstance(OptionPopup.class) != null
        injector.getInstance(I18NProcessor.class) != null
        injector.getInstance(PatternCacheLoader.class) != null
        injector.getInstance(patternDaoKey) != null
        injector.getInstance(optionDaoDelegateKey) != null
        injector.getInstance(OptionDao.class) != null
        injector.getInstance(OptionCache.class) != null
        injector.getInstance(userHierarchyKey) instanceof SimpleUserHierarchy


    }
}
