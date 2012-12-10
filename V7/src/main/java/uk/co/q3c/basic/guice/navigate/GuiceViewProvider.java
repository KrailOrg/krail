package uk.co.q3c.basic.guice.navigate;


public interface GuiceViewProvider {

	String getViewName(String viewAndParameters);

	GuiceView getView(String viewName);

}
