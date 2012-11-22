package basic;

import static org.mockito.Mockito.*;

import javax.inject.Inject;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;

import com.google.inject.name.Named;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ BasicModule.class })
public abstract class UITestBase implements ViewChangeListener {

	@Inject
	@Named(A.baseUri)
	String baseUri;

	VaadinRequest mockedRequest = mock(VaadinRequest.class);

	View currentView;

	@Inject
	protected UI ui;

	@Before
	public void uiSetup() {
		// VaadinRequest vr = new VaadinServletRequest(null, null);
		System.out.println("initialising test");
		when(mockedRequest.getParameter("loc")).thenReturn(baseUri);
		ui.doInit(mockedRequest, 1);
		ui.getNavigator().addViewChangeListener(this);
	}

	@After
	public void teardown() {
		currentView = null;
	}

	@Override
	public boolean beforeViewChange(ViewChangeEvent event) {
		return true;
	}

	@Override
	public void afterViewChange(ViewChangeEvent event) {
		currentView = event.getNewView();
	}

}
