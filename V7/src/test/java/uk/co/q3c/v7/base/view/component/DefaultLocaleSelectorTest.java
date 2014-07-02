package uk.co.q3c.v7.base.view.component;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import uk.co.q3c.v7.base.guice.vsscope.VaadinSessionScopeModule;
import uk.co.q3c.v7.base.user.notify.UserNotifier;
import uk.co.q3c.v7.base.user.opt.UserOption;
import uk.co.q3c.v7.i18n.CurrentLocale;
import uk.co.q3c.v7.i18n.I18NModule;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.vaadin.server.VaadinService;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ I18NModule.class, VaadinSessionScopeModule.class })
public class DefaultLocaleSelectorTest {

	@Mock
	VaadinService vaadinService;

	@Inject
	CurrentLocale currentLocale;

	@Mock
	UserNotifier userNotifier;

	@Mock
	UserOption userOption;

	private DefaultLocaleSelector selector;

	@Before
	public void setup() {
		VaadinService.setCurrent(vaadinService);
		Set<Locale> supportedLocales = new HashSet<>();
		supportedLocales.add(Locale.UK);
		supportedLocales.add(Locale.GERMANY);

		LocaleContainer container = new LocaleContainer(supportedLocales, userOption);
		selector = new DefaultLocaleSelector(currentLocale, container, userNotifier);
	}

	@Test
	public void build() {

		// given

		// when

		// then
		assertThat(selector.selectedLocale()).isEqualTo(Locale.UK);
	}

	@Test
	public void localeChanged() {

		// given
		selector.setRespondToLocaleChange(true);
		// when
		selector.localeChanged(Locale.GERMANY);
		// then
		assertThat(selector.selectedLocale()).isEqualTo(Locale.GERMANY);
		// given
		selector.setRespondToLocaleChange(false);
		// when
		selector.localeChanged(Locale.UK);
		// then
		assertThat(selector.selectedLocale()).isEqualTo(Locale.GERMANY);
	}

}
