package basic;

import static org.mockito.Mockito.*;

import javax.inject.Inject;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;

import com.google.inject.name.Named;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ BasicModule.class })
public abstract class UITestBase {

	@Inject
	@Named(A.baseUri)
	String baseUri;

	VaadinRequest mockedRequest = mock(VaadinRequest.class);

	@Inject
	protected UI ui;

	@Before
	public void uiSetup() {
		// VaadinRequest vr = new VaadinServletRequest(null, null);
		System.out.println("initialising test");
		when(mockedRequest.getParameter("loc")).thenReturn(baseUri);
		ui.doInit(mockedRequest, 1);
	}

	@After
	public void teardown() {
		System.out.println("tearing down");
	}

}
