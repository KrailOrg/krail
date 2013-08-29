package uk.co.q3c.v7.base.view;

import uk.co.q3c.v7.base.shiro.Public;

/**
 * Interface for ErrorViews. Binding to implementation can be changed in {@link StandardViewModule}
 * 
 * @author david
 * 
 */
@Public
public interface ErrorView extends V7View {

	void setError(Throwable throwable);

}
