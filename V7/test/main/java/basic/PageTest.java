package basic;

import static org.fest.assertions.Assertions.*;
import static org.mockito.Mockito.*;

import javax.inject.Inject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ BasicModule.class })
public class PageTest {

	String baseUri = "http://example.com";

	VaadinRequest mockedRequest = mock(VaadinRequest.class);

	@Inject
	UI ui;

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

	@Test
	public void initialPosition() {

		// given
		// when

		// then
		assertThat(ui).isNotNull();
		assertThat(ui.getPage().getLocation().toString()).isEqualTo("http://example.com#!view1");

	}

	@Test
	public void changePage() {

		// given
		// when
		ui.getNavigator().navigateTo("view2");
		// then
		assertThat(ui.getPage().getLocation().toString()).isEqualTo("http://example.com#!view2");

	}
}
