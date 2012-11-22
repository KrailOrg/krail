package uk.co.q3c.basic;

import java.util.List;

/**
 * Can use syntax of setNavigationState("xx/yy").virtualPage()
 * 
 * @author david
 * 
 */
public interface URIDecoder {

	public String virtualPage();

	public List<String> parameters();

	public URIDecoder setNavigationState(String navigationState);

}
