package uk.co.q3c.v7.base.config;

public class BaseIniProvider extends AbstractIniProvider<BaseIni>{

	@Override
	protected BaseIni createIni() {
		return new BaseIni();
	}

}
