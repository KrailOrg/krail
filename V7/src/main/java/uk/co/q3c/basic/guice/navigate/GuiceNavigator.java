package uk.co.q3c.basic.guice.navigate;


public interface GuiceNavigator {

	void navigateTo(String navigationState);

	String getState();

}
