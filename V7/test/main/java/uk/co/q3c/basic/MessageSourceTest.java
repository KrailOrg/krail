package uk.co.q3c.basic;

import static org.fest.assertions.Assertions.*;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;

import uk.co.q3c.v7.demo.ui.MessageSource;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext()
public class MessageSourceTest {

	@Inject
	MessageSource messageSource;

	@Test
	public void messageSourceInjected() {

		// given

		// when

		// then
		assertThat(messageSource).isNotNull();

	}
}
