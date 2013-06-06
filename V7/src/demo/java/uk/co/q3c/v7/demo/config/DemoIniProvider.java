package uk.co.q3c.v7.demo.config;


import uk.co.q3c.v7.base.config.BaseIniProvider;

public class DemoIniProvider extends BaseIniProvider<DemoIni> {
	
	@Override
	protected DemoIni createIni(){
		return new DemoIni();
	}
	
}
