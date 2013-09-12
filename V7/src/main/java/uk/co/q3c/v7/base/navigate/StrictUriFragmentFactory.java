package uk.co.q3c.v7.base.navigate;

public class StrictUriFragmentFactory implements UriFragmentFactory {

	@Override
	public URIFragment getUriFragment(String uri) {
		return new StrictURIFragment(uri);
	}
	
}
