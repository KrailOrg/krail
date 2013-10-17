package uk.co.q3c.v7.base.guice.services;

import java.lang.reflect.InvocationTargetException;

import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

@Singleton
public class ServicesListener implements TypeListener {

	private final ServicesRegistry registry;
	private final Provider<ServicesManager> servicesHandlerProvider;

	public ServicesListener(Provider<ServicesManager> servicesHandlerProvider, ServicesRegistry registry) {
		this.servicesHandlerProvider = servicesHandlerProvider;
		this.registry = registry;
	}

	@Override
	public <I> void hear(final TypeLiteral<I> type, TypeEncounter<I> encounter) {
		registry.add(type.getRawType());
		encounter.register(new InjectionListener<Object>() {
			@Override
			public void afterInjection(Object injectee) {
				try {
					servicesHandlerProvider.get().startService(injectee);
				} catch (IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					throw new RuntimeException(e);
				}
			}
		});
	}

}
