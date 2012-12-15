package uk.co.q3c.basic;

import java.util.List;

/**
 * Can use syntax of setNavigationState("xx/yy").virtualPage()
 * 
 * @author david
 * 
 */
public interface URIHandler {

	public String virtualPage();

	public List<String> parameters();

	public URIHandler setNavigationState(String navigationState);

	public String getNavigationState();

	public List<String> getNavigationParams();

}
