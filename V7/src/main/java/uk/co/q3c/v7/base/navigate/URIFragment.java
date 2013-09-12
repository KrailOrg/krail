package uk.co.q3c.v7.base.navigate;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles the decoding and encoding of a URI Fragment.
 * 
 * @author david
 * 
 */
public interface URIFragment {
	/**
	 * returns the "virtual page path" of the URIFragment. The path is assumed to finish as soon as a paired parameter
	 * is found. No attempt is made to validate the actual structure of the path, so for example something like
	 * <code>view//subview/a=b</code> will return <code>view//subview</code>. An empty String is returned if
	 * <code>navigationState</code> is null or empty. If <code>navigationState</code> contains only paired parameters,
	 * an empty String is returned.
	 * 
	 * @see #getPathSegments()
	 */
	public String getVirtualPage();

	public void setFragment(String fragment);

	public String getUri();

	/**
	 * Sets the value of the specified parameter. If this parameter already exists, its value is updated, otherwise the
	 * parameter is added to the URI
	 * 
	 * @param paramName
	 * @param value
	 * @return
	 */
	public URIFragment setParameterValue(String paramName, String value);

	public URIFragment removeParameter(String paramName);

	public void setVirtualPage(String pageName);

	public String getParameterValue(String paramName);

	public boolean isUseBang();

	/**
	 * If true, use "#!" (hashbang) after the base URI, if false use "#" (hash).
	 * 
	 * @param useBang
	 */
	public void setUseBang(boolean useBang);

	public LinkedHashMap<String, String> getParameters();

}
