package uk.co.q3c.basic;

import static org.mockito.Mockito.*;

import javax.inject.Inject;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;

import uk.co.q3c.basic.guice.navigate.GuiceView;
import uk.co.q3c.basic.guice.navigate.GuiceViewChangeEvent;
import uk.co.q3c.basic.guice.navigate.GuiceViewChangeListener;

import com.google.inject.name.Named;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;
import com.vaadin.util.CurrentInstance;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ BasicModule.class })
public abstract class UITestBase implements GuiceViewChangeListener {

	@Inject
	@Named(A.baseUri)
	String baseUri;

	VaadinRequest mockedRequest = mock(VaadinRequest.class);

	GuiceView currentView;

	@Inject
	protected BasicUI ui;

	@Before
	public void uiSetup() {
		// VaadinRequest vr = new VaadinServletRequest(null, null);
		System.out.println("initialising test");
		CurrentInstance.set(UI.class, ui);
		when(mockedRequest.getParameter("loc")).thenReturn(baseUri);
		ui.doInit(mockedRequest, 1);
		ui.getGuiceNavigator().addViewChangeListener(this);

	}

	@After
	public void teardown() {
		currentView = null;
	}

	@Override
	public boolean beforeViewChange(GuiceViewChangeEvent event) {
		return true;
	}

	@Override
	public void afterViewChange(GuiceViewChangeEvent event) {
		currentView = event.getNewView();
	}

}
