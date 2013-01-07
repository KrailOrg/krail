package uk.co.q3c.v7.base.navigate;

import java.util.List;

import com.vaadin.server.Page.UriFragmentChangedListener;

public interface V7Navigator extends UriFragmentChangedListener {

	void navigateTo(String navigationState);

	String getNavigationState();

	List<String> geNavigationParams();

	void addViewChangeListener(V7ViewChangeListener listener);

	void removeViewChangeListener(V7ViewChangeListener listener);

	void loginOut();

	void returnAfterLogin();

}
