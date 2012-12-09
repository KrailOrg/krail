package uk.co.q3c.basic.view;


public interface GuiceViewProvider {

	String getViewName(String viewAndParameters);

	GuiceView getView(String viewName);

}
