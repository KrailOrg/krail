package uk.co.q3c.basic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.inject.Inject;

import uk.co.q3c.basic.guice.uiscope.UIScoped;

import com.google.common.base.Splitter;
import com.google.gwt.thirdparty.guava.common.base.Joiner;
import com.google.gwt.thirdparty.guava.common.base.Strings;

/**
 * 
 * This provides a more strict interpretation of the UriFragment than Vaadin does by default. It requires that the URI
 * structure is of the form:<br>
 * <br>
 * http://example.com/domain#!finance/report/risk/id=1223/year=2012 (<I>with or without the bang after the hash,
 * depending on the <code>useBang</code> setting)</I> <br>
 * <br>
 * where: <br>
 * <br>
 * finance/report/risk/ <br>
 * <br>
 * is a "virtual page path" and is represented by a View <br>
 * <br>
 * and everything after it is paired parameters. If a segment within what should be paired parameters is malformed, it
 * is ignored, and when the URI is reconstructed, will disappear. So for example: <br>
 * <br>
 * <code>http://example.com/domain#!finance/report/risk/id=1223/year2012 <br></code> <br>
 * would be treated as: <br>
 * <br>
 * <code>http://example.com/domain#!finance/report/risk/id=1223</code><br>
 * The year parameter has been dropped because it has no "=" <br>
 * <br>
 * Optionally uses hash(#) or hashBang(#!). Some people get excited about hashbangs. Try Googling it<br>
 * <br>
 * Holds the current navigation state for the UI, so it is UIScoped
 * 
 */
@UIScoped
public class StrictURIFragmentHandler implements URIFragmentHandler, Serializable {

	private String fragment;
	private String virtualPage;
	private final Map<String, String> parameters = new TreeMap<String, String>();
	private final List<String> pathSegments = new ArrayList<>();
	private boolean useBang;
	// fragment is out of date
	private boolean dirty;

	@Inject
	protected StrictURIFragmentHandler() {
		super();
	}

	/**
	 * returns the "virtual page path" of the URIFragment. The path is assumed to finish as soon as a paired parameter
	 * is found. No attempt is made to validate the actual structure of the path, so for example something like
	 * <code>view//subview/a=b</code> will return <code>view//subview</code>. An empty String is returned if
	 * <code>navigationState</code> is null or empty. If <code>navigationState</code> contains only paired parameters,
	 * an empty String is returned.
	 * 
	 * @see uk.co.q3c.basic.URIFragmentHandler#virtualPage(java.lang.String)
	 */
	private void decode() {
		fragment = stripBangAndTrailingSlash(fragment);
		parameters.clear();
		pathSegments.clear();

		// empty fragment is 'home'
		if (Strings.isNullOrEmpty(fragment)) {
			virtualPage = "";
			return;
		}

		// no parameters, everything is the virtual page path
		if (!fragment.contains("=")) {
			virtualPage = fragment;
			return;
		}

		Iterable<String> segments = Splitter.on('/').split(fragment);
		boolean paramsStarted = false;
		Iterator<String> iter = segments.iterator();
		while (iter.hasNext()) {
			String s = iter.next();
			if (paramsStarted) {
				addParameter(s);
			} else {
				if (s.contains("=")) {
					paramsStarted = true;
					addParameter(s);
				} else {
					pathSegments.add(s);
				}
			}
		}

		// join the virtual page path up again
		virtualPage = Joiner.on('/').join(pathSegments.toArray());

	}

	private void addParameter(String s) {
		if (s.contains("=")) {
			Iterable<String> segments = Splitter.on('=').split(s);
			Iterator<String> iter = segments.iterator();
			String key = iter.next();
			String value = iter.next();
			if (Strings.isNullOrEmpty(key)) {
				return;
			}
			if (Strings.isNullOrEmpty(value)) {
				return;
			}
			parameters.put(key, value);
		}
	}

	private String stripBangAndTrailingSlash(String path) {
		int copyStart = 0;
		int copyEnd = path.length();

		if (path.startsWith("!")) {
			copyStart++;
		}

		if (path.endsWith("/")) {
			copyEnd--;
		}
		String copy = path.substring(copyStart, copyEnd);
		return copy;

	}

	@Override
	public String virtualPage() {
		return virtualPage;
	}

	@Override
	public String fragment() {
		if (dirty) {
			encode();
		}
		return fragment;
	}

	private void encode() {
		StringBuilder buf = new StringBuilder();
		if (useBang) {
			buf.append("!");
		}
		buf.append(virtualPage);

		// append the parameters
		for (Map.Entry<String, String> entry : parameters.entrySet()) {
			buf.append("/");
			buf.append(entry.getKey());
			buf.append("=");
			buf.append(entry.getValue());
		}

		fragment = buf.toString();
	}

	@Override
	public URIFragmentHandler setFragment(String navigationState) {
		this.fragment = navigationState;
		decode();
		return this;
	}

	@Override
	public List<String> parameterList() {
		List<String> list = new ArrayList<String>();
		for (Map.Entry<String, String> entry : parameters.entrySet()) {
			list.add(entry.getKey() + "=" + entry.getValue());
		}
		return list;
	}

	@Override
	public boolean isUseBang() {
		return useBang;
	}

	@Override
	public void setUseBang(boolean useBang) {
		if (this.useBang != useBang) {
			this.useBang = useBang;
			dirty = true;
		}
	}

	@Override
	public URIFragmentHandler setParameterValue(String paramName, String value) {
		parameters.put(paramName, value);
		dirty = true;
		return this;
	}

	@Override
	public URIFragmentHandler removeParameter(String paramName) {
		parameters.remove(paramName);
		dirty = true;
		return this;
	}

	@Override
	public void setVirtualPage(String pagePath) {
		this.virtualPage = pagePath;
		dirty = true;
	}

	@Override
	public String parameterValue(String paramName) {
		return parameters.get(paramName);
	}

}
