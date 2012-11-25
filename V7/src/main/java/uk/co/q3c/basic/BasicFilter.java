package uk.co.q3c.basic;

import static java.util.Arrays.*;
import static java.util.regex.Pattern.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import uk.co.q3c.basic.guice.uiscope.UIScopeModule;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceFilter;

public class BasicFilter extends GuiceFilter {

	private static Injector INJECTOR;

	public static final Pattern URI_ADMIN_PATTERN = compile("/_ah/.*");

	public static final Set<String> URI_NOADMIN_SET = new HashSet<String>(asList("/_ah/warmup"));

	public static Injector getInjector() {
		return INJECTOR;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		String uri = ((HttpServletRequest) request).getRequestURI();
		if (URI_ADMIN_PATTERN.matcher(uri).matches())
			if (!URI_NOADMIN_SET.contains(uri)) {
				chain.doFilter(request, response);
				return;

			}
		super.doFilter(request, response, chain);
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		if (INJECTOR != null) {
			throw new ServletException("Injector already created?!");
		}
		INJECTOR = Guice.createInjector(new BasicModule(), new UIScopeModule());
		filterConfig.getServletContext()
				.log("Created injector with " + INJECTOR.getAllBindings().size() + " bindings.");
		super.init(filterConfig);
	}

}
