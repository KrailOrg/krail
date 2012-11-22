package uk.co.q3c.basic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.gwt.thirdparty.guava.common.base.Joiner;
import com.google.gwt.thirdparty.guava.common.base.Strings;

/**
 * This provides a more strict interpretation of the UriFragment than Vaadin does by default. It is required that the
 * URI structure is of the form:<br>
 * <br>
 * http://example.com/domain#!finance/report/risk/id=1223/year=2012 <br>
 * <br>
 * where: <br>
 * <br>
 * finance/report/risk/ <br>
 * <br>
 * is a "virtual page path" and is represented by a View <br>
 * <br>
 * and everything after it is paired parameters. Parameter pairs are not verified and may be malformed, the purpose of
 * this class is to split the parameters from the virtual page path, and the split occurs at the first path segment
 * containing "="
 */
public class StrictURIDecoder implements URIDecoder {

	private String navigationState;
	private String virtualPagePath;
	private List<String> parameters = new ArrayList<>();
	private final List<String> pathSegments = new ArrayList<>();

	@Inject
	protected StrictURIDecoder() {
		super();
	}

	/**
	 * returns the "virtual page path" of the URIFragment. The path is assumed to finish as soon as a paired parameter
	 * is found. No attempt is made to validate the actual structure of the path, so for example something like
	 * <code>view//subview/a=b</code> will return <code>view//subview</code>. An empty String is returned if
	 * <code>navigationState</code> is null or empty. If <code>navigationState</code> contains only paired parameters,
	 * an empty String is returned.
	 * 
	 * @see uk.co.q3c.basic.URIDecoder#virtualPage(java.lang.String)
	 */
	public void decode() {
		navigationState = stripTrailingSlash(navigationState);
		parameters.clear();
		pathSegments.clear();

		// empty navigation state
		if (Strings.isNullOrEmpty(navigationState)) {
			virtualPagePath = "";
			return;
		}

		// no parameters, everything is the virtual page path
		if (!navigationState.contains("=")) {
			virtualPagePath = navigationState;
		}

		Iterable<String> segments = Splitter.on('/').split(navigationState);
		boolean paramsStarted = false;
		Iterator<String> iter = segments.iterator();
		while (iter.hasNext()) {
			String s = iter.next();
			if (paramsStarted) {
				parameters.add(s);
			} else {
				if (s.contains("=")) {
					paramsStarted = true;
					parameters.add(s);
				} else {
					pathSegments.add(s);
				}
			}
		}

		// home page with parameters
		if (!segments.iterator().hasNext()) {
			virtualPagePath = "";
			parameters = Lists.newArrayList(segments);
			return;
		}

		// join the virtual page path up again
		// but keep the params as segments
		virtualPagePath = Joiner.on('/').join(pathSegments.toArray());

	}

	private String stripTrailingSlash(String path) {
		if (path.endsWith("/")) {
			return path.substring(0, path.length() - 1);
		} else {
			return path;
		}

	}

	@Override
	public List<String> parameters() {
		return parameters;
	}

	@Override
	public String virtualPage() {
		return virtualPagePath;
	}

	public String getNavigationState() {
		return navigationState;
	}

	@Override
	public URIDecoder setNavigationState(String navigationState) {
		this.navigationState = navigationState;
		decode();
		return this;
	}

}
