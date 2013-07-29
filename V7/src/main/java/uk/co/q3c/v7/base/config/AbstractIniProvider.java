package uk.co.q3c.v7.base.config;

import com.google.inject.Provider;

public abstract class AbstractIniProvider<T extends V7Ini> implements Provider<T> {

	@Override
	public T get() {
		T ini = createIni();
		init(ini);
		return ini;
	}
	
	protected abstract T createIni();
	
	protected void init(T ini){
		// exceptions are all handled in the load method
		ini.load();
	}

}
