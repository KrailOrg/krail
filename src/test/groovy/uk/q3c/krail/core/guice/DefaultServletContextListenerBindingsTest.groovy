package uk.q3c.krail.core.guice

import com.google.inject.Guice
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
import uk.q3c.util.testutil.LogMonitor

import javax.servlet.ServletContextEvent

/**
 * This is not intended to be a comprehensive test, but just checks a sample of interfaces to ensure their bindings are included
 *
 * Created by David Sowerby on 22 Aug 2017
 */
class DefaultServletContextListenerBindingsTest extends Specification {

    def setup() {

    }

    def "bindings"() {
        given:
        Key patternDaoKey = Key.get(PatternDao.class, InMemory)
        Key optionDaoDelegateKey = Key.get(OptionDaoDelegate.class, InMemory)
        Key userHierarchyKey = Key.get(UserHierarchy.class, UserHierarchyDefault)

        when:
        Injector injector = Guice.createInjector(new CoreBindingsCollator(new ServletEnvironmentModule()).allModules())

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

    def "destroy context with null injector"() {
        given:
        LogMonitor logMonitor = new LogMonitor()
        logMonitor.addClassFilter(DefaultServletContextListener.class)
        ServletContextEvent servletContextEvent = Mock(ServletContextEvent)
        DefaultServletContextListener contextListener = new DefaultServletContextListener()
        InjectorHolder.injector = null

        when:
        contextListener.contextDestroyed(servletContextEvent)

        then:
        logMonitor.debugLogs().contains("Injector has not been constructed, no call made to stop service")
    }
}
