package uk.co.q3c.basic.guice.navigate;

import java.util.List;

import com.vaadin.server.Page.UriFragmentChangedListener;

public interface GuiceNavigator extends UriFragmentChangedListener {

	void navigateTo(String navigationState);

	String getNavigationState();

	List<String> geNavigationParams();

	void addViewChangeListener(GuiceViewChangeListener listener);

	void removeViewChangeListener(GuiceViewChangeListener listener);

}
