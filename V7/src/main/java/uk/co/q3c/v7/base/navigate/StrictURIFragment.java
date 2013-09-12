package uk.co.q3c.v7.base.navigate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.inject.Inject;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;

/**
 * 
 * This provides a more strict interpretation of the UriFragment than Vaadin
 * does by default. It requires that the URI structure is of the form:<br>
 * <br>
 * http://example.com/domain#!finance/report/risk/id=1223/year=2012 (<I>with or
 * without the bang after the hash, depending on the <code>useBang</code>
 * setting)</I> <br>
 * <br>
 * where: <br>
 * <br>
 * finance/report/risk/ <br>
 * <br>
 * is a "virtual page path" and is represented by a View <br>
 * <br>
 * and everything after it is paired parameters. If a segment within what should
 * be paired parameters is malformed, it is ignored, and when the URI is
 * reconstructed, will disappear. So for example: <br>
 * <br>
 * <code>http://example.com/domain#!finance/report/risk/id=1223/year2012 <br></code>
 * <br>
 * would be treated as: <br>
 * <br>
 * <code>http://example.com/domain#!finance/report/risk/id=1223</code><br>
 * The year parameter has been dropped because it has no "=" <br>
 * <br>
 * Optionally uses hash(#) or hashBang(#!). Some people get excited about
 * hashbangs. Try Googling it<br>
 * <br>
 * 
 */
public class StrictURIFragment implements URIFragment,
		Serializable {

	// cached fragment, if null some data has chanded and need do be re-encoded
	private String cachedUri;
	private String virtualPage;
	// linkedHashMap to mantain parameters order
	private final LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();
	private boolean useBang;

	public StrictURIFragment(String fragment) {
		setFragment(fragment);
	}

	private String decode(String fragment) {
		fragment = stripBangAndTrailingSlash(fragment);
		virtualPage = null;
		parameters.clear();

		// empty fragment is 'root'
		if (Strings.isNullOrEmpty(fragment)) {
			virtualPage = "";
			return "";
		}

		// no parameters, everything is the virtual page path
		if (!fragment.contains("=")) {
			virtualPage = fragment;
			return fragment;
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
					virtualPage += (virtualPage.length() != 0 ? "/" : "") + s;
				}
			}
		}
		
		return fragment;
	}

	private void invalidateCachedFragment() {
		this.cachedUri = null;
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
		// FIXME is always more performant calculate the substring even if is
		// exactly the original string than check for it ?
		String copy = path.substring(copyStart, copyEnd);
		return copy;
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

		cachedUri = buf.toString();
	}

	@Override
	public String getUri() {
		if (cachedUri == null) {
			encode();
		}
		return cachedUri;
	}
	
	@Override
	public void setFragment(String fragment) {
		this.cachedUri = decode(fragment);
	}

	@Override
	public boolean isUseBang() {
		return useBang;
	}

	@Override
	public void setUseBang(boolean useBang) {
		if (this.useBang != useBang) {
			this.useBang = useBang;
			invalidateCachedFragment();
		}
	}
	
	/**
	 * returns the "virtual page path" of the URIFragment. The path is assumed
	 * to finish as soon as a paired parameter is found. No attempt is made to
	 * validate the actual structure of the path, so for example something like
	 * <code>view//subview/a=b</code> will return <code>view//subview</code>. An
	 * empty String is returned if <code>navigationState</code> is null or
	 * empty. If <code>navigationState</code> contains only paired parameters,
	 * an empty String is returned.
	 * 
	 * @see uk.co.q3c.v7.base.navigate.URIFragment#virtualPage(java.lang.String)
	 */
	@Override
	public String getVirtualPage() {
		return virtualPage;
	}
	
	@Override
	public void setVirtualPage(String pagePath) {
		this.virtualPage = pagePath;
		invalidateCachedFragment();
	}

	private void addParameter(String s) {
		String[] ss = s.split("=");
		assert ss.length == 2;
		addParameter(ss[0],ss[1]);
	}
	
	private void addParameter(String key, String value) {
		invalidateCachedFragment();

		assert !Strings.isNullOrEmpty(key);
		assert !Strings.isNullOrEmpty(value);

		parameters.put(key, value);
	}
	
	@Override
	public String getParameterValue(String paramName) {
		return parameters.get(paramName);
	}

	@Override
	public URIFragment setParameterValue(String paramName, String value) {
		parameters.put(paramName, value);
		invalidateCachedFragment();
		return this;
	}

	@Override
	public URIFragment removeParameter(String paramName) {
		parameters.remove(paramName);
		invalidateCachedFragment();
		return this;
	}

	@Override
	public LinkedHashMap<String, String> getParameters() {
		return this.parameters;
	}
}
