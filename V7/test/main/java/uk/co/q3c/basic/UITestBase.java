package uk.co.q3c.basic;

import static org.mockito.Mockito.*;

import javax.inject.Inject;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;

import uk.co.q3c.v7.A;
import uk.co.q3c.v7.base.guice.BaseModule;
import uk.co.q3c.v7.base.navigate.V7View;
import uk.co.q3c.v7.base.navigate.V7ViewChangeEvent;
import uk.co.q3c.v7.base.navigate.V7ViewChangeListener;
import uk.co.q3c.v7.demo.ui.BasicUI;

import com.google.inject.name.Named;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;
import com.vaadin.util.CurrentInstance;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ BaseModule.class })
public abstract class UITestBase implements V7ViewChangeListener {

	@Inject
	@Named(A.baseUri)
	String baseUri;

	VaadinRequest mockedRequest = mock(VaadinRequest.class);

	V7View currentView;

	@Inject
	protected BasicUI ui;

	@Before
	public void uiSetup() {
		// VaadinRequest vr = new VaadinServletRequest(null, null);
		System.out.println("initialising test");
		CurrentInstance.set(UI.class, ui);
		when(mockedRequest.getParameter("v-loc")).thenReturn(baseUri + "/");
		ui.doInit(mockedRequest, 1);
		ui.getGuiceNavigator().addViewChangeListener(this);

	}

	@After
	public void teardown() {
		currentView = null;
	}

	@Override
	public boolean beforeViewChange(V7ViewChangeEvent event) {
		return true;
	}

	@Override
	public void afterViewChange(V7ViewChangeEvent event) {
		currentView = event.getNewView();
	}

}
