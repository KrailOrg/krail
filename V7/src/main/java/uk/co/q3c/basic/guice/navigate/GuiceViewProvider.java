package uk.co.q3c.basic.guice.navigate;

import uk.co.q3c.basic.view.ErrorView;

public interface GuiceViewProvider {

	/**
	 * Return the name of the view extracted from the <code>viewAndParameters</code>
	 * 
	 * @param viewAndParameters
	 * @return
	 */
	String getViewName(String viewAndParameters);

	/**
	 * Return the view for the given <code>viewName</code>, or the {@link ErrorView} if the viewName has not been
	 * registered as a <code>viewName</code>
	 * 
	 * @param viewName
	 * @return
	 */
	GuiceView getView(String viewName);

}
