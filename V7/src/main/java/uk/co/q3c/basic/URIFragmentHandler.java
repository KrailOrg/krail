package uk.co.q3c.basic;

import java.util.List;

/**
 * Handles the decoding and encoding of a URI Fragment.
 * 
 * @author david
 * 
 */
public interface URIFragmentHandler {

	public String virtualPage();

	public URIFragmentHandler setFragment(String fragment);

	public String fragment();

	public List<String> parameterList();

	/**
	 * Sets the value of the specified parameter. If this parameter already exists, its value is updated, otherwise the
	 * parameter is added to the URI
	 * 
	 * @param paramName
	 * @param value
	 * @return
	 */
	public URIFragmentHandler setParameterValue(String paramName, String value);

	public URIFragmentHandler removeParameter(String paramName);

	void setVirtualPage(String pageName);

	String parameterValue(String paramName);

	/**
	 * 
	 * @return
	 */
	boolean isUseBang();

	/**
	 * If true, use "#!" (hashbang) after the base URI, if false use "#" (hash).
	 * 
	 * @param useBang
	 */
	void setUseBang(boolean useBang);

}
