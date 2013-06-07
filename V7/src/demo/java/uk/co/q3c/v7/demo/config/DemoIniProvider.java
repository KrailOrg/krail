package uk.co.q3c.v7.demo.config;


import uk.co.q3c.v7.base.config.AbstractIniProvider;

public class DemoIniProvider extends AbstractIniProvider<DemoIni> {
	
	@Override
	protected DemoIni createIni(){
		return new DemoIni();
	}
	
}
