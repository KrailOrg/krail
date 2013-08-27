package uk.co.q3c.v7.base.config;

public class BaseIniProvider extends AbstractIniProvider<V7Ini>{

	@Override
	protected V7Ini createIni() {
		return new V7Ini();
	}

}
