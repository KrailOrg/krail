package uk.co.q3c.v7.base.navigate;

/**
 * Handles the decoding and encoding of a URI Fragment.
 * 
 * @author david
 * 
 */
public interface URIFragmentHandler {

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

	/**
	 * Returns a {@link NavigationState}, which is a representation of a URI fragment, broken down into its constituent
	 * parts
	 * 
	 * @return
	 */

	NavigationState navigationState(String fragment);

	/**
	 * Returns a URI fragment encoded from the {@code navigationState}
	 * 
	 * @param navigationState
	 * @return
	 */
	String fragment(NavigationState navigationState);

	/**
	 * Updates the fragment part of the {@code navigationState} from its ccomponent parts
	 * 
	 * @param navigationState
	 */
	void updateFragment(NavigationState navigationState);

}
