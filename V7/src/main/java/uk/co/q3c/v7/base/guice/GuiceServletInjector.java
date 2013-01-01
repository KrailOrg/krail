package uk.co.q3c.v7.base.guice;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.guice.aop.ShiroAopModule;

import uk.co.q3c.v7.base.guice.threadscope.ThreadScopeModule;
import uk.co.q3c.v7.base.guice.uiscope.UIScopeModule;
import uk.co.q3c.v7.demo.shiro.DemoShiroWebModule;
import uk.co.q3c.v7.demo.view.DemoViewModule;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

public class GuiceServletInjector extends GuiceServletContextListener {
	private static Injector injector;

	private final ThreadLocal<ServletContext> ctx = new ThreadLocal<ServletContext>();

	@Override
	protected Injector getInjector() {

		injector = Guice.createInjector(new DemoShiroWebModule(ctx.get()), new ShiroAopModule(), new BaseModule(),
				new DemoViewModule(), new ThreadScopeModule(), new UIScopeModule());

		// injector = Guice.createInjector(new MyShiroWebModule(ctx.get()), new ShiroAopModule(), new BasicModule(),
		// new ViewModule(), new ThreadScopeModule(), new UIScopeModule());

		org.apache.shiro.mgt.SecurityManager securityManager = injector
				.getInstance(org.apache.shiro.mgt.SecurityManager.class);
		SecurityUtils.setSecurityManager(securityManager);

		return injector;
	}

	public static Injector injector() {
		return injector;
	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		final ServletContext servletContext = servletContextEvent.getServletContext();
		ctx.set(servletContext);
		super.contextInitialized(servletContextEvent);
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		// may need later for Quartz
		// try {
		// if (injector != null)
		// injector.getInstance(Scheduler.class).shutdown();
		// } catch (SchedulerException e) {
		// e.printStackTrace();
		// }
		// injector.getInstance(PersistService.class).stop();
		super.contextDestroyed(servletContextEvent);
		ctx.remove();
	}
}
